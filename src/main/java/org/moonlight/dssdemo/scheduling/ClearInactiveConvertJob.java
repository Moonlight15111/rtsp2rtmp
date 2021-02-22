package org.moonlight.dssdemo.scheduling;

import org.moonlight.dssdemo.cache.CacheUtil;
import org.moonlight.dssdemo.config.CameraConfig;
import org.moonlight.dssdemo.config.CameraConfigProvide;
import org.moonlight.dssdemo.vo.CameraVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 〈功能简述〉<br>
 * 〈〉
 *
 * @author Moonlight
 * @date 2021/1/23 18:53
 */
@Slf4j
@Component
public class ClearInactiveConvertJob {

    private final CameraConfigProvide cameraConfigProvide;

    @Autowired
    public ClearInactiveConvertJob(CameraConfigProvide cameraConfigProvide) {
        this.cameraConfigProvide = cameraConfigProvide;
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    public void clearInactiveConvertJob() {
        CameraConfig cameraConfig = cameraConfigProvide.provide();
        if (cameraConfig == null) {
            log.error("清理不活跃转流任务失败,摄像头配置为空");
            return;
        }

        long cur = System.currentTimeMillis();

        log.info("本次扫描到转流中的摄像头CONVERTING_CAMERA_CACHE[{}]个 执行中的Convert CONVERT_CACHE[{}]个 转流任务CONVERT_JOB_CACHE[{}]个",
                CacheUtil.CONVERTING_CAMERA_CACHE.size(), CacheUtil.CONVERT_CACHE.size(), CacheUtil.CONVERT_JOB_CACHE.size());

        int removeCount = 0;
        CameraVO cameraVO;
        for (String rtsp : CacheUtil.CONVERTING_CAMERA_CACHE.keySet()) {
            cameraVO = CacheUtil.CONVERTING_CAMERA_CACHE.get(rtsp);
            if (cameraVO.getWatchCount() <= 0 || (((cur - cameraVO.getKeepAliveTime().getTime()) / 1000) >= cameraConfig.getKeepAliveTime())) {

                CacheUtil.CONVERT_JOB_CACHE.get(rtsp).exitConvert();
                CacheUtil.removeCache(rtsp);

                removeCount++;
            }
        }
        log.info("本次移除无人观看或没有进行保活的转流任务[{}]个", removeCount);
    }


}
