### Elastic Job Lite Spring Project

  由于目前项目基于注解开发逐渐成为趋势，但是目前`ElasticJob`目前支持`Java API`与`XML`的配置方式，所以本项目的目的就是兼容`ElasticJob`所有功能基础上增强注解的功能。<br/>
  使用步骤如下:<br/>
  
  - 在pom文件增加依赖
  
   ````  
   <dependency>
       <groupId>com.huitongjy.elasticjob.spring</groupId>
       <artifactId>elastic-job-lite-spring-context</artifactId>
       <version>${LAST_RELEASE_VERSION}</version>
   </dependency>
   ````
  - 创建自己的任务 <br/>
  在任务类上标注`@ElasticJobLiteConfig`注解并配置相应的参数，注解参数可参考[ElasticJob 官网](http://elasticjob.io/docs/elastic-job-lite/02-guide/config-manual/)
  ````
  @Component
  @ElasticJobLiteConfig(jobName = "testJob1", cron = "0 */1 * * * ?", shardingTotalCount = 1, overwrite = true)
  public class TestJob1 implements SimpleJob {
  
      @Override
      public void execute(ShardingContext shardingContext) {
          System.out.println("Test Job1 Executing...");
      }
  }

  @Component
  @ElasticJobLiteConfig(jobName = "testJob2", cron = "0 */1 * * * ?", shardingTotalCount = 1, overwrite = true)
  public class TestJob2 implements DataflowJob<String> {
    @Override
    public List<String> fetchData(ShardingContext shardingContext) {
        System.out.println("test job 2 fetch data execute...");
        return Lists.newArrayList("Hello World");
    }

    @Override
    public void processData(ShardingContext shardingContext, List data) {
        System.out.println("test job 2 process data execute...");
    }
  }

  ````
  - 启动任务 <br/>
    在配置类标注`@EnableElasticJobLite`即可。 同时可以指定引用`Zookeeper`注册中心，也可以不指定，只需配置即可。
  ````
    @Configuration
    @EnableElasticJobLite()
    public class ElasticJobConfig {
    }
  ````

  - `Zookeeper`注册中心配置 <br/>
   可以在根据环境配置来分别配置，同时也兼容ElasticJob原有的`Java API`与`XML`配置。
  ````
    elastic.job.lite.config.zookeeper.id=regCenter
    elastic.job.lite.config.zookeeper.server-lists=localhost:2181
    elastic.job.lite.config.zookeeper.namespace=exambase-elastic-job-dev
    elastic.job.lite.config.zookeeper.base-sleep-time-milliseconds=1000
    elastic.job.lite.config.zookeeper.max-sleep-time-milliseconds=3000
    elastic.job.lite.config.zookeeper.max-retries=3
  ````
  
  - 引入本项目同时兼容ElasticJob原来的任何用法。 既`Java API`与`XML`配置可以与注解方式共存。
