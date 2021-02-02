package com.qingtengtech.dssdemo.scheduling;

import com.qingtengtech.dssdemo.cache.CacheUtil;
import com.qingtengtech.dssdemo.vo.CameraVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

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

    @Value("${app.keep-alive-time}")
    private Integer keepAliveTime;

    @Scheduled(cron = "0 0/1 * * * ?")
    public void clearInactiveConvertJob() {
        if (CacheUtil.CONVERTING_CAMERA_CACHE.isEmpty() && (!CacheUtil.CONVERT_CACHE.isEmpty() || !CacheUtil.CONVERT_JOB_CACHE.isEmpty())) {
            log.warn("检测到异常情况CONVERTING_CAMERA_CACHE有[{}]个,但CONVERT_CACHE有[{}]个CONVERT_JOB_CACHE有[{}]个.准备把CONVERT_CACHE,CONVERT_JOB_CACHE全部清空.",
                    CacheUtil.CONVERTING_CAMERA_CACHE.size(), CacheUtil.CONVERT_CACHE.size(), CacheUtil.CONVERT_JOB_CACHE.size());
            CacheUtil.CONVERT_CACHE.clear();
            CacheUtil.CONVERT_JOB_CACHE.clear();
        }

        if (!CacheUtil.CONVERTING_CAMERA_CACHE.isEmpty()) {
            long cur = System.currentTimeMillis();

            log.info("本次扫描到转流中的摄像头CONVERTING_CAMERA_CACHE[{}]个 执行中的Convert CONVERT_CACHE[{}]个 转流任务CONVERT_JOB_CACHE[{}]个",
                    CacheUtil.CONVERTING_CAMERA_CACHE.size(), CacheUtil.CONVERT_CACHE.size(), CacheUtil.CONVERT_JOB_CACHE.size());

            int removeCount = 0;
            CameraVO cameraVO;
            for (String rtsp : CacheUtil.CONVERTING_CAMERA_CACHE.keySet()) {
                cameraVO = CacheUtil.CONVERTING_CAMERA_CACHE.get(rtsp);
                if (cameraVO.getWatchCount() <= 0 || ((cur - cameraVO.getKeepAliveTime().getTime()) / 1000) > keepAliveTime) {
                    CacheUtil.CONVERT_JOB_CACHE.get(rtsp).exitConvert();
                    CacheUtil.CONVERT_JOB_CACHE.remove(rtsp);

                    CacheUtil.CONVERT_CACHE.remove(rtsp);

                    CacheUtil.CONVERTING_CAMERA_CACHE.remove(rtsp);

                    removeCount++;
                    log.info("移除无人观看或没有进行保活的转流任务[{}]成功", cameraVO);
                }
            }
            log.info("本次移除无人观看或没有进行保活的转流任务[{}]个", removeCount);
        }
    }


}
