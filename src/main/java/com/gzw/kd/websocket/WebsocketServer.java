package com.gzw.kd.websocket;


import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @author gzw
 * @description：
 * @since：2023/2/17 00:37
 */
@SuppressWarnings("all")
@Slf4j
@ServerEndpoint("/websocket/{uid}")
@Component
public class WebsocketServer {

    private static final AtomicInteger ONLINE_NUM  = new AtomicInteger(0);

    private static final CopyOnWriteArraySet<Session> sessions = new CopyOnWriteArraySet<>();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "uid") String uid) throws IOException {
        if(!CollectionUtils.isEmpty(sessions) && sessions.contains(session)){
            session.close();
            log.info("[{}]的连接已存在，不能重复连接，当前连接数=[{}]", uid, ONLINE_NUM);
            return;
        }
        sessions.add(session);
        ONLINE_NUM.incrementAndGet();
        log.info(" uid : {} 链接加入 online num {},",uid,ONLINE_NUM);
        sendMessage(session,ONLINE_NUM+"");
    }
    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session, @PathParam(value = "uid") String uid){
        sessions.remove(session);
        int cnt = ONLINE_NUM.decrementAndGet();
        log.info(" uid : {} 链接退出 online num {},",uid,cnt);
    }

    /**
     * 发送消息
     */

    public void sendMessage(Session session, String message) throws IOException {
        if(session!=null){
            synchronized (session){
                session.getBasicRemote().sendText(message);
            }
        }
    }


    /**
     * 群发消息
     */

    public void broadCastInfo(String message) throws IOException {
       for(Session session : sessions){
           if(session.isOpen()){
               sendMessage(session,message);
           }
       }
    }


    /**
     * 收到客户端消息后调用的方法
     * @param message
     * @param session
     */

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        log.info("收到来自窗口的信息:{}",message);
        sendMessage(session,ONLINE_NUM+"");
    }

    /**
     * 发生错误
     */
    @OnError
    public void onError(Session session,@PathParam(value = "uid") String uid, Throwable throwable){
        log.info("uid {} session {} error ",uid,session);
        throwable.printStackTrace();
    }
}
