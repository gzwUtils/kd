package com.gzw.kd.listener;

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

    public static SessionContext sessionContext = SessionContext.getInstance();

    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        HttpSession session = httpSessionEvent.getSession();
        if (session.isNew() && !isOnline(session.getId())) {
            sessionContext.addSession(httpSessionEvent.getSession());
        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        sessionContext.delSession(httpSessionEvent.getSession());
    }

    public static boolean isOnline(String sUserName) {
        return sessionContext.getSessionMap().containsKey(sUserName);
    }
}
