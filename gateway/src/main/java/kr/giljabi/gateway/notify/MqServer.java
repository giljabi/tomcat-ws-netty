package kr.giljabi.gateway.notify;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Data
@Table(name = "mq_server")
public class MqServer {
    @Column(name = "sendserver", length = 10)
    private String sendserver;

    @Id
    @Column(name = "recvserver", length = 10)
    private String recvserver;

    //@JsonIgnore
    @Column(name = "datetime", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime dateTime = LocalDateTime.now();

    @Column(name = "message")
    private String message;

    @Override
    public String toString() {
        return "MqServer{" +
                "sendServer='" + sendserver + '\'' +
                ", recvServer='" + recvserver + '\'' +
                ", dateTime=" + dateTime + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
