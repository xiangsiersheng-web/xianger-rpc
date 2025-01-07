package com.ws.rpc.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 服务信息类，封装服务的元数据
 * @author ws
 * @version 1.0
 * @date 2024-12-27 1:39
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 服务名称（全限定名+version）
     */
    private String serviceKey;

    /**
     * 服务版本号，用于区分同一服务的不同版本
     */
    private String version;

    /**
     * 服务提供者的主机地址（IP 或域名）
     */
    private String host;

    /**
     * 服务提供者的端口号
     */
    private int port;

    /**
     * 服务权重，用于负载均衡策略
     */
    private int weight;

    /**
     * 其他可能的元数据，如健康状态、服务描述等
     */
    private String metadata;
}
