package kr.giljabi.gateway.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * websocket 설명 https://growth-coder.tistory.com/157
 * @Author : eahn.park@gmail.com
 * @Date : 2024. 5. 3.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.initialize();

        registry.enableSimpleBroker("/topic")   //메시지 브로커가 /topic으로 시작하는 메시지를 클라이언트로 브로드캐스팅 (1:N 통신)
                .setTaskScheduler(taskScheduler).setHeartbeatValue(new long[]{60 * 1000, 60 * 1000});
        registry.setApplicationDestinationPrefixes("/app");// --> @MessageMapping("/app/hello")
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").withSockJS();
    }
}