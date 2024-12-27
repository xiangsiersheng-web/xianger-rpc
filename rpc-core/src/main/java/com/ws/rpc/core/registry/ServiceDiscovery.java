package com.ws.rpc.core.registry;

import com.ws.rpc.core.dto.RpcRequest;
import com.ws.rpc.core.dto.ServiceInfo;

import java.util.ArrayList;
import java.util.List;

public interface ServiceDiscovery {
    ServiceInfo discover(RpcRequest rpcRequest);

    default List<ServiceInfo> getServices(String serviceName) throws Exception {
        return new ArrayList<>();
    }

    void destroy() throws Exception;
}
