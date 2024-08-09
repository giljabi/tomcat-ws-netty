package kr.giljabi.gatewaytest.command;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Setter
@Getter
public class CommandParam {
    private String username;
    private String password;
    private String terminalId;

}
