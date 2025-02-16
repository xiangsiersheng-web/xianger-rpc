package com.ws.rpc.core.loadbalance;

import com.ws.rpc.core.dto.RpcRequest;
import com.ws.rpc.core.dto.ServiceInfo;
import com.ws.rpc.core.extension.SPI;

import java.util.List;

/**
 * @author ws
 * @version 1.0
 * @date 2024-12-27 2:05
 */
@SPI
public interface LoadBalance {
    /**
     * 根据负载均衡策略选择一个服务
     * @param serviceInfoList 服务列表
     * @param rpcRequest 请求信息
     * @return 服务信息
     */
    ServiceInfo select(List<ServiceInfo> serviceInfoList, RpcRequest rpcRequest);
}
