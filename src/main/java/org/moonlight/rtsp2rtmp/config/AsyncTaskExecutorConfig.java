//package org.moonlight.rtsp2rtmp.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.AsyncConfigurer;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//
//import java.util.Arrays;
//import java.util.concurrent.Executor;
//
///**
// * 〈功能简述〉<br>
// * 〈〉
// *
// * @author Moonlight
// * @date 2021/1/20 14:55
// */
//@Slf4j
//@EnableAsync
//@Configuration
//public class AsyncTaskExecutorConfig implements AsyncConfigurer {
//
//    @Value("${dss.convert.job-limit}")
//    private Integer jobLimit;
//
//    @Bean
//    @Override
//    public Executor getAsyncExecutor() {
//        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
//        taskExecutor.setCorePoolSize(jobLimit);
//        taskExecutor.setMaxPoolSize(2 * jobLimit);
//        taskExecutor.setQueueCapacity(jobLimit);
//        taskExecutor.setThreadNamePrefix("rtsp2rtmp-convert-job-");
//        return taskExecutor;
//    }
//
//    @Override
//    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
//        return (ex, method, params) -> {
//            log.error("调用异步任务时发生意外错误 method[{}]params[{}]", method, Arrays.toString(params), ex);
//        };
//    }
//
//}
