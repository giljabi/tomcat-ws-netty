package kr.giljabi.gateway.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "userinfo")
public class UserInfo {

    @Id
    @Column(name = "user_id", nullable = false, length = 20)
    private String userId;

    @Column(name = "user_nm", nullable = false, length = 20)
    private String userName;

    @Column(name = "user_pw", nullable = false, length = 100)
    private String password;

    @Column(name = "terminal_id")
    private Integer terminalId;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @Column(name = "created_by", length = 20)
    private String createdBy;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "updated_by", length = 20)
    private String updatedBy;
}
