package kr.giljabi.gateway.repository;

import kr.giljabi.gateway.model.BoothStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;

public interface BoothStatusRepository extends CrudRepository<BoothStatus, String> {
    @Transactional
    @Modifying
    @Query("UPDATE BoothStatus b SET b.clientSendTime=:clientsendtime, b.localIp=:localip, b.publicIp=:publicip, b.recvMessage=:recvmessage, b.status = :status, b.serverTime = :serverTime, b.lastCommand = :lastCommand WHERE b.terminalId = :terminalId")
    void updateStatusByTerminalId(Timestamp clientsendtime, String localip, String publicip, String terminalId, String status, Timestamp serverTime, String lastCommand, String recvmessage);

}
/*
    update
        public.boothstatus
    set
                clientsendtime='2024-04-30T13:10:52.031+0900',
                lastcommand='cmd',
            localip='150.73.9.108',
            publicip='127.0.0.1',
        recvmessage='2024-04-30T13:10:52.019, cmd 했다....',
                servertime='2024-04-30T13:10:52.035+0900',
                status='0000'
    where
        terminalid='1001'
 */