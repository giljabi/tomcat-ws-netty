package kr.giljabi.gatewaytest.util;

import lombok.Getter;

@Getter
public enum ErrorCode {
    STATUS_SUCCESS("0000", "정상 처리 되었습니다."),
    STATUS_FAILURE("0001", "알 수 없는 장애가 발생하였습니다."),
    STATUS_EXCEPTION("0002", ""),   //예외 메세지를 사용
    EMPTY_TOKEN("0003", "토큰이 없습니다."),
    STATUS_USERNOTFOUND("0004", "사용자를 찾을 수 없습니다.");

    private final String status;
    private final String message;

    ErrorCode(String status, String message) {
        this.status = status;
        this.message = message;
    }
}