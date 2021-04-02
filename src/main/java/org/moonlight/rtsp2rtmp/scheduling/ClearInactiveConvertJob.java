package org.moonlight.rtsp2rtmp.scheduling;

import org.moonlight.rtsp2rtmp.cache.CacheUtil;
import org.moonlight.rtsp2rtmp.config.CameraConfig;
import org.moonlight.rtsp2rtmp.config.CameraConfigProvide;
import org.moonlight.rtsp2rtmp.vo.convert.CameraVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 〈功能简述〉<br>
 * 〈〉
 * 定时任务 - 清理观看人数为0或超出保活期限的转流任务
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

    @Scheduled(cron = "${dss.convert.clear-inactive-task-cron}")
    public void clearInactiveConvertJob() {
        CameraConfig cameraConfig = cameraConfigProvide.provide();
        if (cameraConfig == null) {
            log.error("清理不活跃转流任务失败,摄像头配置为空");
            return;
        }

        long cur = System.currentTimeMillis();

        log.info("本次扫描到转流中的摄像头CONVERTING_CAMERA_CACHE[{}]个 转流任务CONVERT_JOB_CACHE[{}]个",
                CacheUtil.CONVERTING_CAMERA_CACHE.size(), CacheUtil.CONVERT_JOB_CACHE.size());

        int removeCount = 0;
        CameraVO cameraVO;
        for (String rtsp : CacheUtil.CONVERTING_CAMERA_CACHE.keySet()) {
            cameraVO = CacheUtil.CONVERTING_CAMERA_CACHE.get(rtsp);
            if (cameraVO.getWatchCount() <= 0 || (((cur - cameraVO.getKeepAliveTime().getTime()) / 1000) >= cameraConfig.getKeepAliveTime())) {
                CacheUtil.removeCache(rtsp);
                removeCount++;
            }
        }

        log.info("本次移除无人观看或没有进行保活的转流任务[{}]个", removeCount);
    }


}
