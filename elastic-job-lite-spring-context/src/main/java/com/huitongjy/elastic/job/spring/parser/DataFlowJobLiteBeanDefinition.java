package com.huitongjy.elastic.job.spring.parser;

import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.exception.JobConfigurationException;
import com.huitongjy.elastic.job.spring.properties.BaseJobLiteConfigProperties;
import com.huitongjy.elastic.job.spring.properties.DataFlowJobLiteConfigProperties;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

/**
 * DataFlow Job Lite BeanDefinition
 *
 * @author zhaoke
 * @since 2020/4/24
 **/
public class DataFlowJobLiteBeanDefinition extends AbstractJobLiteBeanDefinition {

    /**
     * Registry DataFlow Job Type
     *
     * @param map DataFlow类型集合 key:作业名称  value:作业详情配置
     */
    public DataFlowJobLiteBeanDefinition(Map<String, DataFlowJobLiteConfigProperties> map, BeanDefinitionRegistry registry) {
        if (null == map || map.size() == 0) {
            return;
        }
        for (Map.Entry<String, DataFlowJobLiteConfigProperties> entry : map.entrySet()) {
            doRegistryJobType(entry.getKey(), entry.getValue(), registry);
        }
    }

    @Override
    protected BeanDefinition getJobTypeConfigurationBeanDefinition(BaseJobLiteConfigProperties properties, BeanDefinition jobCoreBeanDefinition, BeanDefinitionRegistry registry) {
        if (!(properties instanceof DataFlowJobLiteConfigProperties)) {
            throw new JobConfigurationException("job properties is not DataFlowJobLiteConfigProperties!");
        }
        DataFlowJobLiteConfigProperties dataFlowJobLiteConfigProperties = (DataFlowJobLiteConfigProperties) properties;
        BeanDefinitionBuilder result = BeanDefinitionBuilder.rootBeanDefinition(DataflowJobConfiguration.class);
        result.addConstructorArgValue(jobCoreBeanDefinition);
        if (StringUtils.isEmpty(dataFlowJobLiteConfigProperties.getJobClass())) {
            result.addConstructorArgValue(registry.getBeanDefinition(dataFlowJobLiteConfigProperties.getJobRef()).getBeanClassName());
        } else {
            result.addConstructorArgValue(dataFlowJobLiteConfigProperties.getJobClass());
        }
        result.addConstructorArgValue(dataFlowJobLiteConfigProperties.isStreamingProcess());
        return result.getBeanDefinition();
    }
}
