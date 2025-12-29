package com.gzw.kd.controller;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.generator.RandomGenerator;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson2.JSONObject;
import com.gzw.kd.common.Constants;
import com.gzw.kd.common.annotation.OperatorLog;
import com.gzw.kd.common.annotation.RedisLockAnnotation;
import com.gzw.kd.common.entity.*;
import com.gzw.kd.common.enums.*;
import com.gzw.kd.common.exception.GlobalException;
import com.gzw.kd.common.R;
import com.gzw.kd.common.generators.RandomIdGenerator;
import com.gzw.kd.common.annotation.Resubmit;
import com.gzw.kd.common.init.FileInfoInit;
import com.gzw.kd.common.utils.*;
import com.gzw.kd.controller.es.OperatorLogIndex;
import com.gzw.kd.export.AsyncTaskLogService;
import com.gzw.kd.export.ExportFileMeta;
import com.gzw.kd.listener.event.MsgEvent;
import com.gzw.kd.mail.MailUtil;
import com.gzw.kd.scheduletask.ScheduleTask;
import com.gzw.kd.service.*;
import com.gzw.kd.vo.input.LogSearchInput;
import com.gzw.kd.vo.input.OperatorLogInput;
import com.gzw.kd.vo.output.AsyncTaskVo;
import com.gzw.kd.vo.output.EsLogSearchIndex;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.File;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import static com.gzw.kd.common.Constants.*;


/**
 * gzw
 */
@RequiredArgsConstructor
@Slf4j
@Api(tags = "首页")
@Controller
@RequestMapping("/pc")
public class PCController {


    @Value("${system.errorLogin.count:4}")
    private Integer errorCount;

    @Value("${system.defaultPassword:1234}")
    private String defaultPassword;


    @Value("${bio.uploadPath}")
    private  String basedir;


    @Value("${system.startEsQuery}")
    private  boolean startEsQuery;

    // =========== 服务依赖 ===========
    private final OperatorLogIndex operatorLogIndex;
    private final DocService docService;
    private final UserService userService;
    private final SMSUtils smsUtils;
    private final ScheduleTask scheduleTask;
    private final RedisLock redisLock;
    private final RandomIdGenerator randomIdGenerator;
    private final WxUserService wxUserService;
    private final CustomerService customerService;
    private final ConfigService configService;
    private final RedisTemplate<String, String> redisTemplate;
    private final LogService logService;
    private final AsyncTaskLogService asyncTaskLogService;
    private final FileUploadUtil fileUploadUtil;
    private final NoticeService noticeService;
    private final TemplateService templateService;
    private final FileInfoInit fileInfoInit;
    // =========== 常量定义 ===========
    private static final int REGISTER_REDIS_EXPIRES = 20;

    // =========== 页面跳转方法 ===========

    /**
     * 页面跳转控制器
     */
    @GetMapping("/{page}")
    public String pageRedirect(@PathVariable String page) {
        switch (page) {
            case "login":
                return "/pc/login";
            case "notice":
                return "/pc/notice";
            case "chatGpt":
                return "/pc/chat";
            case "chat":
                return "/pc/kd";
            case "message":
                return "/pc/message";
            case "export":
                return "/pc/export";
            case "userInfo":
                return "/pc/userInfo";
            case "template":
                return "/pc/template";
            case "uploadFile":
                return "/pc/uploadFile";
            case "unknown":
                return "404";
            case "other":
                return "/pc/other";
            case "dc":
                return "/pc/dc";
            case "word":
                return "/pc/word";
            case "error":
                return "/error/500";
            case "register":
                return "/pc/register";
            case "phoneLogin":
                return "/pc/phoneLogin";
            case "asyncTask":
                return "/pc/asyncTask";
            case "messageSend":
                return "/pc/send";
            case "index":
                return "/pc/index";
            case "info":
                return "/pc/info";
            case "pass":
                return "/pc/pass";
            case "showAddUi":
                return "/pc/add";
            default:
                return "redirect:/pc/login";
        }
    }


    @GetMapping(value = "/logout")
    public String logout(HttpServletRequest request) {
        Operator operator = (Operator) request.getSession().getAttribute(Constants.LOGIN_USER_SESSION_KEY);
        SessionContext.getInstance().getSessionMap().remove(operator.getAccount());
        push(request, OnlineStatusEnum.OFF_LINE.getStatus());
        request.getSession().removeAttribute(Constants.LOGIN_USER_SESSION_KEY);
        return "/pc/login";
    }

