package kr.giljabi.gateway.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "boothstatus", schema = "public")
@Getter @Setter
public class BoothStatus {

    @Id
    @Column(name = "terminalid", nullable = false, length = 32)
    private String terminalId;

    @Column(name = "servertime", nullable = true)
    private Timestamp serverTime;

    @Column(name = "status", nullable = false, length = 4)
    private String status;

    @Column(name = "clientsendtime", nullable = true)
    private Timestamp clientSendTime;

    @Column(name = "localip", nullable = true, length = 64)
    private String localIp;

    @Column(name = "publicip", nullable = true, length = 64)
    private String publicIp;

    @Column(name = "recvmessage", nullable = true, length = 1024)
    private String recvMessage;

    @Column(name = "lastcommand", nullable = false, length = 16)
    private String lastCommand;

}
