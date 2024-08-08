package kr.giljabi.gateway.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class StompController {
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/monitor/send") // /app/monitor/send
    public void sendMsg(@Payload String data){
        log.info("sendMsg : {}", data);
        simpMessagingTemplate.convertAndSend("/topic/monitor", data);
    }

/*    @MessageMapping("/ping")    // /app/ping
    public void processPingMessage(String message) {
        log.info("Received ping message: {}", message);
        simpMessagingTemplate.convertAndSend("/topic/pong", "pong");
        log.info("Send pong message: {}", "pong");
    }*/
}