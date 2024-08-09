package kr.giljabi.gatewaytest.command;

import lombok.Getter;

/**
 * @Author: eahn.park@gmail.com
 * commandHealthCode와 구분해서 사용하기 위함
 */
@Getter
public enum CommandCode {
    reboot,
    shutdown,
    health,
    login, //client 로그인에서만 사용됨
    none //아무것도 하지 않음
}