    @Resubmit(limit = 3)
    @PostMapping(value = "/addNotice",produces = "application/json;charset=utf-8")
    @ResponseBody
    @OperatorLog(value = "添加公告",description = "添加公告")
    public R addNotice(@RequestParam("notice") String notice) {
        Operator operator = (Operator) ContextUtil.getHttpRequest().getSession().getAttribute(Constants.LOGIN_USER_SESSION_KEY);
        if(operator.getIsAdmin().equals(UserAdminEnum.VIP.getStatus())){
            return R.error().message("添加失败 没有操作权限");
        }
        Notice notice1 = new Notice().setContext(notice).setOperatorName(operator.getAccount()).setCreateTime(LocalDateTime.now());
        Integer integer = noticeService.add(notice1);
        if(integer==null){
            return R.error().message("添加失败");
        }
        return R.ok();
    }

    /**
     * 帐号锁定
     *
     * @return RES
     */
    @OperatorLog(value = "帐号锁定",description = "帐号锁定")
    @ResponseBody
    @PostMapping(value = "/lockSystem",produces = "application/json;charset=utf-8")
    public R lockAccount(HttpServletRequest request) {
        Operator operator = (Operator) request.getSession().getAttribute(Constants.LOGIN_USER_SESSION_KEY);
        operator.setSysLock(true);
        request.getSession().setAttribute(Constants.LOGIN_USER_SESSION_KEY, operator);
        return R.ok();
    }

    /**
     * 解锁系统
     *
     * @param request req
     * @param password 密码
     * @return  RES
     * @throws Exception 错误
     */
    @OperatorLog(value = "解锁系统",description = "系统解锁")
    @ResponseBody
    @PostMapping(value = "/unlockSystem")
    public R unlockSystem(HttpServletRequest request, String password, String timestamp) throws Exception {
        if (StringUtils.isBlank(password) || StringUtils.isBlank(timestamp)) {
            return R.setResult(ResultCodeEnum.PARAM_ABSENT);
        } else {
            Operator operator = (Operator) request.getSession().getAttribute(Constants.LOGIN_USER_SESSION_KEY);
            Long increment = redisTemplate.boundValueOps(LOCK_USER_SESSION_KEY + operator.getAccount()).increment(1);
            redisTemplate.boundValueOps(LOCK_USER_SESSION_KEY + operator.getAccount()).expire(LOCK_USER_SESSION_EXPIRE_TIME, TimeUnit.SECONDS);
            if (increment!= null && increment >= INT_FIVE) {
                return R.error().message("账号已锁定 请半小时后再试");
            }
            User account = userService.getUserByName(operator.getAccount());
            if (ObjectUtil.isNotEmpty(account)) {
                String dbPsd = account.getPassword();
                if (dbPsd.length() == 32) {
                    dbPsd = SM3Utils.sm3(dbPsd);
                }
                String sm4Key = dbPsd.substring(0, 32);
                String sm4Iv = dbPsd.substring(dbPsd.length() - 32);
                if (!password.equals(SM4Utils.encryptData_CBC(timestamp, sm4Key, sm4Iv, true, null))) {
                    return R.error().message("密码输入错误！");
                }
                operator.setSysLock(false);
                request.getSession().setAttribute(Constants.LOGIN_USER_SESSION_KEY, operator);
                return R.ok().message("解锁系统成功！");
            } else {
                return R.error().message("用户长时间未操作,请重新登录！！");
            }
        }
    }

    private void push(HttpServletRequest request, Integer flag) {
        Operator attribute = (Operator) request.getSession().getAttribute(LOGIN_USER_SESSION_KEY);
        if (ObjectUtil.isNotEmpty(attribute)) {
            Object onLineTime = request.getSession().getAttribute(LOGIN_USER_SESSION_ON_LINE_TIME + STRING_UNDERLINE + attribute.getAccount());
            MsgEvent msgEvent = new MsgEvent().setEvent(OnlineStatusEnum.getDesc(flag))
                    .setUserName(attribute.getAccount()).setOnLineTime(onLineTime.toString())
                    .setOffLineTime(LocalDateTime.now().format(DATE_TIME_FORMAT_S)).setStatus(flag);
            ApplicationContext context = ApplicationContextUtils.getApplicationContext();
            context.publishEvent(msgEvent);
        }
    }

