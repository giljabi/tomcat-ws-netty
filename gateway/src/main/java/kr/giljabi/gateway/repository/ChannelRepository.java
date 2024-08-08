package kr.giljabi.gateway.repository;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ChannelRepository {
    public static final AttributeKey<Long> CREATED_TIME = AttributeKey.valueOf("CreatedTime");
    public static final AttributeKey<Long> LAST_READ_TIME = AttributeKey.valueOf("LastReadTime");
    public static final AttributeKey<Long> LAST_WRITE_TIME = AttributeKey.valueOf("LastWriteTime");
    public static final AttributeKey<String> LAST_HANDLER = AttributeKey.valueOf("LastHandler");

    private final ConcurrentHashMap<String, Channel> terminalIdChannelMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Channel, String> channelTerminalIdMap = new ConcurrentHashMap<>();

    private long lastProcessingTime = 0;

    public void addChannel(String terminalId, Channel channel) {
        terminalIdChannelMap.put(terminalId, channel);
        channelTerminalIdMap.put(channel, terminalId);
        log.info("addChannel count: {}", terminalIdChannelMap.size());
    }

    public Channel getChannelByTerminalId(String terminalId) {
        return terminalIdChannelMap.get(terminalId);
    }

    public String getTerminalIdByChannel(Channel channel) {
        return channelTerminalIdMap.get(channel);
    }

    public void removeByTerminalId(String terminalId) {
        Channel channel = terminalIdChannelMap.remove(terminalId);
        if (channel != null) {
            channelTerminalIdMap.remove(channel);
        }
        log.info("terminalId removed: {}, count:{}", terminalId, channelTerminalIdMap.size());
    }

    public void removeByChannel(Channel channel) {
        String terminalId = channelTerminalIdMap.remove(channel);
        if (terminalId != null) {
            terminalIdChannelMap.remove(terminalId);
        }
        log.info("channel removed: {}, count:{}", channel, terminalIdChannelMap.size());
    }

    public ConcurrentHashMap<String, Channel> getTerminalIdChannelMap() {
        return terminalIdChannelMap;
    }

    public long getLastProcessingTime() {
        return lastProcessingTime;
    }

    public void setLastProcessingTime(long lastProcessingTime) {
        this.lastProcessingTime = lastProcessingTime;
    }
/*
    @Override
    public String toString() {
        return String.format("boothIdToChannelMap count=%d, channelToBoothIdMap count=%d, boothIdToChannelMap=%s, channelToBoothIdMap=%s",
                terminalIdToChannelMap.size(), channelToBoothIdMap.size(),
                terminalIdToChannelMap, channelToBoothIdMap);
    }*/
}