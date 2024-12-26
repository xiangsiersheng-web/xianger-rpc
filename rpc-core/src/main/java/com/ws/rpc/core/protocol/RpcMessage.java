package com.ws.rpc.core.protocol;

import lombok.Data;

/**
 * @author ws
 * @version 1.0
 * @date 2024-12-26 23:56
 */
@Data
public class RpcMessage {
    private MessageHeader header;
    private Object body;
}
