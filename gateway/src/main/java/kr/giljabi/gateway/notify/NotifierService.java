package kr.giljabi.gateway.notify;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.util.function.Consumer;

/**
 * @Author eahn.park@gmail.com
 * @Date 2024.04.25
 * @Description
 * PostgreSQL NOTIFY를 이용한 메시지 전달
 */
@Slf4j
@RequiredArgsConstructor
public class NotifierService {

    @Value("${gateway.server-topic}")
    private String MESSAGE_CHANNEL;
    private final JdbcTemplate tpl;

    @Transactional
    public void notifyMgServerMessage(MqServer mqServer) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String message = objectMapper.writeValueAsString(mqServer);
            log.info("notifyMgServerMessage: message={}", message);
            tpl.execute("NOTIFY " + MESSAGE_CHANNEL + ", '" + message + "'");
        } catch (Exception e) {
            log.error("notifyMgServerMessage: error", e);
        }
    }

    //createNotificationHandler는 메시지를 받아서 처리하는 쓰레드
    public Runnable createNotificationHandler(Consumer<PGNotification> consumer) {
        return () -> {
            tpl.execute((Connection c) -> {
                log.info("notificationHandler: sending LISTEN command...");
                c.createStatement().execute("LISTEN " + MESSAGE_CHANNEL);
                PGConnection pgconn = c.unwrap(PGConnection.class); // pgconn은 c의 PGConnection 인터페이스를 언래핑한 객체, close 없음
                while(!Thread.currentThread().isInterrupted()) {
                    PGNotification[] nts = pgconn.getNotifications(10000);
                    if ( nts == null || nts.length == 0 ) {
                        continue;
                    }
                    for( PGNotification nt : nts) {
                        consumer.accept(nt);
                    }
                }
                return 0;
            });
        };
    }
}