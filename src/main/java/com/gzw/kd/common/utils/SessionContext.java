package com.gzw.kd.common.utils;

import com.gzw.kd.common.Constants;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 高志伟
 */
@SuppressWarnings("all")
@Slf4j
public class SessionContext {

  private static SessionContext instance;
  private final HashMap<String, HttpSession> sessionMap;



  private SessionContext() {
    sessionMap = new HashMap<>();
  }

  public static SessionContext getInstance() {
    if (instance == null) {
      instance = new SessionContext();
    }
    return instance;
  }

  public synchronized void addSession(HttpSession session) {
    if (session != null) {
      sessionMap.put(session.getId(), session);
    }
  }

  public synchronized void delSession(HttpSession session) {
    if (session != null) {
      sessionMap.remove(session.getId());
      if (session.getAttribute(Constants.LOGIN_USER_SESSION_KEY) != null) {
        sessionMap.remove(session.getAttribute(Constants.LOGIN_USER_SESSION_KEY).toString());
      }
    }
  }

  public HashMap<String,HttpSession> getSessionMap() {
    return sessionMap;
  }

}
