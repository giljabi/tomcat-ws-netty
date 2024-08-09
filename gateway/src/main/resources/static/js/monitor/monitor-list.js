let socket;
let stompClient;
let monitorType;

function commandClassFadeout(terminalId, className) {
    setTimeout(function () { //추가하는 경우는 필요없지만, td update후에는 addClass 후 사라지게 하는 로직이 필요
        $('#' + terminalId).removeClass(className);
    }, 500);
}

// Mapping command to className and whether to call commandClass
const commandsEffect = {
    'shutdown': { className: 'fadeEffectShutdown', callFunction: false }, // No function call noted in the comment
    'reboot': { className: 'fadeEffectReboot', callFunction: true },
    'health': { className: 'fadeEffectHealth', callFunction: true },
    'trans': { className: 'fadeEffectTrans', fadeEffectTrans: true },
    'default': { className: 'fadeEffectBlueToWhite', callFunction: true } // Default case handling
}

function updateTerminalFields(terminal, newTerminal) {
    const fields = ['#sendTime', '#recvTime', '#command', '#status', '#srType'];
    fields.forEach(field => {
        terminal.find(field).text(newTerminal.find(field).text());
    });
}

$(document).ready(function () {
    monitorType = false;
    $('#monitorType').change(function() {
        monitorType = $(this).is(':checked');
        //console.log('monitorType=' + monitorType);
    });

    function connect() {
        socket = new SockJS("/ws");    //registerStompEndpoints()에 설정한 endpoint
        stompClient = Stomp.over(socket);

        stompClient.heartbeat.outgoing = 60 * 1000;  // 클라이언트에서 서버로의 하트비트 간격
        stompClient.heartbeat.incoming = 60 * 1000;  // 서버에서 클라이언트로의 하트비트 간격
/* stompClient.heartbeat 설정
>>> CONNECT
accept-version:1.1,1.0
heart-beat:60000,60000

*/
        //stompClient.debug = null; //stomp 로그 비활성화
        stompClient.debug = function(str) {
            //console.log(new Date() + ' STOMP: ' + str);
            if (str.startsWith('ERROR') || str.startsWith('WARN')) {    //필요한 로그만 출력
                console.log(str);
            }
        };

        console.log(new Date() + ' Connecting to WS...');
        console.log(new Date() + ' STOMP: connected to server, connected to: ' + stompClient.ws.url);
        stompClient.connected = false;

        stompClient.connect({}, function (frame) {
                console.log(new Date() + ' Connected: ', frame);
                console.log(new Date() + ' stompClient.ws._transport.ws.url: ' + stompClient.ws._transport.ws.url);
                let subMonitor = stompClient.subscribe('/topic/monitor', function (e) {
                    //console.log(new Date() + ' monitor stompClient.connected=' + stompClient.connected);
                    showMessage(e.body);
                });
                let subTime = stompClient.subscribe('/topic/time', function(timeMessage) {
                    //console.log(new Date() + ' time stompClient.connected=' + stompClient.connected);
                    //console.log(timeMessage.body);
                    $('#time').text('Server Time: ' + timeMessage.body);
                });

                /*
                // 채널 추가
                let subPingpong = stompClient.subscribe('/topic/pong', function (e) {
                    console.log(new Date()  + ' /topic/pong 구독: ' + e.body);
                    showMessage(e.body);
                });
                subMonitor.onclose = function() { reconnect('/topic/monitor'); };
                subPingpong.onclose = reconnect('/topic/pong');
                setInterval(sendPing, 60 * 1 * 1000);
                */
            },
            function (error) {
                console.error(new Date() + ' --------------------------------Connection Error: ' + error);
                reconnect('/topic/monitor');
            }
        );
    }

    function reconnect(subName) {
        setTimeout(function() {
            console.log(new Date() + ' reconnect stompClient.connected=' + stompClient.connected);
            if (stompClient.connected === false) {
                console.log(subName + " Attempting to reconnect...");
                connect();
            }
        }, 10 * 1000); // 10초 후 재연결 시도
    }

    connect();

    /*        chrome.runtime.onMessage.addListener((request, sender, sendResponse) => {
                // Perform some asynchronous operation
                setTimeout(() => {
                    // Send a response back to the sender
                    sendResponse({status: true});
                }, 1000); // Simulate a delay
                // Return true to keep sendResponse valid
                return true;
            });*/

    //연결 유지를 위한 ping protocol 구현시 사용
    function sendPing() {
        if (stompClient !== null && stompClient.connected) {
            stompClient.send("/app/ping", {}, "ping");
            console.log("Ping sent to server");
        } else {
            console.log("Not connected to server.");
        }
    }

    function disconnect() {
        if (stompClient !== null) {
            stompClient.disconnect();
        }
        console.log("Disconnected");
    }

    //메시지 브로커로 메시지 전송할 필요가 있을때 3가지
    //1. DB insert 후 메시지 브로커로 전송
    //2. controller에서 DB insert 후 메시지 브로커로 전송
    //3. 메시지 브로커로 메시지 전송할 필요가 있을때....
    function send() { //3번 방법
        let data = {
            'sender': 'monitoruser',
            'contents': 'Hello, I am monitoruser!'
        };
        // @MessageMapping("/monitor/send"),
        stompClient.send("/app/monitor/send", {}, JSON.stringify(data));
    }
    /*
            $('#reboot').click( function () {
                send();
            });*/

    function showMessage(data) {
        let recvData = JSON.parse(data);
        let terminal = $('#monitorTable tr#' + recvData.terminalId);
        if (recvData.command === 'inactive') {
            terminal.find('#command').text(recvData.command);
            terminal.find('#status').text(recvData.status);
            terminal.addClass('fadeEffectRedToCoral');
        } else {
            //tr object를 만들고...
            let newTerminal = $('<tr>').attr('id', recvData.terminalId); //새로운 tr object
            const appendCell = (id, text) => newTerminal.append($('<td>').attr('id', id).text(text));
            if (terminal.length > 0) { // 이미 존재하는 terminalId인 경우는 send, recv시간이 중요
                appendCell('terminalId', recvData.terminalId);
                if (recvData.srType === 'request') {
                    appendCell('sendTime', recvData.time);
                    appendCell('recvTime', '');
                } else {
                    appendCell('sendTime', $('#' + recvData.terminalId + ' #sendTime').text());
                    appendCell('recvTime', recvData.time);
                }
            } else { // terminalId가 없다면...
                appendCell('terminalId', recvData.terminalId);
                appendCell('sendTime', '');
                appendCell('recvTime', recvData.time);

                //add TerminalIDList
                $('#terminalIDList').append($('<option>', {
                    value: recvData.terminalId,
                    text: recvData.terminalId
                }));
            }
            appendCell('command', recvData.command);
            appendCell('status', recvData.status);
            appendCell('srType', recvData.srType);

            console.log('recvData.data=' + recvData);
            if(recvData.data != null) {
                let dataLength = JSON.stringify(recvData.data).length;
                if (dataLength > 0) {
                    if (dataLength > 50)
                        appendCell('recvData', JSON.stringify(recvData.data).substring(0, 50)); //일부만 표시
                    else
                        appendCell('recvData', JSON.stringify(recvData.data).substring(0, 50));
                }
            }

            const { className, callFunction } = commandsEffect[recvData.command] || commandsEffect['default'];
            if(monitorType) {   //모니터링 타입 체크박스가 체크되어 있는 경우
                if(terminal.length > 0) {   //기존 터미널정보 update
                    terminal.removeAttr('class');
                    updateTerminalFields(terminal, newTerminal);
                    terminal.addClass(className);
                    if (callFunction) { //tr에 적용된 className을 제거하는 함수 호출
                        commandClassFadeout(recvData.terminalId, className);
                    }
                } else {
                    newTerminal.addClass(className);
                    $('#monitorTable').prepend(newTerminal);
                }
            } else {
                terminal.remove(); // 기존 tr 삭제
                newTerminal.addClass(className);
                $('#monitorTable').prepend(newTerminal);
            }
        }
    }

    $('#sendCommand').click(function() {
        let selectedCommand = $('#command').val();
        let selectedTerminalID = $('#terminalIDList').val();

        let requestObject = {};
        requestObject.command = selectedCommand;
        requestObject.terminalId = selectedTerminalID;
        if(selectedCommand == 'cmd')
            requestObject.script = 'dir /tmp';

        if (selectedCommand) {
            $.ajax({
                url: '/api/toAgent',
                contentType: 'application/json',
                data: JSON.stringify(requestObject),
                type: 'POST',
                success: function(response) {
                    console.log('Request was successful');
                    // 필요한 경우, 응답(response) 데이터를 처리할 수 있습니다.
                },
                error: function(xhr, status, error) {
                    console.error('Request failed: ' + error);
                }
            });
        } else {
            alert('Please select a command.');
        }
    });
});