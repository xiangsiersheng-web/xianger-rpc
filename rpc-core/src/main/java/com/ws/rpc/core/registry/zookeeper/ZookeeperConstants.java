package com.ws.rpc.core.registry.zookeeper;

import com.ws.rpc.core.enums.SerializationType;

/**
 * @author ws
 * @version 1.0
 * @date 2025-01-06 22:26
 */
public class ZookeeperConstants {
    public static final String BASE_PATH = "/xianger-rpc";
    public static final int SESSION_TIMEOUT = 60 * 1000;
    public static final int CONNECT_TIMEOUT = 15 * 1000;
    public static final int BASE_SLEEP_TIME = 3 * 1000;
    public static final int MAX_RETRY = 10;

    // 默认序列化方式 （需要将serviceInfo进行序列化）
    public static final SerializationType DEFAULT_SERIALIZATION_TYPE = SerializationType.JSON;
}
