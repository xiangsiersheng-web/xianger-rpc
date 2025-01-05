package com.ws.rpc.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author ws
 * @version 1.0
 * @date 2024-12-27 0:21
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RpcResponse implements Serializable {
    private Object result;
    private Exception exception;
}
