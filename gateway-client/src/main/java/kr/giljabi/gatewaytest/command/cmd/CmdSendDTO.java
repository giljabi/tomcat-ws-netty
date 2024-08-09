package kr.giljabi.gatewaytest.command.cmd;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Author : eahn.park@gmail.com
 */
@Getter
@Setter
public class CmdSendDTO {
    private String cmd;
    private List<FileInfo> fileList;
}
