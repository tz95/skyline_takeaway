package com.sky.task;

import com.sky.websocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author tz95
 * @project sky-take-out
 * @date 2025/6/8
 */
@Component
public class WebSocketTask {

    @Autowired
    private WebSocketServer webSocketServer;

    // @Scheduled(cron = "0/5 * * * * ?")
    public void sendMessageToAllClient(){
        webSocketServer.sendToAllClient("本周超市薯片半价,时间2025/6/11 14:00:00结束,当前时间: " + LocalDateTime.now());
    }

}
