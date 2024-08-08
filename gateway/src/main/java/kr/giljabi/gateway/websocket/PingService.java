package kr.giljabi.gateway.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * ping pong 프로토콜은 브라우저 수준에서 사용되므로 서버에서는 사용하지 않음
 * 브라우저
 * stompClient.heartbeat.outgoing = 60 * 1000;  // 클라이언트에서 서버로의 하트비트 간격
 * stompClient.heartbeat.incoming = 60 * 1000;  // 서버에서 클라이언트로의 하트비트 간격
 * 서버
 * .setTaskScheduler(taskScheduler).setHeartbeatValue(new long[]{60000, 60000});
 *
 * @Author : eahn.park@gmail.com
 * @Date : 2024. 5. 3.
 * @deprecated
 */
//@Service
//@EnableScheduling
@Slf4j
public class PingService {

    //private final SimpMessagingTemplate template;

    @Autowired
    public PingService(SimpMessagingTemplate template) {
        //this.template = template;
    }
/*
    @Scheduled(fixedRate = 60 * 1000)
    public void sendPing() {
        template.convertAndSend("/topic/ping", "ping");
        log.info("Ping sent to clients");
    }*/
}