package kr.giljabi.gateway.nettyhandler.sr;

import kr.giljabi.gateway.util.CommonUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author eahn.park@gmail.com
 * @Date 2024.04.25
 * server <--> client 공통으로 사용하는 헤더 부분
 */
@Getter @Setter
@Builder
@ToString
public class CommonHeader {
    private String command;
    private String status;
    private String srType;
    private String terminalId;
    @Builder.Default // Lombok 빌더에서 기본값을 설정하기 위한 애너테이션 사용
    private String time = CommonUtils.getCurrentTime(CommonUtils.DEFAULT_TIMESTAMP_FORMAT);
    private String localIp; //수신정보에서만 사용됨
    private String message;
    private Object data; //업무에 따라 클래스를 만들어서 사용

}
