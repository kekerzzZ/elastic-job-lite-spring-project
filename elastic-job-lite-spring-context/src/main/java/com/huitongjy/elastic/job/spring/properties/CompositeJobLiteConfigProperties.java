package com.huitongjy.elastic.job.spring.properties;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * composite configuring job config.
 *
 * @author zhaoke
 * @since 2020/4/22
 **/
@Getter
@Setter
@ToString(callSuper = true)
public class CompositeJobLiteConfigProperties {

    /**
     * Simple任务集合
     */
    Map<String, SimpleJobLiteConfigProperties> simpleJobConfig = new HashMap<>();

    /**
     * DataFlow任务集合
     */
    Map<String, DataFlowJobLiteConfigProperties> dataFlowJobConfig = new HashMap<>();

    /**
     * Script任务集合
     */
    Map<String, ScriptJobLiteConfigProperties> scriptJobConfig = new HashMap<>();

}
