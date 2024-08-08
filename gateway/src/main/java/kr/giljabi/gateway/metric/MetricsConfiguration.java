package kr.giljabi.gateway.metric;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @Author : eahn.park@gmail.com
 * @Date : 2024.04.30
 * @Description
http://localhost:8082/actuator/prometheus
gateway_netty_connections_active{application="Gateway-netty",}
 application 구분자 추가
 */
@Component
public class MetricsConfiguration {

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config().commonTags("application", "Gateway-netty");
    }

}