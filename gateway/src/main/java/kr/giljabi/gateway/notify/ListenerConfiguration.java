package kr.giljabi.gateway.notify;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

/**
 * @Author eahn.park@gmail.com
 * @Date 2024.04.25
 * @Description
 *
 */
@Configuration
@Slf4j
public class ListenerConfiguration {

    //채널, 대소문자 구분되므로 주의해야 함
    @Value("${gateway.server-topic}")
    private String topic;

    //createNotificationHandler는 메시지를 받아서 처리하는 쓰레드
    @Bean
    CommandLineRunner startListener(NotifierService notifier, NotificationHandler handler) {
        return (args) -> {
            log.info("Starting server message listener thread: {}", topic);
            Runnable listener = notifier.createNotificationHandler(handler);
            Thread t = new Thread(listener, topic);
            t.start();
        };
    }
}