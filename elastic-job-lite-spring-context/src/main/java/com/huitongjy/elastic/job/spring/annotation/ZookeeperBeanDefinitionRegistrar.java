package com.huitongjy.elastic.job.spring.annotation;

import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.google.common.base.Strings;
import com.huitongjy.elastic.job.spring.properties.ZookeeperConfigProperties;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Zookeeper Config {@link ImportBeanDefinitionRegistrar BeanDefinition Registrar}
 *
 * @author zhaoke
 * @since 2020/4/22
 **/
public class ZookeeperBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        //Register Zookeeper Config Beans.
        ZookeeperConfigProperties zkConfigProperties = parseZookeeperConfig((ConfigurableEnvironment) environment);
        if (null == zkConfigProperties) {
            return;
        }
        if (registry.containsBeanDefinition(zkConfigProperties.getId())) {
            return;
        }
        registryZookeeperConfig(zkConfigProperties, registry);
    }

    private ZookeeperConfigProperties parseZookeeperConfig(ConfigurableEnvironment environment) {
        if (Strings.isNullOrEmpty(environment.getProperty("elastic.job.lite.config.zookeeper.id"))
                || Strings.isNullOrEmpty(environment.getProperty("elastic.job.lite.config.zookeeper.server-lists"))) {
            return null;
        }
        ZookeeperConfigProperties result = new ZookeeperConfigProperties();
        result.setId(environment.getProperty("elastic.job.lite.config.zookeeper.id"));
        result.setServerLists(environment.getProperty("elastic.job.lite.config.zookeeper.server-lists"));
        result.setNamespace(environment.getProperty("elastic.job.lite.config.zookeeper.namespace"));
        if (Strings.isNullOrEmpty(environment.getProperty("elastic.job.lite.config.zookeeper.base-sleep-time-milliseconds"))) {
            result.setBaseSleepTimeMilliseconds(environment.getProperty("elastic.job.lite.config.zookeeper.base-sleep-time-milliseconds", Integer.class));
        }
        if (Strings.isNullOrEmpty(environment.getProperty("elastic.job.lite.config.zookeeper.max-sleep-time-milliseconds"))) {
            result.setMaxSleepTimeMilliseconds(environment.getProperty("elastic.job.lite.config.zookeeper.max-sleep-time-milliseconds", Integer.class));
        }
        if (Strings.isNullOrEmpty(environment.getProperty("elastic.job.lite.config.zookeeper.max-retries"))) {
            result.setMaxRetries(environment.getProperty("elastic.job.lite.config.zookeeper.max-retries", Integer.class));
        }
        return result;
    }

    private void registryZookeeperConfig(ZookeeperConfigProperties zkConfigProperties, BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(ZookeeperRegistryCenter.class);
        factory.addConstructorArgValue(buildZookeeperConfigurationBeanDefinition(zkConfigProperties));
        factory.setInitMethodName("init");
        registry.registerBeanDefinition(zkConfigProperties.getId(), factory.getBeanDefinition());
    }

    private AbstractBeanDefinition buildZookeeperConfigurationBeanDefinition(ZookeeperConfigProperties zkConfigProperties) {
        BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(ZookeeperConfiguration.class);
        factory.addConstructorArgValue(zkConfigProperties.getServerLists());
        factory.addConstructorArgValue(zkConfigProperties.getNamespace());
        addPropertyValueIfNotEmpty("baseSleepTimeMilliseconds", zkConfigProperties.getBaseSleepTimeMilliseconds(), factory);
        addPropertyValueIfNotEmpty("maxSleepTimeMilliseconds", zkConfigProperties.getMaxSleepTimeMilliseconds(), factory);
        addPropertyValueIfNotEmpty("maxRetries", zkConfigProperties.getMaxRetries(), factory);
        addPropertyValueIfNotEmpty("sessionTimeoutMilliseconds", zkConfigProperties.getSessionTimeoutMilliseconds(), factory);
        addPropertyValueIfNotEmpty("connectionTimeoutMilliseconds", zkConfigProperties.getConnectionTimeoutMilliseconds(), factory);
        addPropertyValueIfNotEmpty("digest", zkConfigProperties.getDigest(), factory);
        return factory.getBeanDefinition();
    }

    private void addPropertyValueIfNotEmpty(final String propertyName, final Object propertyValue, final BeanDefinitionBuilder factory) {
        if (null != propertyValue) {
            factory.addPropertyValue(propertyName, propertyValue);
        }
    }
}
