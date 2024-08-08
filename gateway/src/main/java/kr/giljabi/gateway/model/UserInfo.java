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

    @Column(name = "use_flag", nullable = false, length = 1)
    private char useFlag;

    @Column(name = "phone_no", length = 14)
    private String phoneNo;

    @Column(name = "company_id", length = 10)
    private String companyId;

    @Column(name = "branch_id", length = 10)
    private String branchId;

    @Column(name = "shop_id")
    private Integer shopId;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @Column(name = "created_by", length = 20)
    private String createdBy;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "updated_by", length = 20)
    private String updatedBy;
}