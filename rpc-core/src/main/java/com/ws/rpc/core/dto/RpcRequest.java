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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest implements Serializable {
    /**
     * 服务名+版本
     */
    private String serviceKey;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;
}
