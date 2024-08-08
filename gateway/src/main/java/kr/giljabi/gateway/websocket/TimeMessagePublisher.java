package kr.giljabi.gateway.websocket;

import kr.giljabi.gateway.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @Author : eahn.park@gmail.com
 * @Date : 2024. 5. 3.
 * 서버에서 클라이언트로 주기적으로 시간을 전송하는 서비스
 */
@Service
@EnableScheduling
@Slf4j
public class TimeMessagePublisher {

    @Autowired
    private SimpMessagingTemplate template;

    @Scheduled(fixedRate = 1000)
    public void publishTime() {
        String time = CommonUtils.getCurrentTime("");
        //log.info("Sending time: {}", time);
        template.convertAndSend("/topic/time", time);
    }
}