package kr.giljabi.gateway.nettyhandler;

import com.google.gson.Gson;
import kr.giljabi.gateway.nettyhandler.sr.CommonHeader;
import kr.giljabi.gateway.nettyhandler.sr.LoginRecvDTO;
import kr.giljabi.gateway.nettyhandler.sr.LoginSendDTO;
import kr.giljabi.gateway.model.BoothStatus;
import kr.giljabi.gateway.model.NettyChannel;
import kr.giljabi.gateway.repository.BoothStatusRepository;
import kr.giljabi.gateway.repository.ChannelRepository;
import kr.giljabi.gateway.repository.NettyChannelRepository;
import kr.giljabi.gateway.util.*;
import kr.giljabi.gateway.websocket.StompController;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @Author eahn.park@gmail.com
 * @Date 2024.04.25
 * Netty 데이터 수신 후 처리
 */
@Slf4j
@Sharable
@Component
public class NettyServiceHandler extends ChannelInboundHandlerAdapter {
    private final StompController stompController;

    //netty channel 정보
    private static ChannelRepository channelRepository = new ChannelRepository();

    private final JwtProvider jwtProvider;
    private final NettyChannelRepository nettyChannelRepository;
    private final BoothStatusRepository boothStatusRepository;

    @Autowired
    public NettyServiceHandler(JwtProvider jwtProvider,
                               NettyChannelRepository nettyChannelRepository,
                               BoothStatusRepository boothStatusRepository,
                               StompController stompController) {
        this.stompController = stompController;
        this.jwtProvider = jwtProvider;
        this.nettyChannelRepository = nettyChannelRepository;
        this.boothStatusRepository = boothStatusRepository;

        //테이블에 있는 channel 정보 삭제
        removeAllChannelDatabase();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("channelActive address : {}", ctx.channel().remoteAddress().toString());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("channelInactive address : {}", ctx.channel().remoteAddress().toString());
        channelRepository.removeByChannel(ctx.channel());
        removeChannelDatabase(ctx.channel().id().asShortText());
        log.info("size : {}", channelRepository.getTerminalIdChannelMap().size());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object o) {
        Long startTime = System.currentTimeMillis();
        ctx.channel().attr(channelRepository.LAST_READ_TIME).set(System.currentTimeMillis());

        String msg = (String) o;
        CommonHeader recvHeader = new Gson().fromJson(msg, CommonHeader.class);
        CommonHeader sendHeader = CommonHeader.builder()    //응답용 기본값 설정
                .command(recvHeader.getCommand())
                .srType(SendRecvCode.response.name())
                .terminalId(recvHeader.getTerminalId())
                .time(CommonUtils.getCurrentTime(""))
                .data(recvHeader.getData())
                .build();

        try {
            log.info("channelRead : {}", recvHeader.toString());

            switch (recvHeader.getCommand()) {
                case "login":
                    login(ctx, recvHeader);
                    break;
                case "reboot":
                case "shutdown":
                case "health":
                case "trans":
                case "cmd":
                    //수신 후 상태 table update하고 응답은 하지 않아야 함
                    String ip = ctx.channel().remoteAddress().toString();
                    commonReceiver(recvHeader, ip.substring(1, ip.indexOf(":")));
                    break;
                default:
                    log.error("command not found : {}", recvHeader.getCommand());
                    sendHeader.setStatus(ErrorCode.UNKNOWN_COMMAND.getStatus());
                    sendHeader.setMessage(ErrorCode.UNKNOWN_COMMAND.getMessage());
                    sendHeader.setData(recvHeader.getData());
                    channelWrite(ctx.channel(), sendHeader,
                            this.getClass().getName() + "channelRead");
                    break;
            }
        } catch (Exception e) {
            log.error("socketSignIn Exception : {}", e.getMessage());
            e.printStackTrace();
            sendHeader.setStatus(ErrorCode.UNKNOWN_EXCEPTION.getStatus());
            sendHeader.setMessage(ErrorCode.UNKNOWN_EXCEPTION.getMessage());
            sendHeader.setData(recvHeader.getData());

            channelWrite(ctx.channel(), sendHeader,
    this.getClass().getName() + "channelRead");
        }
        ReferenceCountUtil.safeRelease(msg);
        Long endTime = System.currentTimeMillis();
        channelRepository.setLastProcessingTime(endTime - startTime);
    }


