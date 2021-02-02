package com.qingtengtech.dssdemo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Arrays;
import java.util.concurrent.Executor;

/**
 * 〈功能简述〉<br>
 * 〈〉
 *
 * @author Moonlight
 * @date 2021/1/20 14:55
 */
@Slf4j
@Configuration
@EnableAsync
public class AsyncTaskExecutorConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
	    /*最小线程数*/
        taskExecutor.setCorePoolSize(10);
	    /*最大线程数*/
        taskExecutor.setMaxPoolSize(40);
	    /*等待队列*/
        taskExecutor.setQueueCapacity(1000);
        taskExecutor.setThreadNamePrefix("dssdemo-async-task-");
        taskExecutor.initialize();

        return taskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            log.error("调用异步任务时发生意外错误 method[{}]params[{}]", method, Arrays.toString(params), ex);
        };
    }

}
