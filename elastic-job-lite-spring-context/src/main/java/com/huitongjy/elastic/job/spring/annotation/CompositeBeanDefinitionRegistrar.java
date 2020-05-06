package com.huitongjy.elastic.job.spring.annotation;

import com.huitongjy.elastic.job.spring.parser.DataFlowJobLiteBeanDefinition;
import com.huitongjy.elastic.job.spring.parser.ElasticJobConfigParser;
import com.huitongjy.elastic.job.spring.parser.ScriptJobLiteBeanDefinition;
import com.huitongjy.elastic.job.spring.parser.SimpleJobLiteBeanDefinition;
import com.huitongjy.elastic.job.spring.properties.CompositeJobLiteConfigProperties;
import com.huitongjy.elastic.job.spring.properties.DataFlowJobLiteConfigProperties;
import com.huitongjy.elastic.job.spring.properties.ScriptJobLiteConfigProperties;
import com.huitongjy.elastic.job.spring.properties.SimpleJobLiteConfigProperties;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Composite Job Lite {@link ImportBeanDefinitionRegistrar} BeanDefinition Registrar
 *
 * @author zhaoke
 * @see AbstractBeanDefinition
 * @see SimpleJobLiteBeanDefinition
 * @see DataFlowJobLiteBeanDefinition
 * @see ScriptJobLiteBeanDefinition
 * @since 2020/4/24
 **/
public class CompositeBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, BeanFactoryAware {

    protected CompositeJobLiteConfigProperties compositeJobLiteConfigProperties;

    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    protected void setCompositeJobLiteConfigProperties() {
        compositeJobLiteConfigProperties = null;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator beanNameGenerator) {
        setCompositeJobLiteConfigProperties();
        if (null == compositeJobLiteConfigProperties) {
            compositeJobLiteConfigProperties = new CompositeJobLiteConfigProperties();
        }
        //解析ElasticJobLiteConfig注解
        ElasticJobConfigParser elasticJobConfigParser = new ElasticJobConfigParser(compositeJobLiteConfigProperties, importingClassMetadata, beanFactory);
        elasticJobConfigParser.parseElasticJobLiteAnnotation();
        //Registry Simple Job Lite.
        Map<String, SimpleJobLiteConfigProperties> simpleJobConfig = compositeJobLiteConfigProperties.getSimpleJobConfig();
        BeanDefinitionBuilder simpleBeanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(SimpleJobLiteBeanDefinition.class);
        simpleBeanDefinitionBuilder.addConstructorArgValue(simpleJobConfig);
        simpleBeanDefinitionBuilder.addConstructorArgValue(registry);
        String simpleBeanName = beanNameGenerator.generateBeanName(simpleBeanDefinitionBuilder.getBeanDefinition(), registry);
        registry.registerBeanDefinition(simpleBeanName, simpleBeanDefinitionBuilder.getBeanDefinition());
        //Registry DataFlow Job Lite.
        Map<String, DataFlowJobLiteConfigProperties> dataFlowJobConfig = compositeJobLiteConfigProperties.getDataFlowJobConfig();
        BeanDefinitionBuilder dataFlowBeanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(DataFlowJobLiteBeanDefinition.class);
        dataFlowBeanDefinitionBuilder.addConstructorArgValue(dataFlowJobConfig);
        dataFlowBeanDefinitionBuilder.addConstructorArgValue(registry);
        String dataFlowBeanName = beanNameGenerator.generateBeanName(dataFlowBeanDefinitionBuilder.getBeanDefinition(), registry);
        registry.registerBeanDefinition(dataFlowBeanName, dataFlowBeanDefinitionBuilder.getBeanDefinition());
        //Registry Script Job Lite.
        Map<String, ScriptJobLiteConfigProperties> scriptJobConfig = compositeJobLiteConfigProperties.getScriptJobConfig();
        BeanDefinitionBuilder scriptBeanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(ScriptJobLiteBeanDefinition.class);
        scriptBeanDefinitionBuilder.addConstructorArgValue(scriptJobConfig);
        scriptBeanDefinitionBuilder.addConstructorArgValue(registry);
        String scriptBeanName = beanNameGenerator.generateBeanName(scriptBeanDefinitionBuilder.getBeanDefinition(), registry);
        registry.registerBeanDefinition(scriptBeanName, scriptBeanDefinitionBuilder.getBeanDefinition());
    }
}
