package com.sky.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author tz95
 * @project sky-take-out
 * @date 2025/6/8
 */
@Component
@ServerEndpoint("/ws/{sid}")
@Slf4j
public class WebSocketServer {

    /**
     * 存储所有连接的WebSocket会话
     * key: sessionId
     * value: Session对象
     */
    private static Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) throws IOException {
        log.info("WebSocket连接建立，sessionId: {}, sid: {}", session.getId(), sid);
        // 将新连接的Session存入Map
        sessionMap.put(sid, session);
        session.getBasicRemote().sendText("欢迎连接到WebSocket服务器，您的sid是: " + sid);
        // 可以在这里发送欢迎消息或其他初始化操作

    }

    @OnMessage
    public void onMessage(String message, @PathParam("sid") String sid) {
        log.info("收到消息: sid:{} , {}", sid, message);
    }

    @OnClose
    public void onClose(@PathParam("sid") String sid) {
        log.info("sid: {} 的WebSocket连接已关闭", sid);
        sendToAllClient(sid + " 已断开连接");
        sessionMap.remove(sid);
    }

    public void sendToAllClient(String message) {
        log.info("向所有客户端发送消息: {}", message);
        for (Session session : sessionMap.values()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                log.error("发送消息失败: {}", e.getMessage());
            }
        }
    }

}
