package com.ws.rpc.core.factory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ws
 * @version 1.0
 * @date 2024-12-27 4:03
 */
public class SingletonFactory {
    private static Map<String, Object> OBJECT_MAP = new ConcurrentHashMap<>();

    public static <T> T getInstance(Class<T> clazz) {
        try {
            // 获取类的全限定名
            String className = clazz.getName();
            if (OBJECT_MAP.containsKey(className)) {
                return clazz.cast(OBJECT_MAP.get(className));
            } else {
//                synchronized (className.intern()) {
                synchronized (clazz) {
                    // 双重检查
                    if (OBJECT_MAP.containsKey(className)) {
                        return clazz.cast(OBJECT_MAP.get(className));
                    }
                    T instance = clazz.getDeclaredConstructor().newInstance();
                    OBJECT_MAP.put(className, instance);
                    return instance;
                }
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
