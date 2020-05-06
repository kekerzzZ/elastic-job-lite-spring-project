package com.huitongjy.elastic.job.spring.parser;

import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.event.rdb.JobEventRdbConfiguration;
import com.dangdang.ddframe.job.executor.handler.JobProperties;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.huitongjy.elastic.job.spring.properties.BaseJobLiteConfigProperties;
import com.huitongjy.elastic.job.spring.properties.DistributedListenerProperties;
import java.util.EnumMap;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultBeanNameGenerator;
import org.springframework.beans.factory.support.ManagedList;

/**
 * Abstract Job Lite  BeanDefinition
 *
 * @author zhaoke
 * @since 2020/4/22
 **/
public abstract class AbstractJobLiteBeanDefinition {

    /**
     * do registry for Job
     *
     * @param jobName    job name
     * @param properties job config properties
     * @param registry   {@link BeanDefinitionRegistry}
     */
    protected void doRegistryJobType(String jobName, BaseJobLiteConfigProperties properties, BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(SpringJobScheduler.class);
        factory.setInitMethodName("init");
        if (StringUtils.isEmpty(properties.getJobName())) {
            properties.setJobName(jobName);
        }
        String jobClass = properties.getJobClass();
        String jobRef = properties.getJobRef();
        if (StringUtils.isNotEmpty(jobRef)) {
            factory.addConstructorArgReference(jobRef);
        } else {
            if (StringUtils.isNotEmpty(jobClass)) {
                factory.addConstructorArgValue(BeanDefinitionBuilder.rootBeanDefinition(properties.getJobClass()).getBeanDefinition());
            } else {
                factory.addConstructorArgValue(null);
            }
        }
        factory.addConstructorArgReference(properties.getRegistryCenterRef());
        factory.addConstructorArgValue(createLiteJobConfiguration(properties, registry));
        BeanDefinition jobEventConfig = createJobEventConfig(properties);
        if (null != jobEventConfig) {
            factory.addConstructorArgValue(jobEventConfig);
        }
        factory.addConstructorArgValue(createJobListeners(properties));
        String beanName = DefaultBeanNameGenerator.INSTANCE.generateBeanName(factory.getBeanDefinition(), registry);
        registry.registerBeanDefinition(beanName, factory.getBeanDefinition());
    }

    private BeanDefinition createLiteJobConfiguration(final BaseJobLiteConfigProperties properties, final BeanDefinitionRegistry registry) {
        return createLiteJobConfiguration(properties, createJobCoreBeanDefinition(properties), registry);
    }

    private BeanDefinition createLiteJobConfiguration(final BaseJobLiteConfigProperties properties, final BeanDefinition jobCoreBeanDefinition, final BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(LiteJobConfiguration.class);
        factory.addConstructorArgValue(getJobTypeConfigurationBeanDefinition(properties, jobCoreBeanDefinition, registry));
        factory.addConstructorArgValue(properties.isMonitorExecution());
        factory.addConstructorArgValue(properties.getMaxTimeDiffSeconds());
        factory.addConstructorArgValue(properties.getMonitorPort());
        factory.addConstructorArgValue(properties.getJobShardingStrategyClass());
        factory.addConstructorArgValue(properties.getReconcileIntervalMinutes());
        factory.addConstructorArgValue(properties.isDisabled());
        factory.addConstructorArgValue(properties.isOverwrite());
        return factory.getBeanDefinition();
    }

    private BeanDefinition createJobCoreBeanDefinition(final BaseJobLiteConfigProperties properties) {
        BeanDefinitionBuilder jobCoreBeanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(JobCoreConfiguration.class);
        jobCoreBeanDefinitionBuilder.addConstructorArgValue(properties.getJobName());
        jobCoreBeanDefinitionBuilder.addConstructorArgValue(properties.getCron());
        jobCoreBeanDefinitionBuilder.addConstructorArgValue(properties.getShardingTotalCount());
        jobCoreBeanDefinitionBuilder.addConstructorArgValue(properties.getShardingItemParameters());
        jobCoreBeanDefinitionBuilder.addConstructorArgValue(properties.getJobParameter());
        jobCoreBeanDefinitionBuilder.addConstructorArgValue(properties.isFailover());
        jobCoreBeanDefinitionBuilder.addConstructorArgValue(properties.isMisfire());
        jobCoreBeanDefinitionBuilder.addConstructorArgValue(properties.getDescription());
        jobCoreBeanDefinitionBuilder.addConstructorArgValue(createJobPropertiesBeanDefinition(properties));
        return jobCoreBeanDefinitionBuilder.getBeanDefinition();
    }

    private BeanDefinition createJobPropertiesBeanDefinition(final BaseJobLiteConfigProperties properties) {
        BeanDefinitionBuilder result = BeanDefinitionBuilder.rootBeanDefinition(JobProperties.class);
        EnumMap<JobProperties.JobPropertiesEnum, String> map = new EnumMap<>(JobProperties.JobPropertiesEnum.class);
        map.put(JobProperties.JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER, properties.getExecutorServiceHandler());
        map.put(JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER, properties.getJobExceptionHandler());
        result.addConstructorArgValue(map);
        return result.getBeanDefinition();
    }

    private BeanDefinition createJobEventConfig(final BaseJobLiteConfigProperties properties) {
        String eventTraceDataSourceName = properties.getEventTraceRdbDataSource();
        if (StringUtils.isEmpty(eventTraceDataSourceName)) {
            return null;
        }
        BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(JobEventRdbConfiguration.class);
        factory.addConstructorArgReference(eventTraceDataSourceName);
        return factory.getBeanDefinition();
    }

    private List<BeanDefinition> createJobListeners(final BaseJobLiteConfigProperties properties) {
        String listenerClass = properties.getListenerClass();
        DistributedListenerProperties distributedListenerProperties = properties.getDistributedListener();
        List<BeanDefinition> result = new ManagedList<>(2);
        if (StringUtils.isNotEmpty(listenerClass)) {
            BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(listenerClass);
            factory.setScope(BeanDefinition.SCOPE_PROTOTYPE);
            result.add(factory.getBeanDefinition());
        }
        if (null != distributedListenerProperties) {
            BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(distributedListenerProperties.getJobListenerClass());
            factory.setScope(BeanDefinition.SCOPE_PROTOTYPE);
            factory.addConstructorArgValue(distributedListenerProperties.getStartedTimeoutMilliseconds());
            factory.addConstructorArgValue(distributedListenerProperties.getCompletedTimeoutMilliseconds());
            result.add(factory.getBeanDefinition());
        }
        return result;
    }

    protected abstract BeanDefinition getJobTypeConfigurationBeanDefinition(BaseJobLiteConfigProperties properties, BeanDefinition jobCoreBeanDefinition, BeanDefinitionRegistry registry);
}
