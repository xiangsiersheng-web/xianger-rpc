package com.ws.rpc.server.spring;

import com.ws.rpc.server.annotation.RpcScan;
import com.ws.rpc.server.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;

/**
 * 这个生效是因为 {@link RpcScan} 注解的 value 属性是 @Import(RpcRegistry.class)
 * @author ws
 * @version 1.0
 * @date 2024-12-28 11:27
 */
@Slf4j
public class RpcRegistry implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {
    private ResourceLoader resourceLoader;

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 获取 RpcComponentScan 注解的属性和值
        AnnotationAttributes annotationAttributes = AnnotationAttributes
                .fromMap(importingClassMetadata.getAnnotationAttributes(RpcScan.class.getName()));

        String[] basePackages = {};
        if (annotationAttributes != null) {
            // 获取 RpcComponentScan 注解的 basePackages 值。basePackages 要和 RpcScan 的 字段一致
            basePackages = annotationAttributes.getStringArray("basePackages");
        }

        // 如果没有指定 basePackages，则默认扫描当前类所在包
        if (basePackages.length == 0) {
            basePackages = new String[]{((StandardAnnotationMetadata) importingClassMetadata)
                    .getIntrospectedClass().getPackage().getName()};
        }

        // 创建一个扫描 RpcService 注解的 Scanner
        RpcScanner rpcScanner = new RpcScanner(registry, RpcService.class);

        if (this.resourceLoader != null) {
            rpcScanner.setResourceLoader(this.resourceLoader);
        }

        // 扫描指定包路径下的所有 RpcService Bean 并返回注册成功的数量
        int count = rpcScanner.scan(basePackages);
        log.info("The number of BeanDefinitions scanned and registered by RpcScanner is {}.", count);
    }
}
