package com.huitongjy.elastic.job.spring.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * Enable Elastic Job
 *
 * @author zhaoke
 * @since 2020/4/22
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ZookeeperBeanDefinitionRegistrar.class, CompositeBeanDefinitionRegistrar.class})
public @interface EnableElasticJobLite {

    /**
     * 注册中心
     */
    String registryRef() default "";
}
