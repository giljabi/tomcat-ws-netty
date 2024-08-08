package kr.giljabi.gatewaytest.runner;

import kr.giljabi.gatewaytest.dto.CommonHeader;
import kr.giljabi.gatewaytest.dto.TokenResponse;
import com.google.gson.Gson;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;

/**
 * @Author : eahn.park@gmail.com
 */
public class ClientRunner implements Runnable {

    private String username;
    private String password;
    private String terminalId;
    private ClientComponent clientComponent;

    public ClientRunner(String username, String password, String terminalId, ClientComponent clientComponent) {
        this.username = username;
        this.password = password;
        this.terminalId = terminalId;
        this.clientComponent = clientComponent;
    }

    @Override
    public void run() {
        LoginService loginService = new LoginService(clientComponent);
        CommonHeader commonHeader = loginService.getToken(username, password);

        if(!commonHeader.getStatus().equalsIgnoreCase("0000")) {
            System.out.println("Error: " + commonHeader.getMessage());
            return;
        }
        Gson gson = new Gson();
        TokenResponse tokenResponse = gson.fromJson(commonHeader.getData().toString(), TokenResponse.class);

        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
                            pipeline.addLast(new LineBasedFrameDecoder(8192));  // 라인 구분자로 메시지를 분할
                            pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                            pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                            pipeline.addLast(new ClientTestHandler(tokenResponse.getAccessToken(), terminalId));
                        }
                    });

            Channel channel = bootstrap.connect("localhost",
                    Integer.parseInt(clientComponent.getNettyPort())).sync().channel();
            channel.closeFuture().await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Client was interrupted.");
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
