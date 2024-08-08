package kr.giljabi.gateway.notify;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @Author eahn.park@gmail.com
 * @Date 2024.04.25
 * @Description
   송신, 수신자를 지정하는 코드
 */
@RequiredArgsConstructor
@Getter
public enum NotifyType {
    API,
    ADMIN,
    GATEWAY,
}