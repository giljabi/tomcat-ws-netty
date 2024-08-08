package kr.giljabi.gateway.request.dto;

import lombok.Getter;

/**
 * @Author : eahn.park@gmail.com
 * @Date : 2024.05.17
 * @Description
 */
@Getter
public class CommandRequestDTO {
    private String command;
    private String terminalId;
    private String script;
}
