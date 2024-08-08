package kr.giljabi.gatewaytest.command.cmd;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author : eahn.park@gmail.com
 */
@Getter
@Setter
public class CmdRecvDTO {
    private String cmd;
    private String path;
}
