package kr.giljabi.gatewaytest.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class BoothTokenRequest {
    private String userId;
    private String password;
}
