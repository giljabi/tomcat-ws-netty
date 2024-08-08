package kr.giljabi.gateway.notify;

import org.springframework.data.repository.CrudRepository;

public interface MqServerRepository extends CrudRepository<MqServer, String>{
    MqServer findByRecvserver(String recvserver);
}
