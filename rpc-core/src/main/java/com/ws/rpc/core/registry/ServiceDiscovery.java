package com.ws.rpc.core.registry;

import com.ws.rpc.core.dto.RpcRequest;
import com.ws.rpc.core.dto.ServiceInfo;

import java.util.List;

public interface ServiceDiscovery {
    /**
     * 基于 getServices，根据负载均衡算法，获取服务信息
     * @param rpcRequest
     * @return
     */
    ServiceInfo discover(RpcRequest rpcRequest);

    /**
     * 根据服务名获取服务信息列表
     * @param serviceKey
     * @return
     * @throws Exception
     */
    List<ServiceInfo> getServices(String serviceKey) throws Exception;

    /**
     * 销毁
     * @throws Exception
     */
    void destroy() throws Exception;
}
