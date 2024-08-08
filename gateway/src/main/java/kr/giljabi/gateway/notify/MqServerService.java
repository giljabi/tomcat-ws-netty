package kr.giljabi.gateway.notify;

import com.google.gson.Gson;
import kr.giljabi.gateway.nettyhandler.sr.CommonHeader;
import kr.giljabi.gateway.request.dto.CommandRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MqServerService {
    private final MqServerRepository mqServerRepository;
    private final NotifierService notifier;

    public MqServer notify(String notifySender, String notifyRecvier,
                           CommandRequestDTO commandRequestDTO) {
        CommonHeader request = CommonHeader.builder()
                .command(commandRequestDTO.getCommand())
                .status("0000")
                .srType("request")
                .terminalId(commandRequestDTO.getTerminalId())
                .message(commandRequestDTO.getCommand() + "를 전송합니다.")
                .data(commandRequestDTO.getScript())
                .build();

        MqServer mqServer = new MqServer();
        mqServer.setSendserver(notifySender);
        mqServer.setRecvserver(notifyRecvier);
        mqServer.setMessage(new Gson().toJson(request));
        mqServer.setDateTime(LocalDateTime.now());

        //db에 저장하고 trigger를 이용해 notify하는 방법과 db channel을 이용하는 방법이 있음, 여기서는 save를 사용함
        mqServer = mqServerRepository.save(mqServer); //by trigger
        //notifier.notifyMgServerMessage(mqServer);

        return mqServer;
    }

    //NotificationHandler accept에서 사용됨, findByRecvserver에서 mq_server 테이블에 데이터가 있어야 사용가능한데...
    public MqServer findByRecvserver(String recvMessage)  {
        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject jsonMsg = (JSONObject) jsonParser.parse(recvMessage);
            MqServer mqServer = mqServerRepository.findByRecvserver((String)jsonMsg.get("recvserver")); //recvserver로 조회
            return mqServer;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
