package org.moonlight.rtsp2rtmp.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 〈功能简述〉<br>
 * 〈转流任务线程池关闭handler〉
 * 应用关闭前应当清掉所有的转流任务并关停线程池
 * @author Moonlight
 * @date 2021/7/14 17:35
 */
@Slf4j
@Component
public class ContextClosedHandler implements ApplicationListener<ContextClosedEvent> {

    private final ThreadPoolExecutor covertJobExecutor;
    private final StreamConvertHandler streamConvertHandler;

    @Autowired
    public ContextClosedHandler(ThreadPoolExecutor covertJobExecutor, StreamConvertHandler streamConvertHandler) {
        this.covertJobExecutor = covertJobExecutor;
        this.streamConvertHandler = streamConvertHandler;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        try {
            streamConvertHandler.clearJob();
            covertJobExecutor.shutdownNow();
            covertJobExecutor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException | RuntimeException e) {
            log.warn("关闭转流任务线程池时发生异常", e);
        }
    }
}
