package com.huitongjy.elastic.job.spring.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Script Job Lite Config Properties
 *
 * @author zhaoke
 * @since 2020/4/22
 **/
@Getter
@Setter
@ToString(callSuper = true)
public class ScriptJobLiteConfigProperties extends BaseJobLiteConfigProperties {

    /**
     * 脚本型作业执行命令行
     */
    private String scriptCommandLine;
}
