package com.ws.rpc.core.protocol;

import com.ws.rpc.core.enums.CompressionType;
import com.ws.rpc.core.enums.MessageType;
import com.ws.rpc.core.enums.SerializationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author ws
 * @version 1.0
 * @date 2024-12-26 23:56
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcMessage implements Serializable {
    private MessageHeader header;
    private Object body;

    public static RpcMessage getPing() {
        // 默认的心跳包构造
        MessageHeader header = new MessageHeader();
        header.setMessageType(MessageType.HEARTBEAT_REQUEST.getCode());
        header.setSerializationAlgorithm(SerializationType.JDK.getType());
        header.setCompressionAlgorithm(CompressionType.UN_COMPRESSION.getType());

        RpcMessage rpcMessage = new RpcMessage(header, ProtocolConstants.PING);
        return rpcMessage;
    }

/*    public static RpcMessage getPong(RpcMessage ping) {
        MessageHeader header = new MessageHeader();
        header.setMessageType(MessageType.HEARTBEAT_RESPONSE.getCode());
        header.setSerializationAlgorithm(ping.getHeader().getSerializationAlgorithm());
        header.setCompressionAlgorithm(ping.getHeader().getCompressionAlgorithm());

        RpcMessage rpcMessage = new RpcMessage(header, RpcConstants.PONG);
        return rpcMessage;
    }*/
}
