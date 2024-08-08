package kr.giljabi.gateway.util;

import lombok.Getter;

@Getter
public enum CommandCode {
    reboot,
    shutdown,
    health,
    cmd,    //원격 cmd(dir, ls, ps 등)
    login, //client 로그인에서만 사용됨
    trans, //거래중
    inactive, //클라이언트 연결이 끊어질때 사용됨
    none //아무것도 하지 않음
}