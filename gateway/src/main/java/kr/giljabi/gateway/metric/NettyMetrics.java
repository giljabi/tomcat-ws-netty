package kr.giljabi.gateway.metric;

import kr.giljabi.gateway.nettyhandler.NettyServiceHandler;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
/**
 * @Author : eahn.park@gmail.com
 * @Date : 2024.04.30
 * @Description
http://localhost:8082/actuator/prometheus
gateway_netty_connections_active{application="Gateway-netty",} 2.0
 */
@Component
public class NettyMetrics {
    @Autowired
    private NettyServiceHandler serviceHandler;

    public NettyMetrics(MeterRegistry registry) {
        registry.gauge("gateway.netty.channel.connections", this, NettyMetrics::calculateActiveConnections);
        registry.gauge("gateway.netty.channel.lastprocessingtime", this, NettyMetrics::getLastProcessingTime);
    }
    private int calculateActiveConnections() {
        return serviceHandler.getChannelRepository().getTerminalIdChannelMap().size();
    }
    private Long getLastProcessingTime() {
        return serviceHandler.getChannelRepository().getLastProcessingTime();
    }


}