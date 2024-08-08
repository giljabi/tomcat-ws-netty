package kr.giljabi.gatewaytest.runner;

import kr.giljabi.gatewaytest.command.CommandHealthCode;
import kr.giljabi.gatewaytest.command.cmd.Cmd;
import kr.giljabi.gatewaytest.command.cmd.CmdSendDTO;
import kr.giljabi.gatewaytest.command.cmd.FileInfo;
import kr.giljabi.gatewaytest.dto.CommonHeader;
import kr.giljabi.gatewaytest.dto.LoginSendDTO;
import kr.giljabi.gatewaytest.command.CommandCode;
import kr.giljabi.gatewaytest.util.CommonUtils;
import kr.giljabi.gatewaytest.util.ErrorCode;
import kr.giljabi.gatewaytest.util.SendRecvCode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author : eahn.park@gmail.com
 */
public class ClientTestHandler extends ChannelInboundHandlerAdapter {
    private final String NEWLINE = System.getProperty("line.separator");
    private String token;
    private String terminalId;
    List<HealthyRunner> healthyRunnerList = new ArrayList<HealthyRunner>();

    public ClientTestHandler(String token, String terminalId) {
        this.token = token;
        this.terminalId = terminalId;
    }

    //5초마다 polling, 테스트를 위해 command를 랜덤하게 변경해서 전송
    private void polling(ChannelHandlerContext ctx) {
        CommonHeader commonResponse = CommonHeader.builder()
                .command(CommandHealthCode.health.name())
                .time(CommonUtils.getCurrentTime("yyyy-MM-dd'T'HH:mm:ss.SSS"))
                .srType(SendRecvCode.response.name())
                .status(ErrorCode.STATUS_SUCCESS.getStatus())
                .terminalId(terminalId)
                .localIp(CommonUtils.getLocalIp())
                .message(CommonUtils.getCurrentTime("") + ", " + CommandHealthCode.health.name() + " 했다....")
                .build();

        Thread healthy = new Thread(new HealthyRunner(ctx, commonResponse));
        healthy.start();
        try {
            Thread.sleep(100);
        } catch (Exception e) {
            e.printStackTrace();
        }

        commonResponse.setData(null);
    }


    //Netty login
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("channelActive terminalId: " + terminalId);

        LoginSendDTO dto = new LoginSendDTO(token);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = gson.toJson(dto);
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);

        CommonHeader loginRequest = CommonHeader.builder()
                .status("0000")
                .command(CommandCode.login.name())
                .srType(SendRecvCode.request.name())
                .terminalId(terminalId) //
                .message("로그인 요청")
                .data(jsonObject)
                .build();
        String message = new Gson().toJson(loginRequest) + NEWLINE;
        System.out.println("Client connected to " + ctx.channel().remoteAddress());
        System.out.println(message);
        ctx.channel().writeAndFlush(message);

        //
        polling(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("Message received: " + (String)msg);
        CommonHeader commonRecvdata = new Gson().fromJson(msg.toString(), CommonHeader.class);

        if (!commonRecvdata.getStatus().equalsIgnoreCase("0000")) {
            System.out.println("Error: " + commonRecvdata.getMessage());
            return;
        }
        System.out.println("recv command:" + commonRecvdata.getCommand() + ", terminalId:" + commonRecvdata.getTerminalId());

        //Thread healthy = new Thread(new HealthyRunner(ctx, commonResponse));
        //healthy.start();

        CommonHeader commonResponse;
        switch (commonRecvdata.getCommand()) {
            case "login":
                break;
            case "reboot":
            case "shutdown":
            case "health":
                commonResponse = makeHeader(commonRecvdata.getCommand());
                commonResponse.setData(null);
                send(ctx, commonResponse);
                break;
            case "cmd": //README 참고, dir c:\windows
                Gson gson = new Gson();

                commonResponse = makeHeader(commonRecvdata.getCommand());
                String recvCommand = commonRecvdata.getData().toString();
                String commnadArgs[] = recvCommand.split(" ");
                //CmdRecvDTO recvDTO = gson.fromJson(data, CmdRecvDTO.class);

                if (commnadArgs[0].compareToIgnoreCase("dir") == 0) {
                    List<FileInfo> fileInfos = Cmd.dir(commnadArgs[1]);
                    CmdSendDTO cmdSendDTO = new CmdSendDTO();
                    cmdSendDTO.setCmd(recvCommand);
                    cmdSendDTO.setFileList(fileInfos);

                    String jsonString = gson.toJson(cmdSendDTO);
                    JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
                    commonResponse.setData(jsonObject);
                } else {
                    System.out.println("Invalid command: " + recvCommand);
                }
                //commonResponse.setData();
                send(ctx, commonResponse);
                break;
        }
    }

    private CommonHeader makeHeader(String command) {
        CommonHeader commonResponse = CommonHeader.builder()
                .command(command)
                .time(CommonUtils.getCurrentTime("yyyy-MM-dd'T'HH:mm:ss.SSS"))
                .srType(SendRecvCode.response.name())
                .status(ErrorCode.STATUS_SUCCESS.getStatus())
                .terminalId(terminalId)
                .localIp(CommonUtils.getLocalIp())
                .message(CommonUtils.getCurrentTime("") + ", " + command + " 했다....")
                .build();
        return commonResponse;
    }

    private void send(ChannelHandlerContext ctx, CommonHeader response) {
        response.setTime(CommonUtils.getCurrentTime("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        String message = new Gson().toJson(response) + NEWLINE;
        ctx.channel().writeAndFlush(message);
        System.out.println("Message send    : " + message);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        //cause.printStackTrace();
        System.out.println(cause.getMessage());
        ctx.close();
    }

}
