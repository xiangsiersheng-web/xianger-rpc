package com.ws.rpc.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author ws
 * @version 1.0
 * @date 2024-12-27 22:33
 */
@Data
@ConfigurationProperties(prefix = "rpc.client")
public class RpcClientProperties {
    private String loadbalance;
    private String serialization;
    private String transport;
    private String compression;
    private String registry;
    private String registryAddr;
    private long timeout;

    public RpcClientProperties() {
        this.loadbalance = "random";
        this.serialization = "jdk";
        this.transport = "netty";
        this.compression = "un_compression";
        this.registry = "zookeeper";
        this.registryAddr = "127.0.0.1:2181";
        this.timeout = 5000;
    }
}
