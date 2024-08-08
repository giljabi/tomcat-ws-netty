package kr.giljabi.gateway.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.context.event.EventListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@Slf4j
public class StompWebSocketEvents {

    private final Map<String, String> sessionUsers = new ConcurrentHashMap<>();

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        // You can also access user-specific details if needed
        sessionUsers.put(sessionId, accessor.getUser().getName());
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        sessionUsers.remove(accessor.getSessionId());
    }

    public Map<String, String> getSessionUsers() {
        return sessionUsers;
    }
}