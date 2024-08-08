package kr.giljabi.gateway.nettyhandler.sr;

import lombok.Data;

/**
 * @Author eahn.park@gmail.com
 * @Date 2024.04.25
 * send, recv에서 사용되는 DATA 부분, 업무에 따라 다양하게 사용할 수 있음
 */
@Data
public class CommonDATA {
    private String time;

}
