package kr.giljabi.gatewaytest.runner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author : eahn.park@gmail.com
 */
@Component
public class ClientComponent {
    @Value("${gateway.was.port}")
    private String wasPort;

    @Value("${gateway.netty.port}")
    private String nettyPort;

    public String getWasPort() {
        return wasPort;
    }
    public String getNettyPort() {
        return nettyPort;
    }

}
