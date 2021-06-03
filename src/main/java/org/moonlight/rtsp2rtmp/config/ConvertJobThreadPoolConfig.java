package org.moonlight.rtsp2rtmp.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 〈功能简述〉<br>
 * 〈转流任务线程池配置〉
 *
 * @author Moonlight
 * @date 2021/6/2 10:20
 */
@Slf4j
@Configuration
public class ConvertJobThreadPoolConfig {

    @Value("${dss.convert.job-limit}")
    private Integer jobLimit;

    @Bean
    public ThreadPoolExecutor convertJobExecutor() {
        int coreSize = jobLimit == null || jobLimit < 1 ? 10 : jobLimit;
        return new ThreadPoolExecutor(
                coreSize,
                2 * coreSize,
                60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(coreSize),
                new ConvertJobThreadFactory(),
                new ConvertJobPolicy()
        );
    }

    static class ConvertJobThreadFactory implements ThreadFactory {
        private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        ConvertJobThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "convert-job-" +
                    POOL_NUMBER.getAndIncrement() +
                    "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            t.setDaemon(false);
            t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }

    static class ConvertJobPolicy implements RejectedExecutionHandler {

        ConvertJobPolicy() { }

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            log.error("转流任务[{}]被线程池[{}]拒绝执行，可能线程池已达到负载上限或线程池已经处于非运行态", r.toString(), e.toString());
        }
    }

}