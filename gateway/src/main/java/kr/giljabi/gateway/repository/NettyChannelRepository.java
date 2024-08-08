package kr.giljabi.gateway.repository;

import kr.giljabi.gateway.model.NettyChannel;
import org.springframework.data.repository.CrudRepository;

public interface NettyChannelRepository extends CrudRepository<NettyChannel, String> {
    NettyChannel findByChannelId(String channelId);
    int deleteByChannelId(String channelId);
}