    //결과만 받으므로 바디부 처리는 없음
    private void commonReceiver(CommonHeader header, String publicIp) {
        BoothStatus boothStatus = new BoothStatus();
        boothStatus.setStatus(header.getStatus());
        boothStatus.setServerTime(CommonUtils.getTimeStamp());    //수신일시
        boothStatus.setClientSendTime(CommonUtils.stringToTimeStamp(header.getTime())); //클라이언트 송신일시
        boothStatus.setLastCommand(header.getCommand());
        boothStatus.setTerminalId(header.getTerminalId());
        boothStatus.setPublicIp(publicIp);
        boothStatus.setLocalIp(header.getLocalIp());
        boothStatus.setRecvMessage(header.getMessage());

        //select, update가 발생하지만 영향이 거의없어 무시함...
        boothStatusRepository.save(boothStatus);

        //websocket으로 전송
        stompController.sendMsg(new Gson().toJson(header));

/*        //update를 사용하면 select를 하지 않아도 되므로 성능이 향상되나....?? 좀 더 고민이 필요...
        boothStatusRepository.updateStatusByTerminalId(
                boothStatus.getClientSendTime(),
                boothStatus.getLocalIp(),
                boothStatus.getPublicIp(),
                boothStatus.getTerminalId(),
                boothStatus.getStatus(),
                boothStatus.getServerTime(),
                boothStatus.getLastCommand(),
                boothStatus.getRecvMessage()
        );*/
    }

    private void login(ChannelHandlerContext channelHandlerContext,
                                CommonHeader commonRequest) {
        LoginRecvDTO loginDTO = new Gson().fromJson(commonRequest.getData().toString(), LoginRecvDTO.class);
        if(loginDTO == null) {
            log.error("loginDTO is null");
            return;
        }
        log.info("jwt : {}", loginDTO.getToken());
        String jwt = loginDTO.getToken();

        if (StringUtils.hasText(jwt) && jwtProvider.validateToken(jwt)) {
            Channel channel = channelHandlerContext.channel();
            channel.attr(channelRepository.CREATED_TIME).set(System.currentTimeMillis());

            String terminalId = jwtProvider.getTerminalId(jwt);   //식별자
            channelRepository.addChannel(terminalId, channel);

            addChannelDatabase(channel, terminalId);

            LoginSendDTO sendDTO = LoginSendDTO.builder().build();
            CommonHeader commonHeader = CommonHeader.builder()
                    .status(ErrorCode.STATUS_SUCCESS.getStatus())
                    .command(CommandCode.login.name())
                    .srType(SendRecvCode.response.name())
                    .terminalId(terminalId)
                    .message("로그인 성공")
                    .data(sendDTO)
                    .build();
            channelWrite(channel, commonHeader,
                    this.getClass().getName() + ".login");
        }
    }

    private void channelWrite(Channel channel, CommonHeader commonHeader, String handlerName) {
        channel.attr(channelRepository.LAST_WRITE_TIME).set(System.currentTimeMillis());
        channel.attr(channelRepository.LAST_HANDLER).set(handlerName);

        String sendMessage = new Gson().toJson(commonHeader);
        channel.writeAndFlush(sendMessage + System.getProperty("line.separator"));

        //websocket으로 전송
        stompController.sendMsg(new Gson().toJson(commonHeader));
    }

    private void addChannelDatabase(Channel channel, String terminalId) {
        NettyChannel nettyChannel = new NettyChannel();
        nettyChannel.setTerminalId(terminalId);
        nettyChannel.setChannelId(channel.id().asShortText());
        nettyChannel.setChannel(channel.toString());
        nettyChannelRepository.save(nettyChannel);
    }

    private void removeChannelDatabase(String channelId) {
        NettyChannel nettyChannel = nettyChannelRepository.findByChannelId(channelId);
        if (nettyChannel == null) {
            log.error("channel table not found: {}", channelId);
            return;
        }
        nettyChannelRepository.deleteById(nettyChannel.getTerminalId());
        log.info("channel table delete: {}", nettyChannel.getTerminalId());
    }

    private void removeAllChannelDatabase() {
        nettyChannelRepository.deleteAll();
        log.info("channel table delete all");
    }

    /**
     * NettyServer.start, initChannel등에서 발생하는 NettyException 처리
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("An error occurred: " + cause.getMessage());
        String terminalId = channelRepository.getTerminalIdByChannel(ctx.channel());
        CommonHeader commonHeader = CommonHeader.builder()
                .status(ErrorCode.UNKNOWN_EXCEPTION.getStatus())
                .command(CommandCode.inactive.name())
                .srType(SendRecvCode.response.name())
                .terminalId(terminalId)
                .message(cause.getMessage())
                .data("")
                .build();
        channelWrite(ctx.channel(), commonHeader,
                this.getClass().getName() + "exceptionCaught");
    }

    public ChannelRepository getChannelRepository() {
        return channelRepository;
    }
}
