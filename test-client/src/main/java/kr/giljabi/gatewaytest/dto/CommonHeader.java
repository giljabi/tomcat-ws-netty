package kr.giljabi.gatewaytest.dto;

import kr.giljabi.gatewaytest.util.CommonUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * server --> client response
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
    private String time = CommonUtils.getCurrentTime("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private String localIp;
    private String message;
    private Object data;


/*    public CommonResponse(Object data) {
        this.status = ErrorCode.STATUS_SUCCESS.getStatus();
        this.command = CommandCode.none.name(); //default
        this.srType = SendRecvCode.response.name(); //default
        this.message = ErrorCode.STATUS_SUCCESS.getMessage();
        this.data = data;
    }

    public CommonResponse(ErrorCode errorCode) {
        this.status = errorCode.getStatus();
        this.command = CommandCode.none.name(); //default
        this.srType = SendRecvCode.response.name(); //default
        this.message = errorCode.getMessage();
    }*/
}
