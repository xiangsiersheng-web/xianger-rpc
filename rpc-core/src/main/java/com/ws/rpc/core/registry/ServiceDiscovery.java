package com.ws.rpc.core.registry;

import com.ws.rpc.core.dto.RpcRequest;
import com.ws.rpc.core.dto.ServiceInfo;

import java.util.List;

public interface ServiceDiscovery {
    ServiceInfo discover(RpcRequest rpcRequest);

    List<ServiceInfo> getServices(String serviceKey) throws Exception;

    void destroy() throws Exception;
}
