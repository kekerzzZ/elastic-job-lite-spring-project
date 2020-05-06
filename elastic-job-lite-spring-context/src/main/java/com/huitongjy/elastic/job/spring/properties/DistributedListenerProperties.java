package com.huitongjy.elastic.job.spring.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 作业监听配置
 *
 * @author zhaoke
 * @since 2020/4/23
 **/
@Getter
@Setter
@ToString
public class DistributedListenerProperties {

    /**
     * 前置后置任务分布式监听实现类，需继承AbstractDistributeOnceElasticJobListener类
     */
    private String jobListenerClass;

    /**
     * 最后一个作业执行前的执行方法的超时时间
     * 单位：毫秒
     */
    private long startedTimeoutMilliseconds = Long.MAX_VALUE;

    /**
     * 最后一个作业执行后的执行方法的超时时间
     * 单位：毫秒
     */
    private long completedTimeoutMilliseconds = Long.MAX_VALUE;

}
