package com.ws.rpc.core.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ws
 * @version 1.0
 * @date 2024-12-26 23:56
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcMessage {
    private MessageHeader header;
    private Object body;
}
