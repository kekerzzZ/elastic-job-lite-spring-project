package com.huitongjy.elastic.job.spring.parser;

import com.dangdang.ddframe.job.config.script.ScriptJobConfiguration;
import com.dangdang.ddframe.job.exception.JobConfigurationException;
import com.huitongjy.elastic.job.spring.properties.BaseJobLiteConfigProperties;
import com.huitongjy.elastic.job.spring.properties.ScriptJobLiteConfigProperties;
import java.util.Map;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

/**
 * Script Job Lite BeanDefinition
 *
 * @author zhaoke
 * @since 2020/4/24
 **/
public class ScriptJobLiteBeanDefinition extends AbstractJobLiteBeanDefinition {

    /**
     * Registry Script Job Type
     *
     * @param map Script类型集合 key:作业名称  value:作业详情配置
     */
    public ScriptJobLiteBeanDefinition(Map<String, ScriptJobLiteConfigProperties> map, BeanDefinitionRegistry registry) {
        if (null == map || map.size() == 0) {
            return;
        }
        for (Map.Entry<String, ScriptJobLiteConfigProperties> entry : map.entrySet()) {
            doRegistryJobType(entry.getKey(), entry.getValue(), registry);
        }
    }

    @Override
    protected BeanDefinition getJobTypeConfigurationBeanDefinition(BaseJobLiteConfigProperties properties, BeanDefinition jobCoreBeanDefinition, BeanDefinitionRegistry registry) {
        if (!(properties instanceof ScriptJobLiteConfigProperties)) {
            throw new JobConfigurationException("job properties is not ScriptJobLiteConfigProperties!");
        }
        ScriptJobLiteConfigProperties scriptJobLiteConfigProperties = (ScriptJobLiteConfigProperties) properties;
        BeanDefinitionBuilder result = BeanDefinitionBuilder.rootBeanDefinition(ScriptJobConfiguration.class);
        result.addConstructorArgValue(jobCoreBeanDefinition);
        result.addConstructorArgValue(scriptJobLiteConfigProperties.getScriptCommandLine());
        return result.getBeanDefinition();
    }
}