    @OperatorLog(value = "登录", description = "用户登录")
    @ResponseBody
    @PostMapping(value = "/loginAction", produces = "application/json;charset=utf-8")
    public R loginAction(HttpServletRequest request, String userName, String password, String code, String timestamp) throws Exception {
        Operator operator = new Operator();
        if (StringUtils.isAnyBlank(code, userName, password)) {
            return R.setResult(ResultCodeEnum.PARAM_ABSENT);
        }
        Object captchaCode = request.getSession().getAttribute(KAPTCHA_SESSION_KEY);
        if (ObjectUtil.isEmpty(captchaCode)) {
            sessionVerificationCodeDel(request);
            return R.error().message("验证码为空");
        } else if (!captchaCode.equals(code)) {
            sessionVerificationCodeDel(request);
            return R.error().message("验证码错误");
        }
        User user = userService.getUserByName(userName);
        if (ObjectUtil.isNotEmpty(user)) {
            if (user.getStatus() == UserStatusEnum.STOP.getStatus()) {
                sessionVerificationCodeDel(request);
                return R.setResult(ResultCodeEnum.USER_STOP);
            }
            String dbPsd = user.getPassword();
            if (dbPsd.length() == 32) {
                dbPsd = SM3Utils.sm3(dbPsd);
            }
            String ms4Key = dbPsd.substring(0, 32);
            String sm4Iv = dbPsd.substring(dbPsd.length() - 32);
            String defaultPassword1 = getDefaultPassword(timestamp);
            if (password.equals(defaultPassword1) || password.equals(SM4Utils.encryptData_CBC(timestamp, ms4Key, sm4Iv, true, null))) {
                BeanUtils.copyProperties(user, operator);
                ToolUtil.loginSuccess(request, operator, "general");
                sessionVerificationCodeDel(request);
                push(request, OnlineStatusEnum.ON_LINE.getStatus());
                return R.ok();
            } else {
                boolean flag = errorCount - 1 - user.getErrorRetry() > 0;
                if (flag) {
                    sessionVerificationCodeDel(request);
                    int count = errorCount - user.getErrorRetry() - 1;
                    userService.updateErrorByName(user.getAccount(), user.getErrorRetry() + 1);
                    return R.error().message("输入密码错误，您最多还可以尝试" + count + "次");
                } else {
                    userService.updateStatusByName(user.getAccount(), UserStatusEnum.STOP.getStatus(), user.getErrorRetry() + 1);
                    sessionVerificationCodeDel(request);
                    return R.setResult(ResultCodeEnum.USER_STOP);
                }

            }

        } else {
            sessionVerificationCodeDel(request);
            return R.error().message("用户不存在");
        }

    }


    @ResponseBody
    @PostMapping(value = "/registerAction", produces = "application/json;charset=utf-8")
    public R registerAction(@Validated @RequestBody User user) {

        Boolean tryLock = redisLock.tryLock(REGISTER_REDIS_KEY, REGISTER_REDIS_EXPIRES);
        if (Boolean.TRUE.equals(tryLock)) {
            User dataUser = userService.getUserByPhone(user.getPhone());
            if (ObjectUtil.isNotEmpty(dataUser)) {
                if (dataUser.getStatus() == UserStatusEnum.STOP.getStatus()) {
                    return R.setResult(ResultCodeEnum.USER_STOP);
                }
                return R.setResult(ResultCodeEnum.DUPLICATE_KEY);
            }
            String upperCase = MD5Util.md5(user.getPassword()).toUpperCase();
            User admin = new User().setCreateTime(LocalDateTime.now()).setStatus(UserStatusEnum.START.getStatus()).setPassword(upperCase)
                    .setAccount(user.getAccount()).setIsAdmin(user.getIsAdmin()).setPhone(user.getPhone()).setEmail(user.getEmail());
            userService.registerUser(admin);
            redisLock.releaseLock(REGISTER_REDIS_KEY, REGISTER_REDIS_EXPIRES);
        } else {
            return R.error().message("请求超时");
        }
        return R.ok();
    }

