package kr.giljabi.gateway.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @Author : eahn.park@gmail.com
 * @Date : 2024.04.26
 * @Description
 */
@Getter
@Setter
@Entity
@Table(name = "nettychannel")
public class NettyChannel {

    @Id
    @Column(name = "terminalid", nullable = false, length = 32)
    private String terminalId;

    @Column(name = "channelid", nullable = true, length = 32)
    private String channelId;

    @Column(name = "channel", nullable = true, length = 64)
    private String channel;
}
