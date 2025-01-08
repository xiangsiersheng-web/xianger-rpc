package com.ws.rpc.client.dto;

import com.ws.rpc.core.protocol.RpcMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ws
 * @version 1.0
 * @date 2024-12-27 0:21
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequestMetaData {
    private RpcMessage rpcMessage;
    private String serviceAddress;
    private Integer servicePort;
    private int timeout;
}
