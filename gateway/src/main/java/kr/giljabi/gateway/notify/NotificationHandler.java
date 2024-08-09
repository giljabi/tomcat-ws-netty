package kr.giljabi.gateway.notify;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import kr.giljabi.gateway.nettyhandler.NettyServiceHandler;
import kr.giljabi.gateway.nettyhandler.sr.CommonHeader;
import kr.giljabi.gateway.repository.ChannelRepository;
import kr.giljabi.gateway.util.SendRecvCode;
import kr.giljabi.gateway.websocket.StompController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.PGNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import io.netty.channel.Channel;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationHandler implements Consumer<PGNotification>{

    @Autowired
    private final StompController stompController;

    @Autowired
    private final MqServerService mqServerService;

    @Autowired
    private NettyServiceHandler serviceHandler;

    private String newLine = System.getProperty("line.separator");
    @Override
    @Transactional
    public void accept(PGNotification t) {
        log.info("Notification received: pid={}, name={}, param={}",
                t.getPID(), t.getName(), t.getParameter());
        MqServer mqServer = mqServerService.findByRecvserver(t.getParameter());
        if(mqServer != null) {
            log.info("NotificationHandler details: {}", mqServer.toString());
            sendConnectedClients(mqServer);
        }
    }

    //클라이언트에 통지
    private void sendConnectedClients(MqServer mqServer) {
        try {
            CommonHeader sender = new Gson().fromJson(mqServer.getMessage(), CommonHeader.class);
            CommonHeader request = CommonHeader.builder()
                    .status("0000")
                    .command(sender.getCommand())
                    .terminalId(sender.getTerminalId())
                    .srType(SendRecvCode.request.name())
                    .message("Success")
                    .data(sender.getData())
                    .build();

            log.info("commandDto: {}", new Gson().toJson(request));

            if (request == null) {
                log.info("Invalid message: {}", request.toString());
                return;
            }
            ChannelRepository channelRepository = serviceHandler.getChannelRepository();
            log.info("connected client count={}", channelRepository.getTerminalIdChannelMap().size());

            if (sender.getTerminalId().equalsIgnoreCase("ALL")) { //전체
                for (Channel channel : channelRepository.getTerminalIdChannelMap().values()) {
                    //채널을 이용해 terminalId를 가져온다.
                    request.setTerminalId(channelRepository.getTerminalIdByChannel(channel));
                    sendClient(new Gson().toJson(request), channel, "sendConnectedClients");
                }
            } else {
                Channel channel = channelRepository.getTerminalIdChannelMap().get(sender.getTerminalId());
                sendClient(new Gson().toJson(request), channel, "sendConnectedClients");
            }
        } catch (JsonSyntaxException jse) {
            log.info("-----------------JsonSyntaxException-----------------");
            log.info(jse.getMessage());
        } catch(Exception e) {
            log.info(e.getMessage());
        }
    }

    private void sendClient(String message, Channel channel, String handlerName) {
        channel.attr(ChannelRepository.LAST_WRITE_TIME).set(System.currentTimeMillis());
        channel.attr(ChannelRepository.LAST_HANDLER).set(this.getClass().getName() +
                "." + handlerName);

        if (channel != null && channel.isActive()) {
            String response = message + newLine;
            //serviceHandler.channelWrite(channel, response, "sendClient"); //이렇게 사용하면 안됨...
            channel.writeAndFlush(response);

            //웹에서 모니터링을 할 필요가 있는 경우에만 사용
            //stompController.sendMsg(response);
            log.info("sendClient={}", response);
        } else {
            log.warn("Inactive channel found: {}", channel);
        }
    }
}