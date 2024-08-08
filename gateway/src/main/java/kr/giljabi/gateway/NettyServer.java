package kr.giljabi.gateway;

import kr.giljabi.gateway.nettyhandler.NettyServiceHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class NettyServer {
    private final NettyServiceHandler serviceHandler;

    @Value("${netty.tcp.port}")
    private int tcpPort;

    @Value("${netty.boss.thread.count}")
    private int bossThreadCount;

    @Value("${netty.worker.thread.count}")
    private int workkerThreadCount;

    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(bossThreadCount);
        EventLoopGroup workerGroup = new NioEventLoopGroup(workkerThreadCount);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000) //10sec
                        .option(ChannelOption.SO_BACKLOG, 500)             //서버 소켓이 처리할 수 있는 대기 중인 연결 요청의 최대 큐 길이
                            .childOption(ChannelOption.TCP_NODELAY, true)  //Nagle 알고리즘을 비활성화하고, 데이터 패킷의 전송 지연을 최소화
                            .childOption(ChannelOption.SO_LINGER, 0)       //소켓 닫힐 때 대기하지 않고 즉시 반환
                            .childOption(ChannelOption.SO_KEEPALIVE, true) //주기적으로 TCP Keepalive 메시지를 보내 연결이 살아있는지 확인
                            .childOption(ChannelOption.SO_REUSEADDR, true) //포트나 주소가 아직 사용 중인 상태에서도 소켓을 바인딩
                    .handler(new LoggingHandler(LogLevel.INFO))                  //서버 자체 로깅
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception { //연결된 각 클라이언트 소켓 채널에 대해 초기화할 핸들러
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
                            pipeline.addLast(new LineBasedFrameDecoder(8192)); //8192보다 큰 경우 예외발생
                            pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                            pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                            pipeline.addLast(serviceHandler);
                        }

                    });

            b.bind(tcpPort).sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}