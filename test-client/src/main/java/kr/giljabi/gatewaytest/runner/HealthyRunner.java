package kr.giljabi.gatewaytest.runner;

import kr.giljabi.gatewaytest.command.CommandHealthCode;
import kr.giljabi.gatewaytest.dto.CommonHeader;
import kr.giljabi.gatewaytest.util.CommonUtils;
import kr.giljabi.gatewaytest.util.SendRecvCode;
import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * @Author : eahn.park@gmail.com
 * 주기적으로 서버로 송신하여 연결되어 있음을 알리는 폴링정보
 * 클라이언트 상태를 확인하여 전송하는 용도로 사용할 수 있음
 */
@Slf4j
public class HealthyRunner implements Runnable {
    private final String NEWLINE = System.getProperty("line.separator");
    private CommonHeader commonResponse;
    private ChannelHandlerContext ctx;
    private volatile boolean running = true; // Control flag for the loop

    public HealthyRunner(ChannelHandlerContext ctx, CommonHeader response) {
        this.ctx = ctx;
        this.commonResponse = response;

        // Add a listener to the channel to detect disconnection
        ctx.channel().closeFuture().addListener(future -> {
            stop(); // Stop the thread when the channel is closed
        });
    }
    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            System.out.println("HealthyRunner run:" + running);
            try {
                String message = randomCommand();
                ctx.channel().writeAndFlush(message + NEWLINE);
                log.info(ctx.channel() + " Healthy send    : " + message);
                //Thread.sleep(new Random().nextInt(5000));
                Thread.sleep(1000);
            } catch (Exception e) {
                //e.printStackTrace();
                System.out.println("HealthyRunner Exception:" + running);
            }
        }
    }

    //랜덤으로 명령어를 생성하여 전송
    public String randomCommand() {
        Random random = new Random();
        CommandHealthCode[] codes = CommandHealthCode.values();
        CommandHealthCode randomCode = codes[random.nextInt(codes.length)];
        commonResponse.setTime(CommonUtils.getCurrentTime("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        commonResponse.setCommand(randomCode.name());
        commonResponse.setMessage(randomCode.name());
        commonResponse.setSrType(SendRecvCode.response.name());
        //필요한 정보가 발생하면 data에 추가해서 전송할 수 있음
        String message = new Gson().toJson(commonResponse);
        return message;
    }
}
