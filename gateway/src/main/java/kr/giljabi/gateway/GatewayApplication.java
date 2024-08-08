package kr.giljabi.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(GatewayApplication.class);
        ConfigurableApplicationContext context = application.run(args);
        NettyServer nettyServer = context.getBean(NettyServer.class);
        nettyServer.start();
    }

}