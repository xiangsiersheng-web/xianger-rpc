package com.ws.rpc.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author ws
 * @version 1.0
 * @date 2024-12-28 20:50
 */

@Data
@ConfigurationProperties(prefix = "rpc.server")
public class RpcServerProperties {

    private String providerName;

    private Integer port;

    private String registryAddr;

    /**
     * 进行默认初始化值
     */
    public RpcServerProperties() throws UnknownHostException {
        this.providerName = "server-01";
        this.port = 9090;
        this.registryAddr = "127.0.0.1:2181";
    }
}
