package com.ws.rpc.server.spring;

import com.ws.rpc.server.annotation.RpcService;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;

/**
 * @author ws
 * @version 1.0
 * @date 2024-12-28 11:28
 */
public class RpcScanner extends ClassPathBeanDefinitionScanner {

    public RpcScanner(BeanDefinitionRegistry registry, Class<? extends Annotation> annoType) {
        super(registry);
        super.addIncludeFilter(new AnnotationTypeFilter(annoType));
    }

    @Override
    public int scan(String... basePackages) {
        return super.scan(basePackages);
    }
}
