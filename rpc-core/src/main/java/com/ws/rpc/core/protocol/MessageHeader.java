package com.ws.rpc.core.protocol;

import com.ws.rpc.core.constants.RpcConstants;
import com.ws.rpc.core.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 请求协议头部信息
 * <pre>
 *   -------------------------------------------------------------------------------------
 *  | 魔数 (4byte) | 版本号 (1byte) | 消息类型 (1byte) | 序列化算法 (1byte) | 压缩算法 (1byte) |
 *   -------------------------------------------------------------------------------------
 *  |           请求ID (4byte)                  |             消息长度 (4byte)             |
 *   -------------------------------------------------------------------------------------
 * </pre>
 * @author ws
 * @version 1.0
 * @date 2024-12-26 23:58
 */
@Data
@AllArgsConstructor
@Builder
public class MessageHeader {
    /**
     * 魔数（用于标识协议，4字节）
     */
    private byte[] magicNumber;

    /**
     * 版本号（1字节）
     */
    private byte version;

    /**
     * 消息类型（1字节）
     */
    private byte messageType;

    /**
     * 序列化算法（1字节）
     */
    private byte serializationAlgorithm;

    /**
     * 压缩算法（1字节）
     */
    private byte compressionAlgorithm;

    /**
     * 请求ID（4字节）
     */
    private int messageId;

    /**
     * 全文长度（包括消息头和消息体，4字节）
     */
    private int fullLength;

    /**
     * 默认构造方法，设置属性默认值
     */
    public MessageHeader() {
        this.magicNumber = RpcConstants.MAGIC_NUMBER;           // 默认魔数
        this.version = RpcConstants.VERSION;                    // 默认版本号
        this.messageType = MessageType.REQUEST.getCode();   // 默认消息类型
        this.serializationAlgorithm = 0;                        // 默认序列化算法
        this.compressionAlgorithm = 0;                          // 默认压缩算法
        this.messageId = 0;                                     // 默认请求ID
        this.fullLength = RpcConstants.HEAD_LENGTH;             // 默认消息长度（仅头部长度）
    }
}