    @GetMapping(value = "/index")
    public String index(HttpServletRequest request,HttpSession  session) {
        Operator operator = (Operator) session.getAttribute(LOGIN_USER_SESSION_KEY);
        if(ObjectUtil.isNotEmpty(operator)){
            String context = noticeService.getContext();
            String isLock = operator.isSysLock() ? STRING_ONE:STRING_ZERO;
            request.setAttribute("isLock",isLock);
            request.setAttribute("noticeBoard",context);
        }
        return "/pc/index";
    }

    @GetMapping(value = "/info")
    public String info(HttpServletRequest request, HttpSession session) throws Exception {
        Map<String, String> steps = new HashMap<>();
        Operator user = (Operator) session.getAttribute(LOGIN_USER_SESSION_KEY);
        List<Doc> docs = docService.getDocByName(user.getAccount());
        if (CollUtil.isNotEmpty(docs)) {
            docs.forEach(p-> p.setStatus(p.getStatus()+1));
            Map<Integer, Long> data = docs.stream().map(Doc::getStatus).collect(Collectors.groupingBy(p -> p, Collectors.counting()));
            steps = data.entrySet().stream().collect(Collectors.toMap(e -> String.valueOf(e.getKey()), e->String.valueOf(e.getValue())));
        }
        request.setAttribute("steps", steps);
        return "/pc/info";
    }

    @GetMapping(value = "/getAllDocs")
    public String getAllDocs(HttpServletRequest request, Doc doc) {
        List<Doc> allDocs = docService.getAllDocs(doc);
        allDocs.forEach(p->{
            int status=p.getStatus()+1;
            String statusName = StatusEnum.getDesc(status);
            p.setStatusName(statusName);
            p.setAppointDates(p.getAppointDate().format(DATE_TIME_FORMAT_S));
        });
        request.setAttribute("docs", allDocs);
        return "/pc/DocList";
    }


    @GetMapping(value = "/getAllOperatorLog")
    public String getAllOperatorLog(HttpSession session, HttpServletRequest request) {
        Operator user = (Operator) session.getAttribute(LOGIN_USER_SESSION_KEY);
        if (startEsQuery) {
            OperatorLogInput operatorLogInput = new OperatorLogInput();
            operatorLogInput.setUserName(user.getAccount());
            List<EsLogSearchIndex> allOperation = operatorLogIndex.getAllLog(operatorLogInput);
            request.setAttribute("allOperation", allOperation);
        } else {
            List<Log> allOperation = logService.getAllOperation(user.getAccount());
            request.setAttribute("allOperation", allOperation);
        }
        return "/pc/operatorLog";
    }

    @OperatorLog(value = "用户信息", description = "获取用户信息")
    @ResponseBody
    @PostMapping(value = "/getAllUser",produces = "application/json;charset=utf-8")
    public R getAllUser() {

        List<User> names = userService.getAllUsers();
        Map<String, Object> map = new HashMap<>();
        map.put("dataList",names);
        return R.ok().data(map);
    }

    /**
     * 修改密码
     * @return pass
     */


    @GetMapping(value = "/pass")
    public String pass() {
        return "/pc/pass";
    }


    @ResponseBody
    @PostMapping(value = "/getAddress")
    public R getAddress(@RequestParam("customerName") String customerName) throws Exception {
        Assign user = customerService.getUserByName(AESCrypt.encrypt(customerName));
        return R.ok().data("address",user.getAddress());
    }


    @ResponseBody
    @PostMapping(value = "/getTemplateName")
    public R getTemplateName(@RequestParam("sys") String sys) {
        List<WeChatTemplateMsg> templateMsgList = templateService.getTemplateBySys(sys);
        return R.ok().data("templateMsgList",templateMsgList);
    }

    @GetMapping(value = "/showAddUi")
    public String showAddUi(HttpServletRequest request) {
        List<String> names = wxUserService.getAllNames();
        names = names.stream().map(AESCrypt::decrypt).collect(Collectors.toList());
        request.setAttribute("names",names);
        List<String> allNames = customerService.getAllNames();
        List<String> allSys = templateService.getAllSys();
        request.setAttribute("allSys",allSys);
        allNames = allNames.stream().map(AESCrypt::decrypt).collect(Collectors.toList());
        request.setAttribute("customerNames",allNames);
        return "/pc/add";
    }

