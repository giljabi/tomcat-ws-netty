package kr.giljabi.gateway.service;

import kr.giljabi.gateway.repository.NettyChannelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NettyChannelService {
    private final NettyChannelRepository nettyChannelRepository;

    @Autowired
    public NettyChannelService(NettyChannelRepository nettyChannelRepository) {
        this.nettyChannelRepository = nettyChannelRepository;
    }
}
