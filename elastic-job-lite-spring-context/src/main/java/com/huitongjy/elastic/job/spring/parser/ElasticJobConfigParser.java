package com.huitongjy.elastic.job.spring.parser;

import com.dangdang.ddframe.job.api.JobType;
import com.dangdang.ddframe.job.exception.JobConfigurationException;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.google.common.collect.Sets;
import com.huitongjy.elastic.job.spring.annotation.ElasticJobLiteConfig;
import com.huitongjy.elastic.job.spring.annotation.EnableElasticJobLite;
import com.huitongjy.elastic.job.spring.properties.BaseJobLiteConfigProperties;
import com.huitongjy.elastic.job.spring.properties.CompositeJobLiteConfigProperties;
import com.huitongjy.elastic.job.spring.properties.DataFlowJobLiteConfigProperties;
import com.huitongjy.elastic.job.spring.properties.DistributedListenerProperties;
import com.huitongjy.elastic.job.spring.properties.ScriptJobLiteConfigProperties;
import com.huitongjy.elastic.job.spring.properties.SimpleJobLiteConfigProperties;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Annotation {@link ElasticJobLiteConfig} Properties Parser
 *
 * @author zhaoke
 * @see EnableElasticJobLite
 * @see ElasticJobLiteConfig
 * @since 2020/4/25
 **/
@RequiredArgsConstructor
public class ElasticJobConfigParser {

    private final CompositeJobLiteConfigProperties compositeJobLiteConfigProperties;

    private final AnnotationMetadata importingClassMetadata;

    private final BeanFactory beanFactory;

    private static final Set<String> JobTypes = Collections.unmodifiableSet(Sets.newHashSet("SimpleJob", "DataflowJob", "ScriptJob"));

