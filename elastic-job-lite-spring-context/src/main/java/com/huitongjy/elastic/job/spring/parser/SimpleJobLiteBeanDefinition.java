package com.huitongjy.elastic.job.spring.parser;

import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.exception.JobConfigurationException;
import com.huitongjy.elastic.job.spring.properties.BaseJobLiteConfigProperties;
import com.huitongjy.elastic.job.spring.properties.SimpleJobLiteConfigProperties;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

/**
 * Simple Job Lite BeanDefinition
 *
 * @author zhaoke
 * @since 2020/4/23
 **/
public class SimpleJobLiteBeanDefinition extends AbstractJobLiteBeanDefinition {

    public SimpleJobLiteBeanDefinition(Map<String, SimpleJobLiteConfigProperties> map, BeanDefinitionRegistry registry) {
        if (null == map || map.size() == 0) {
            return;
        }
        for (Map.Entry<String, SimpleJobLiteConfigProperties> entry : map.entrySet()) {
            doRegistryJobType(entry.getKey(), entry.getValue(), registry);
        }
    }

    protected BeanDefinition getJobTypeConfigurationBeanDefinition(final BaseJobLiteConfigProperties properties, final BeanDefinition jobCoreConfigurationBeanDefinition, final BeanDefinitionRegistry registry) {
        if (!(properties instanceof SimpleJobLiteConfigProperties)) {
            throw new JobConfigurationException("job properties is not SimpleJobLiteConfigProperties!");
        }
        SimpleJobLiteConfigProperties simpleJobLiteConfigProperties = (SimpleJobLiteConfigProperties) properties;
        BeanDefinitionBuilder result = BeanDefinitionBuilder.rootBeanDefinition(SimpleJobConfiguration.class);
        result.addConstructorArgValue(jobCoreConfigurationBeanDefinition);
        if (StringUtils.isEmpty(simpleJobLiteConfigProperties.getJobClass())) {
            result.addConstructorArgValue(registry.getBeanDefinition(simpleJobLiteConfigProperties.getJobRef()).getBeanClassName());
        } else {
            result.addConstructorArgValue(simpleJobLiteConfigProperties.getJobClass());
        }
        return result.getBeanDefinition();
    }
}
