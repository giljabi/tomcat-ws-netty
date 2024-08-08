package kr.giljabi.gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.giljabi.gateway.nettyhandler.NettyServiceHandler;
import kr.giljabi.gateway.nettyhandler.sr.CommonHeader;
import kr.giljabi.gateway.notify.MqServer;
import kr.giljabi.gateway.notify.MqServerService;
import kr.giljabi.gateway.notify.NotifyType;
import kr.giljabi.gateway.repository.ChannelRepository;
import kr.giljabi.gateway.request.TerminalTokenRequest;
import kr.giljabi.gateway.request.dto.CommandRequestDTO;
import kr.giljabi.gateway.response.TokenResponse;
import kr.giljabi.gateway.service.UserInfoService;
import kr.giljabi.gateway.util.CommandCode;
import kr.giljabi.gateway.util.CommonUtils;
import kr.giljabi.gateway.util.ErrorCode;
import kr.giljabi.gateway.util.SendRecvCode;
import io.netty.channel.Channel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ServerController {

    private final MqServerService mqServerService;
    private final UserInfoService userInfoService;
    private final NettyServiceHandler serviceHandler;

    @Operation(summary = "gateway를 통하여 Netty agent로 메시지 전송", description = "Agent 업무정의 후 사용함<br>" +
            "command: reboot, shutdown<br>" +
            "terminalId: ALL, 1001~<br>" +
            "script: dir (windows command), <span style='color:red;'>dir 옵션으로 single back slash 사용불가, slash를 사용해야 함</span><br>" +
            "{\n" +
            "  \"command\": \"cmd\",\n" +
            "  \"terminalId\": \"ALL\",\n" +
            "  \"script\": \"dir x:/tmp\"\n" +
            "}")
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json")})
    @PostMapping("/api/toAgent")
    public CommonHeader toAgent(@RequestBody CommandRequestDTO commandDTO) {
        log.info("toAgent: message={}", commandDTO);

        MqServer mqServer = mqServerService.notify(NotifyType.ADMIN.name(),
                NotifyType.GATEWAY.name(),
                commandDTO);
        CommonHeader response = CommonHeader.builder()
                .command(CommandCode.none.name()) //용도에 따라 설정
                .status(ErrorCode.STATUS_SUCCESS.getStatus())
                .srType(SendRecvCode.response.name())
                .terminalId(mqServer.getRecvserver())
                .message(ErrorCode.STATUS_SUCCESS.getMessage())
                .data(mqServer)
                .build();
        return response;
    }

    @Operation(summary = "Netty agent 로그인 토큰 발급", description = "ID, Password 또는 ??? 서버 데이터 모델링 후 정의")
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json")})
    @PostMapping("/api/token")
    public CommonHeader getToken(@RequestBody TerminalTokenRequest request) {
        TokenResponse tokenResponse = userInfoService.findByUserId(request);
        CommonHeader response = CommonHeader.builder()
                .command(CommandCode.login.name())
                .status(ErrorCode.STATUS_SUCCESS.getStatus())
                .srType(SendRecvCode.response.name())
                .terminalId("")
                .message(ErrorCode.STATUS_SUCCESS.getMessage())
                .data(tokenResponse)
                .build();

        if(tokenResponse == null) {
            response.setStatus(ErrorCode.STATUS_USERNOTFOUND.getStatus());
            response.setMessage(ErrorCode.STATUS_USERNOTFOUND.getMessage());
            return response;
        }
        return response;
    }

    /**
     * 채널 모니터링에 사용
     *      정상 "channel": "1001=[id: 0x23395808, L:/127.0.0.1:8090 - R:/127.0.0.1:12381]",
     *    비정상 "channel": "1001=[id: 0x23395808, L:/127.0.0.1:8090 ! R:/127.0.0.1:12381]",
     *
     * @return
     */
    @Operation(summary = "Netty agent channel monitor", description = "Agent가 로그인되어 있어야 응답 데이터가 있음" +
            "\"channel\": \"1001=[id: 0x23395808, L:/127.0.0.1:8090 - R:/127.0.0.1:12381]\"에 있는 '-'는 정상이며 " +
            "'!'는 비정상을 의미함")
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json")},
            description = "[{<br>" +
                    "    \"lastReadTime\": \"2024-04-26T15:41:20.865\",<br>" +
                    "    \"channel\": \"1003=[id: 0x6d41a621, L:/127.0.0.1:8090 - R:/127.0.0.1:14603]\",<br>" +
                    "    \"terminalId\": \"1003\",<br>" +
                    "    \"id\": \"6d41a621\",<br>" +
                    "    \"lastWriteTime\": \"2024-04-26T15:41:20.873\"<br>" +
                    "  }]<br>")
    @GetMapping("/api/getChannel")
    public String getChannel() {
        log.info("getChannel");
        ChannelRepository channelRepository = serviceHandler.getChannelRepository();
        List<Map<String, Object>> channelList = new ArrayList<>();

        for (Map.Entry<String, Channel> entry : channelRepository.getTerminalIdChannelMap().entrySet()) {
            Map<String, Object> channelDetails = new HashMap<>();
            channelDetails.put("terminalId", entry.getKey());
            channelDetails.put("channelId", entry.getValue().id().asShortText());  // 채널 ID
            channelDetails.put("channel", entry.toString());
            channelDetails.put("lastReadTime",
                    CommonUtils.longTimeToTimeStamp(entry.getValue().attr(channelRepository.LAST_READ_TIME).get(),
                            CommonUtils.DEFAULT_TIMESTAMP_FORMAT));
            channelDetails.put("lastWriteTime",
                    CommonUtils.longTimeToTimeStamp(entry.getValue().attr(channelRepository.LAST_WRITE_TIME).get(),
                            CommonUtils.DEFAULT_TIMESTAMP_FORMAT));
            channelDetails.put("createdTime",
                    CommonUtils.longTimeToTimeStamp(entry.getValue().attr(channelRepository.CREATED_TIME).get(),
                            CommonUtils.DEFAULT_TIMESTAMP_FORMAT));
            channelDetails.put("lastHandlerName",
                    entry.getValue().attr(channelRepository.LAST_HANDLER).get());
            channelList.add(channelDetails);
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            log.info("channelRepository={}", channelRepository);
            return mapper.writeValueAsString(channelList);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }

        return "";
    }
}