package com.gzw.kd.listener;

import static com.gzw.kd.common.Constants.LOGIN_USER_SESSION_KEY;
import com.gzw.kd.common.entity.Operator;
import com.gzw.kd.common.utils.SessionContext;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.*;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 高志伟  相当于 web.xml  监听器
 */
@Slf4j
@SuppressWarnings("all")
@WebListener
public class MysessionListener implements HttpSessionListener, HttpSessionAttributeListener {

    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
    }

    public static boolean isOnline(String sUserName) {
        return SessionContext.getInstance().getSessionMap().containsKey(sUserName);
    }

    @Override
    public void attributeAdded(HttpSessionBindingEvent se) {
        String name = se.getName();
        if(LOGIN_USER_SESSION_KEY.equals(name)){
            Operator attr = (Operator) se.getValue();
            HttpSession session = se.getSession();
            boolean online = isOnline(attr.getAccount());
            if(online){
                HttpSession session1 = SessionContext.getInstance().getSessionMap().get(attr.getAccount());
                log.warn("账户:{} 重复登陆",attr.getAccount());
                session1.invalidate();
            }
            SessionContext.getInstance().getSessionMap().put(attr.getAccount(), session);
        }
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent se) {
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent se) {
    }
}
