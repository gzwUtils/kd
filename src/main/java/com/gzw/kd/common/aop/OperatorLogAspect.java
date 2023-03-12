package com.gzw.kd.common.aop;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONObject;
import static com.gzw.kd.common.Constants.*;
import com.gzw.kd.common.R;
import com.gzw.kd.common.annotation.OperatorLog;
import com.gzw.kd.common.entity.Operator;
import com.gzw.kd.common.entity.SysLog;
import com.gzw.kd.common.utils.AESCrypt;
import com.gzw.kd.common.utils.AddressUtil;
import com.gzw.kd.common.utils.ContextUtil;
import com.gzw.kd.service.SystemOperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * @author 高志伟
 */

@SuppressWarnings("all")
@Slf4j
@Aspect
@Component
public class OperatorLogAspect {

    @Resource
    SystemOperationLogService systemOperationLogService;

    @Pointcut("@annotation(com.gzw.kd.common.annotation.OperatorLog)")
    public void pointcut() {

    }


    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) throws UnsupportedEncodingException {
        Object result = null;
        long beginTime = System.currentTimeMillis();
        try {
            result = point.proceed();
        } catch (Throwable e) {
            log.error("operator  log exception {} ",e.getMessage(),e);
        }
        long time = System.currentTimeMillis() - beginTime;
        saveLog(point, time,result);
        return result;
    }

    private void saveLog(ProceedingJoinPoint joinPoint, long time, Object result) {
        SysLog sysLog = new SysLog();
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            OperatorLog logAnnotation = method.getAnnotation(OperatorLog.class);
            if (logAnnotation != null) {
                sysLog.setOperation(logAnnotation.value());
                sysLog.setDesc(logAnnotation.description());
            }
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = signature.getName();
            sysLog.setMethod(className + "." + methodName + "()");
            Object[] args = joinPoint.getArgs();
            LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
            String[] paramNames = u.getParameterNames(method);
            if (args != null && paramNames != null) {
                String params = "";
                for (int i = 0; i < args.length; i++) {
                    params += "" + paramNames[i] + ":" + args[i];
                }
                sysLog.setParams(AESCrypt.encrypt(params));
            }
            HttpServletRequest request = ContextUtil.getHttpRequest();
            sysLog.setIp(AddressUtil.getIpAddress(request));
            sysLog.setUsername(request.getParameter(LOGIN_USER_Name)==null?STRING_UNKNOWN:request.getParameter(LOGIN_USER_Name));
            Operator operator = (Operator) request.getSession().getAttribute(LOGIN_USER_SESSION_KEY);
            if (ObjectUtil.isNotEmpty(operator)) {
                sysLog.setUsername(operator.getAccount());
            }
            sysLog.setTime(time);
            sysLog.setCreateTime(LocalDateTime.now());
            sysLog.setLocation(AddressUtil.getAddress(sysLog.getIp()));
            R results = JSONObject.parseObject(JSONUtil.toJsonStr(result), R.class);
            sysLog.setResult(results.getMessage());
        } catch (Exception e) {
            sysLog.setResult("未知错误 请查看日志信息");
            log.error("saveLog error {}",e.getMessage(),e);
        }
        systemOperationLogService.insertSelective(sysLog);

    }


}