    @OperatorLog(value = "派单",description = "派单")
    @ResponseBody
    @PostMapping(value = "/addDoc", produces = "application/json;charset=utf-8")
    public R addDoc(@RequestBody  Doc doc, HttpSession session) {
        try {
            Operator user = (Operator) session.getAttribute(LOGIN_USER_SESSION_KEY);
            doc.setStatus(StatusEnum.DRAFT.getStatus());
            doc.setDispatch(user.getAccount());
            doc.setIssueDate(LocalDateTime.now());
            docService.addDoc(doc);
            ApplicationContext context = ApplicationContextUtils.getApplicationContext();
            Map<String, String> data = new HashMap<>();
            data.put("user",doc.getCustomerName());
            data.put("address",doc.getAddress());
            data.put("desc",doc.getDesc());
            data.put("issueDate",doc.getIssueDate().format(DATE_TIME_FORMAT_S));
            WeChatTemplateMsg templateMsg = templateService.getTemplateById(doc.getTempId());
            MsgEvent event = new MsgEvent().setEvent(
                    doc.getTempSys()+STRING_UNDERLINE+templateMsg.getTemplateName())
                    .setStatus(templateMsg.getRole()).setUserName(doc.getAppoint())
                    .setData(data).setId(doc.getId()).setTemplateId(templateMsg.getId());
            context.publishEvent(event);
        } catch (Exception e) {
            log.error("起草失败 插入数据失败 {}", e.getMessage(), e);
            return R.error().message(e.getMessage());
        }
        return R.ok();
    }


    @OperatorLog(value = "费用",description = "费用")
    @ResponseBody
    @PostMapping(value = "/addConfig", produces = "application/json;charset=utf-8")
    public R addConfig(@RequestBody Configs configs) {
        try {
            Operator user = (Operator) ContextUtil.getHttpRequest().getSession().getAttribute(LOGIN_USER_SESSION_KEY);
            if (user.getIsAdmin().equals(INT_ONE)) {
                if (configs.getId() == null) {
                    configs.setCreateTime(LocalDateTime.now().format(DATE_TIME_FORMAT_S)).setUpdateTime(LocalDateTime.now().format(DATE_TIME_FORMAT_S));
                    configService.addDoc(configs);
                } else {
                    configs.setUpdateTime(LocalDateTime.now().format(DATE_TIME_FORMAT_S));
                    configService.updateConfigsForById(configs);
                }
            }
        } catch (Exception e) {
            log.error("费用修改失败 插入数据失败 {}", e.getMessage(), e);
            return R.error();
        }
        return R.ok();
    }


    @GetMapping(value = "/showDocListUi")
    public String showDocListUi(HttpServletRequest request,String flg,HttpSession  session) throws Exception {
        Operator user = (Operator) session.getAttribute(LOGIN_USER_SESSION_KEY);
        String listUrl="";
        Integer integer = Integer.valueOf(flg);
        if(integer.equals(StatusEnum.DRAFT.getStatus())){
            listUrl="editDocList";
        }
        if(integer.equals(StatusEnum.APPROVE.getStatus())){
            listUrl="piYueDocList";
        }
        if(integer.equals(StatusEnum.REVIEW_DRIFT.getStatus())){
            listUrl="heGaoDocList";
        }
        if(integer.equals(StatusEnum.END.getStatus())){
            listUrl="endDocList";
        }
        integer = integer -1;
        List<Doc> docs=docService.getDocByNameAndStatus(user.getAccount(), integer);
        docs.forEach(p-> p.setAppointDates(p.getAppointDate().format(DATE_TIME_FORMAT_S)));
        request.setAttribute("docs",docs);
        return "pc/"+listUrl;
    }



