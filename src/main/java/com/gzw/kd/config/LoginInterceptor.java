package com.gzw.kd.config;
import cn.hutool.core.util.ObjectUtil;
import com.gzw.kd.common.Constants;
import com.gzw.kd.common.utils.ToolUtil;
import com.gzw.kd.common.entity.Operator;
import com.gzw.kd.common.entity.User;
import com.gzw.kd.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static com.gzw.kd.common.utils.JwtUtils.checkToken;
import static com.gzw.kd.common.utils.JwtUtils.getMemberInfoByJwtToken;


/**
 * 登录 拦截
 * @author 高志伟
 */
@SuppressWarnings("all")
@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {


    @Resource
    UserService userService;

    @Resource
    private AuthConfig authConfig;

    /**
     * controller调用之前
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            Operator operator = (Operator) request.getSession().getAttribute(Constants.LOGIN_USER_SESSION_KEY);
            String ssoToken = request.getParameter("token");
            String state = request.getParameter("state");
            if(StringUtils.equals(state,"STATE")){
                return true;
            }
            if (StringUtils.isNotBlank(ssoToken)) {
                operator =  ssoLogin(request, ssoToken);
                if (operator == null) {
                    response.sendRedirect(request.getContextPath() + "/pc/login");
                    return false;
                }
            }

            if(ObjectUtil.isEmpty(operator)){
                response.sendRedirect(request.getContextPath() + "/pc/login");
                return false;
            }

            if (Constants.REQUEST_SCHEMA.equalsIgnoreCase(request.getScheme())) {
                return false;
            }

            String referer = request.getHeader("referer");
            if(StringUtils.isBlank(referer)){
                request.getRequestDispatcher("/pc/error").forward(request, response);
                return false;
            }
        } catch (Exception e) {
            log.error("get session  user error {}", e.getMessage(), e);
            request.getRequestDispatcher("/pc/unknown").forward(request, response);
            return false;
        }
        return true;
    }





    /**
     * 单点登录
     *
     * @param request  HttpServletRequest
     * @param token    登录令牌
     * @return User 已登录用户信息
     */
    @SuppressWarnings("all")
    private Operator ssoLogin(HttpServletRequest request, String token){
        try {
            if(checkToken(token)){
                Operator operator = new Operator();
                Claims decodeToken = getMemberInfoByJwtToken(token);
                User account = userService.getUserByName(decodeToken.get("user", String.class));
                BeanUtils.copyProperties(account,operator);
                ToolUtil.loginSuccess(request, operator, "switchFlag");
                request.getSession().setAttribute(Constants.LOGIN_USER_SESSION_KEY,operator);
                return operator;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
