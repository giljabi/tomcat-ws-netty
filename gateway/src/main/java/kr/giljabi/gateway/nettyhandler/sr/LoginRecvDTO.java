package kr.giljabi.gateway.nettyhandler.sr;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author eahn.park@gmail.com
 * @Date 2024.04.25
 * Agent에서 로그인할때 수신하는 바디부분
 */
@Data
public class LoginRecvDTO implements Serializable {
    private String token;

}
