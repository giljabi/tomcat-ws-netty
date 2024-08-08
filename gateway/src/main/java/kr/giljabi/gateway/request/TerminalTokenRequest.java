package kr.giljabi.gateway.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
@NoArgsConstructor
public class TerminalTokenRequest {
    private String userId;
    private String password;
}
