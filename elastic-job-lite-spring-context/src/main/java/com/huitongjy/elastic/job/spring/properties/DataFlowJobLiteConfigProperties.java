package com.huitongjy.elastic.job.spring.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Flow Job Lite Config Properties
 *
 * @author zhaoke
 * @since 2020/4/22
 **/
@Getter
@Setter
@ToString(callSuper = true)
public class DataFlowJobLiteConfigProperties extends BaseJobLiteConfigProperties {

    /**
     * 是否流式处理数据
     * 如果流式处理数据, 则fetchData不返回空结果将持续执行作业
     * 如果非流式处理数据, 则处理数据完成后作业结束
     */
    private boolean streamingProcess = false;

}
