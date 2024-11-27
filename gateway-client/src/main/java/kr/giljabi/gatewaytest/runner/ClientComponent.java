package kr.giljabi.gatewaytest.runner;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author : eahn.park@gmail.com
 */
@Component
@Getter
public class ClientComponent {
    @Value("${gateway.was.port}")
    private String wasPort;

    @Value("${gateway.was.targetIp}")
    private String wasTargetIp;

    @Value("${gateway.was.protocol}")
    private String wasProtocol;

    @Value("${gateway.netty.port}")
    private String nettyPort;

    @Value("${gateway.netty.targetIp}")
    private String nettyTargetIp;

}