    public void parseElasticJobLiteAnnotation() {
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) beanFactory;
        String registryRef = getRegistryRef(importingClassMetadata, defaultListableBeanFactory);
        Map<String, Object> beansWithAnnotation = defaultListableBeanFactory.getBeansWithAnnotation(ElasticJobLiteConfig.class);
        for (Map.Entry<String, Object> each : beansWithAnnotation.entrySet()) {
            Object confBean = each.getValue();
            Class<?> clazz = confBean.getClass();
            String jobTypeName = clazz.getInterfaces()[0].getSimpleName();
            if (!JobTypes.contains(jobTypeName)) {
                jobTypeName = clazz.getSuperclass().getInterfaces()[0].getSimpleName();
                if (!JobTypes.contains(jobTypeName)) {
                    throw new JobConfigurationException("job should implement [%s or %s or %s]", JobType.SIMPLE, JobType.DATAFLOW, JobType.SCRIPT);
                }
                clazz = clazz.getSuperclass();
            }
            ElasticJobLiteConfig properties = AnnotationUtils.findAnnotation(clazz, ElasticJobLiteConfig.class);
            if (null == properties) {
                continue;
            }
            JobType jobType = parseJobType(jobTypeName);
            if (JobType.SIMPLE == jobType) {
                compositeJobLiteConfigProperties.getSimpleJobConfig().put(properties.jobName(), getSimpleJobLiteConfigProperties(clazz, registryRef, properties));
            } else if (JobType.DATAFLOW == jobType) {
                compositeJobLiteConfigProperties.getDataFlowJobConfig().put(properties.jobName(), getDataFlowJobLiteConfigProperties(clazz, registryRef, properties));
            } else {
                compositeJobLiteConfigProperties.getScriptJobConfig().put(properties.jobName(), getScriptJobLiteConfigProperties(clazz, registryRef, properties));
            }
        }
    }

    private String getRegistryRef(AnnotationMetadata importingClassMetadata, DefaultListableBeanFactory beanFactory) {
        AnnotationAttributes globalAttributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(EnableElasticJobLite.class.getName()));
        String registryRef = null;
        if (null != globalAttributes) {
            registryRef = globalAttributes.getString("registryRef");
        }
        if (StringUtils.isEmpty(registryRef)) {
            String[] registryRefs = beanFactory.getBeanNamesForType(ZookeeperRegistryCenter.class);
            if (ArrayUtils.isEmpty(registryRefs)) {
                throw new JobConfigurationException("not found registry config.");
            }
            for (String ref : registryRefs) {
                if (null != ref) {
                    registryRef = ref;
                    break;
                }
            }
        }
        if (null == registryRef) {
            throw new JobConfigurationException("not found registry config.");
        }
        return registryRef;
    }

    private JobType parseJobType(String jobTypeName) {
        if ("SimpleJob".equals(jobTypeName)) {
            return JobType.SIMPLE;
        } else if ("DataflowJob".equals(jobTypeName)) {
            return JobType.DATAFLOW;
        } else if ("ScriptJob".equals(jobTypeName)) {
            return JobType.SCRIPT;
        }
        throw new JobConfigurationException("jobType is missing match , %s is not in [%s, %s, %s]", jobTypeName, "SimpleJob", "DataflowJob", "ScriptJob");
    }

    private SimpleJobLiteConfigProperties getSimpleJobLiteConfigProperties(Class<?> jobClass, String registryRef, ElasticJobLiteConfig properties) {
        SimpleJobLiteConfigProperties result = new SimpleJobLiteConfigProperties();
        BaseJobLiteConfigProperties source = copyProperties(jobClass, registryRef, properties);
        BeanUtils.copyProperties(source, result);
        return result;
    }

    private DataFlowJobLiteConfigProperties getDataFlowJobLiteConfigProperties(Class<?> jobClass, String registryRef, ElasticJobLiteConfig properties) {
        DataFlowJobLiteConfigProperties result = new DataFlowJobLiteConfigProperties();
        BaseJobLiteConfigProperties source = copyProperties(jobClass, registryRef, properties);
        BeanUtils.copyProperties(source, result);
        result.setStreamingProcess(properties.streamProcess());
        return result;
    }

    private ScriptJobLiteConfigProperties getScriptJobLiteConfigProperties(Class<?> jobClass, String registryRef, ElasticJobLiteConfig properties) {
        ScriptJobLiteConfigProperties result = new ScriptJobLiteConfigProperties();
        BaseJobLiteConfigProperties source = copyProperties(jobClass, registryRef, properties);
        BeanUtils.copyProperties(source, result);
        result.setScriptCommandLine(properties.scriptCommandline());
        return result;
    }

    private BaseJobLiteConfigProperties copyProperties(Class<?> jobClass, String registryRef, ElasticJobLiteConfig properties) {
        BaseJobLiteConfigProperties result = new BaseJobLiteConfigProperties();
        result.setJobName(properties.jobName());
        result.setJobClass(jobClass.getName());
        result.setRegistryCenterRef(registryRef);
        result.setCron(properties.cron());
        result.setShardingTotalCount(properties.shardingTotalCount());
        result.setShardingItemParameters(properties.shardingItemParameters());
        result.setJobInstanceId(properties.jobInstanceId());
        result.setJobParameter(properties.jobParameter());
        result.setMonitorExecution(properties.monitorExecution());
        result.setMonitorPort(properties.monitorPort());
        result.setMaxTimeDiffSeconds(properties.maxTimeDiffSeconds());
        result.setFailover(properties.failover());
        result.setMisfire(properties.misfire());
        result.setJobShardingStrategyClass(properties.jobShardingStrategyClass());
        result.setDescription(properties.description());
        result.setDisabled(properties.disabled());
        result.setOverwrite(properties.overwrite());
        result.setExecutorServiceHandler(properties.executorServiceHandler());
        result.setJobExceptionHandler(properties.jobExceptionHandler());
        result.setReconcileIntervalMinutes(properties.reconcileIntervalMinutes());
        result.setEventTraceRdbDataSource(properties.eventTraceRdbDataSource());
        result.setListenerClass(properties.listener());
        if (StringUtils.isNotEmpty(properties.jobListenerClass())) {
            DistributedListenerProperties distributedListenerProperties = new DistributedListenerProperties();
            distributedListenerProperties.setJobListenerClass(properties.jobListenerClass());
            distributedListenerProperties.setStartedTimeoutMilliseconds(properties.startedTimeoutMilliseconds());
            distributedListenerProperties.setCompletedTimeoutMilliseconds(properties.completedTimeoutMilliseconds());
            result.setDistributedListener(distributedListenerProperties);
        }
        return result;
    }
}
