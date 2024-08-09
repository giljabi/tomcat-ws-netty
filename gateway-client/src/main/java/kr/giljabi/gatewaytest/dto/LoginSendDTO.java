package kr.giljabi.gatewaytest.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Netty login request
 */
@Data
public class LoginSendDTO implements Serializable {
    private String token;

    public LoginSendDTO(String token) {
        this.token = token;
    }
}