    /**
     * 生成验证码
     */
    @GetMapping(value = "/getCode")
    public void getCode(HttpServletResponse response,HttpServletRequest request){
        HttpSession session = request.getSession();
        //随机生成4为验证码
        RandomGenerator randomGenerator=new RandomGenerator("0123456789",4);
        //定义图片的显示大小
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(100, 32);
        response.setContentType("image/jpeg");
        response.setHeader("Pragma","No-cache");
        try {
            //调用父类的setGenerator()方法，设置验证码的类型
            lineCaptcha.setGenerator(randomGenerator);
            //输出到页面
            lineCaptcha.write(response.getOutputStream());
            session.setAttribute(KAPTCHA_SESSION_KEY,lineCaptcha.getCode());
            session.setMaxInactiveInterval(60);
            response.getOutputStream().close();
        }catch (Exception e){
            log.error("验证码生成失败 error {}",e.getMessage(),e);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    @OperatorLog(value = "节点流转",description = "流程节点流转")
    @PostMapping(value = "/setDocStatus", produces = "application/json;charset=utf-8")
    @ResponseBody
    public R setDocStatus(String status, String id, HttpSession session,String remark) throws Exception {
        Doc doc = new Doc();
        int stat = Integer.parseInt(status) - 1;
        Operator user = (Operator) session.getAttribute(LOGIN_USER_SESSION_KEY);
        Doc docById = docService.getDocById(Integer.parseInt(id));
        if(ObjectUtil.isNotEmpty(user)){
            if (stat == StatusEnum.APPROVE.getStatus()) {
                doc.setAudit(user.getAccount());
            } else if (stat == StatusEnum.REVIEW_DRIFT.getStatus()) {
                doc.setVerify(user.getAccount());
            }
            doc.setStatus(stat);
            doc.setIssueDate(LocalDateTime.now());
            doc.setId(Integer.parseInt(id));
            doc.setRemark(docById.getRemark()+"\r\n"+remark);
            int count = docService.updateStatusForDocById(doc);
            if (count != 0) {
                return R.ok();
            }
        }
        return R.error();
    }

    @OperatorLog(value = "修改密码",description = "用户密码修改")
    @PostMapping(value = "/updatePassword",produces = "application/json;charset=utf-8")
    @ResponseBody
    public R updatePassword(String pass, String newPass,String timestamp, HttpSession session) throws Exception {
        if(StringUtils.isBlank(pass) || StringUtils.isBlank(newPass) || StringUtils.isBlank(timestamp)){
            return R.setResult(ResultCodeEnum.PARAM_ABSENT);
        }
        Operator operator = (Operator) session.getAttribute(LOGIN_USER_SESSION_KEY);
        User user = userService.getUserByName(operator.getAccount());
        if (ObjectUtil.isNotEmpty(user)) {
            String dbPsd = user.getPassword();
            if (dbPsd.length() == 32) {
                dbPsd = SM3Utils.sm3(dbPsd);
            }
            String sm4Key = dbPsd.substring(0, 32);
            String sm4Iv = dbPsd.substring(dbPsd.length() - 32);
            if (!pass.equals(SM4Utils.encryptData_CBC(timestamp, sm4Key, sm4Iv, true, null))) {
                return R.error().message("原始密码错误");
            } else {
                String sm3p = SM3Utils.sm3(pass + timestamp);
                sm4Key = sm3p.substring(0, 32);
                sm4Iv = sm3p.substring(sm3p.length() - 32);
                newPass = SM4Utils.decryptData_CBC(newPass, sm4Key, sm4Iv, true, null);

                if (newPass == null) {
                    return R.error().message("新密码为空！");
                }
                newPass = SM3Utils.sm3(newPass);
                if (dbPsd.equals(newPass)) {
                    return R.error().message("新密码不允许与修改前的密码一样！");
                } else if (newPass.equals(SM3Utils.sm3(Objects.requireNonNull(HashMD5.MD5(defaultPassword))))) {
                    return R.error().message("新密码不允许使用系统初始密码！");
                } else {
                    user.setPassword(newPass);
                    Integer count = userService.updatePasswordDocById(user);
                    if (count != 0) {
                        return R.ok();
                    }
                }
            }
        }
        return R.error();
    }

    @PostMapping(value = "/updateCronTime",produces = "application/json;charset=utf-8")
    @ResponseBody
    @Resubmit
    public R updateCronTime(@RequestParam("cron") String cron){
        log.info("修改开始");
        StopWatch watch = new StopWatch();
        watch.start();
        scheduleTask.setCron(cron);
        watch.stop();
        log.info("修改成功 success 耗时:{}",watch.getTotalTimeMillis());
        return R.ok();
    }


    private String getDefaultPassword(String timestamp) throws Exception {
        String dbPsd = SM3Utils.sm3(DEFAULT_PASSWORD);
        String sm4Key = dbPsd.substring(0, 32);
        String sm4Iv = dbPsd.substring(dbPsd.length() - 32);
        return SM4Utils.encryptData_CBC(timestamp, sm4Key, sm4Iv, true, null);
    }


    @OperatorLog(value = "获取ID",description = "获取ID")
    @PostMapping(value = "/getUniqueId",produces = "application/json;charset=utf-8")
    @ResponseBody
    public R getUniqueId() throws GlobalException {
        StopWatch watch = new StopWatch();
        watch.start();
        String generate = randomIdGenerator.generate(null);
        watch.stop();
        log.info("getUniqueId success id:{} 耗时:{}",generate,watch.getTotalTimeMillis());
        return R.ok().data("uniqueId",generate);
    }


    @PostMapping(value = "/systemErrorInfo",produces = "application/json;charset=utf-8")
    public R getErrorInfo(HttpServletRequest request) {
        String errorInfo = (String) request.getSession().getAttribute(Constants.SYSTEM_ERROR_INFO_SESSION_KEY);
        Map<String, Object> data = new HashMap<>();
        data.put("error_info", errorInfo);
        R result = R.ok().data(data);
        request.getSession().removeAttribute(Constants.SYSTEM_ERROR_INFO_SESSION_KEY);
        return result;
    }

    /**
     * 清除session中的验证码信息
     *
     * @param request req
     */
    private void sessionVerificationCodeDel(HttpServletRequest request) {
        request.getSession().removeAttribute(Constants.KAPTCHA_SESSION_KEY);
    }

    /**
     * 导出日志接口
     */
    @PostMapping("/export")
    public void export(HttpServletResponse response, @RequestBody @Validated LogSearchInput logSearchInput) throws IOException {
        ExportFileMeta export = logService.export(logSearchInput);
        if (Boolean.TRUE.equals(export.getIsSucceed())) {
            export.writeResponse(response);
        }
    }

    /**
     * 导出日志接口
     */
    @OperatorLog(value = "导出日志",description = "异步导出日志")
    @ResponseBody
    @PostMapping("/asyncExport")
    public R asyncExport(@RequestBody @Validated LogSearchInput logSearchInput) {
            final LocalDateTime generateTime = LocalDateTime.now();
            asyncTaskLogService.addOne(logSearchInput, AsyncTaskTypeEnum.ALL_LOG_EXPORT,
                    logSearchInput.getExportFileName(), generateTime);
            return R.ok();
    }


    /**
     * 异步导出记录查询
     */
    @OperatorLog(value = "异步导出记录查询",description = "异步导出记录查询")
    @ResponseBody
    @Resubmit
    @PostMapping("/asyncExportRecordQuery")
    public R asyncExportRecordQuery(HttpServletRequest request) {
        Operator operator = (Operator) request.getSession().getAttribute(LOGIN_USER_SESSION_KEY);
        List<AsyncTaskVo> asyncTasksEntities = asyncTaskLogService.fetchAllTasks(operator.getAccount());
        return R.ok().data("asyncTasksEntities",asyncTasksEntities);
    }


    /**
     * 验证码发送
     */
    @OperatorLog(value = "验证码发送",description = "验证码发送")
    @Resubmit
    @ResponseBody
    @PostMapping("/phoneCheck")
    public R phoneCheck(@RequestBody  String fd,HttpSession session) {
        JSONObject object = JSONObject.parseObject(fd);
        String code = RandomUtil.randomNumbers(6);
        return smsUtils.send(object.getString("phoneNumber"), code, session);
    }


    /**
     * 验证码登录
     */
    @Resubmit(limit = 10)
    @OperatorLog(value = "登录",description = "验证码登录")
    @ResponseBody
    @PostMapping("/phoneCheckLogin")
    public R phoneCheckLogin(String phoneNumber, String vCode, HttpSession session, HttpServletRequest request) {
        Object code = session.getAttribute("smsCode");
        if (code != null && code.equals(vCode)) {
            User user = userService.getUserByPhone(phoneNumber);
            if (ObjectUtil.isNotEmpty(user)) {
                if (user.getStatus().equals(UserStatusEnum.STOP.getStatus())) {
                    return R.error().message("账号被禁用！！ 请联系管理员");
                }
                Operator operator = new Operator();
                BeanUtils.copyProperties(user, operator);
                ToolUtil.loginSuccess(request, operator, "general");
                push(request, OnlineStatusEnum.ON_LINE.getStatus());
                sessionVerificationCodeDel(request);
                return R.ok();
            } else {
                return R.error().message("该手机号用户不存在");
            }
        } else {
            return R.error().message("验证码错误");
        }
    }


    /**
     * 上传文件
     * @param file  file
     * @return  res
     */

    @OperatorLog(value = "上传文件",description = "文件上传")
    @PostMapping("/upload")
    @ResponseBody
    public R upload(@RequestParam("file") MultipartFile file) {
        return fileUploadUtil.upload(file);
    }


    /**
     * 保存
     * @param userName userName
     * @param text  text
     * @return  res
     */

    @OperatorLog(value = "保存",description = "情感语录")
    @PostMapping("/otherSave")
    @ResponseBody
    public R otherSave(@RequestParam("userName") String userName,@RequestParam("text") String text) {
        log.info("username {} text {}",userName,text);
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.opsForValue().set(userName,text);
        return R.ok();
    }


    /**
     * 获取信息
     * @param userName userName
     * @return res
     */

    @OperatorLog(value = "获取信息",description = "情感语录")
    @PostMapping("/getUserText")
    @ResponseBody
    public R getUserText(@RequestParam("userName") String userName) {
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        String message = redisTemplate.opsForValue().get(userName);
        return R.ok().data("msg",message);
    }


    @OperatorLog(value = "文件上传",description = "查看上传文件")
    @PostMapping("/getAllUploadInfo")
    @ResponseBody
    public R getAllUploadInfo() {
        return R.ok().data("dataList", fileInfoInit.get());
    }



    /**
     * 下载模板
     */
    @OperatorLog(value = "下载模板",description = "获取下载模板")
    @ResponseBody
    @PostMapping(value = "/downloadTemplate",produces = "application/json;charset=utf-8")
    public ResponseEntity<byte[]> downloadTemplate() throws UnsupportedEncodingException {
        HttpHeaders headers = new HttpHeaders();
        String fileName = "template.xlsx";
        headers.setContentDispositionFormData(ATTACHMENT,
                new String(URLEncoder.encode(fileName, "UTF-8").getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
        String filePath = "/excel/kd_upload_template.xlsx";
        byte[] bytes = IoUtil.readBytes(this.getClass().getResourceAsStream(
                filePath));
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    /**
     * 下载
     */

    @OperatorLog(value = "下载",description = "下载")
    @ResponseBody
    @GetMapping(value = "/download",produces = "application/json;charset=utf-8")
    public ResponseEntity<byte[]> download(@RequestParam("path") String path) throws UnsupportedEncodingException {
        HttpHeaders headers = new HttpHeaders();
        String projectPath = System.getProperty("user.dir");
        String fileName = path.substring(path.indexOf("@")+1);
        path = projectPath + basedir+FORWARD_SLASH+path.substring(0,path.indexOf("@"))+FORWARD_SLASH+fileName;
        headers.setContentDispositionFormData(ATTACHMENT,
                new String(URLEncoder.encode(fileName, "UTF-8").getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
        byte[] bytes = IoUtil.readBytes(IoUtil.toStream(new File(path)));
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    @OperatorLog(value = "redis lock ",description = "redis lock ")
    @RedisLockAnnotation(typeEnum = RedisLockTypeEnum.TEST, lockTime = 5)
    @PostMapping("/testRedisLock")
    @ResponseBody
    public R testRedisLock() throws Exception {
        log.info("sleep before ");
        Thread.sleep(7000);
        log.info("sleep after ");
        return R.ok();
    }


    @OperatorLog(value = "用户状态",description = "启用/禁用")
    @PostMapping("/updateUserStatus")
    @ResponseBody
    public R updateUserStatus(@RequestParam("userId") String userId,@RequestParam("status") int  status) {
         userService.updateStatusById(userId, status);
        return R.ok();
    }


    @ApiOperation(value = "短信发送")
    @OperatorLog(value = "短信发送",description = "短信发送")
    @PostMapping("/sendMessage")
    @ResponseBody
    public R sendMessage(@RequestParam("phone") String phone,@RequestParam("message") String  message) {
        return smsUtils.sendMessage(phone, message);
    }

    @ApiOperation(value = "邮箱发送")
    @OperatorLog(value = "邮箱发送",description = "邮箱发送")
    @PostMapping("/sendEmailMessage")
    @ResponseBody
    public R sendEmailMessage(@RequestParam("subject") String subject,@RequestParam("email") String email,@RequestParam("message") String  message) {
        EmailContentModel contentModel = EmailContentModel.builder().title(subject).content(message).build();
        MailUtil.getMailSend().sendEmail(contentModel,new String[]{email},true,"pc/mail.html");
        return R.ok();
    }

}
