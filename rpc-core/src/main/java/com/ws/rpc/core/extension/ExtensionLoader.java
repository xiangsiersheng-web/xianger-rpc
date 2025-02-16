package com.ws.rpc.core.extension;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ws
 * @version 1.0
 * @date 2025-02-16 13:25
 */
@Slf4j
public final class ExtensionLoader<T> {
    /**
     * 服务的存储目录
     */
    private static final String SERVICES_DIRECTORY = "META-INF/extensions/";
    /**
     * 存储所有ExtensionLoader, key：接口类，value：ExtensionLoader实例对象
     * 全局缓存，确保单例
     */
    private static final Map<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>();
    /**
     * 存储所有Extension，key：具体实现类，value：具体实现类实例对象
     */
    private static final Map<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<>();

    /**
     * 接口类型
     */
    private final Class<?> type;
    /**
     * 存储该扩展器对象下的所有扩展实现类，key：扩展点名称，value：扩展实现类的实例对象
     * 局部缓存，快速获取实现类
     */
    private final Map<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<>();
    /**
     * 持有一个Map，该Map的key为扩展点名称，value为扩展实现类的Class
     */
    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();

    private ExtensionLoader(Class<?> type) {
        this.type = type;
    }

    public static <S> ExtensionLoader<S> getExtensionLoader(Class<S> type) {
        if (type == null) {
            throw new IllegalArgumentException("Extension type should not be null.");
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Extension type must be an interface.");
        }
        if (type.getAnnotation(SPI.class) == null) {
            throw new IllegalArgumentException("Extension type must be annotated by @SPI");
        }
        // 先查询缓存
        ExtensionLoader<S> extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADERS.get(type);
        if (extensionLoader == null) {
            EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader<>(type));
            extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADERS.get(type);
        }
        return extensionLoader;
    }

    public T getExtension(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Extension name should not be null or empty.");
        }
        // 先从本地缓存查询
        Holder<Object> holder = cachedInstances.get(name);
        if (holder == null) {
            cachedInstances.putIfAbsent(name, new Holder<>());
            holder = cachedInstances.get(name);
        }
        // 获取实例对象
        Object instance = holder.get();
        if (instance == null) {
            synchronized (holder) {
                instance = holder.get();
                if (instance == null) {
                    instance = createExtension(name);
                    holder.set(instance);
                }
            }
        }
        return (T) instance;
    }

    private T createExtension(String name) {
        // 加载接口下的所有实现类，并根据name获取指定实现类
        Class<?> clazz = getExtensionClasses().get(name);
        if (clazz == null) {
            throw new RuntimeException("No such extension of " + type.getName() + " by name " + name);
        }
        // 先从全局缓存中获取
        Object instance = EXTENSION_INSTANCES.get(clazz);
        if (instance == null) {
            try {
                EXTENSION_INSTANCES.putIfAbsent(clazz, clazz.newInstance());
                instance = EXTENSION_INSTANCES.get(clazz);
            } catch (Exception e) {
                throw new RuntimeException("Fail to create extension " + clazz + " of extension point " + type.getName() + ", cause: " + e.getMessage(), e);
            }
        }
        return (T) instance;
    }

    private Map<String, Class<?>> getExtensionClasses() {
        Map<String, Class<?>> classes = cachedClasses.get();
        if (classes == null) {
            synchronized (cachedClasses) {
                classes = cachedClasses.get();
                if (classes == null) {
                    classes = new HashMap<>();
                    loadDirectory(classes);
                    cachedClasses.set(classes);
                }
            }
        }
        return classes;
    }

    private void loadDirectory(Map<String, Class<?>> classes) {
        // 资源文件名：META-INF/extensions/com.ws.rpc.core.loadbalance.LoadBalance
        String fileName = SERVICES_DIRECTORY + type.getName();
        try {
            ClassLoader classLoader = ExtensionLoader.class.getClassLoader();
            Enumeration<URL> urls = classLoader.getResources(fileName);
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    URL resourceUrl = urls.nextElement();
                    loadResource(classes, classLoader, resourceUrl);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadResource(Map<String, Class<?>> classes, ClassLoader classLoader, URL resourceUrl) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceUrl.openStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 获取注释之前的内容
                int index = line.indexOf('#');
                if (index >= 0) {
                    line = line.substring(0, index).trim();
                }
                if (line.length() > 0) {
                    try {
                        // 获取扩展点的名称
                        final int ei = line.indexOf('=');
                        String name = line.substring(0, ei).trim(); // 扩展点名称
                        String clazzName = line.substring(ei + 1).trim(); // 扩展实现类全限定名
                        if (name.length() > 0 && clazzName.length() > 0) {
                            Class<?> clazz = classLoader.loadClass(clazzName);
                            classes.put(name, clazz);
                        }
                    } catch (ClassNotFoundException e) {
                        log.error("Failed to load extension class(interface: " +
                                type + ", class line: " + line + ").", e);
                        throw new RuntimeException(e);
                    }
                }
            }
        } catch (IOException e) {
            log.error("Failed to load resource file (file: " + resourceUrl + ").", e);
            throw new RuntimeException(e);
        }
    }
}
