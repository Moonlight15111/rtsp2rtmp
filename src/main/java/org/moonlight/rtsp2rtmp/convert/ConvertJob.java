package org.moonlight.rtsp2rtmp.convert;

import org.moonlight.rtsp2rtmp.cache.CacheUtil;
import org.moonlight.rtsp2rtmp.vo.convert.CameraVO;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;

/**
 * 〈功能简述〉<br>
 * 〈〉
 * 转流任务 - 通过摄像头对象{@link CameraVO} 生成转流任务，转流任务执行时根据摄像头对象{@link CameraVO}生成转流器对象{@link RtspToRtmpConvert}进行转流
 * @author Moonlight
 * @date 2021/1/23 17:09
 */
@Slf4j
public class ConvertJob implements Runnable {

    private CameraVO cameraVO;

    private Throwable ex;

    private Thread execute;

    public ConvertJob(CameraVO cameraVO) {
        this.cameraVO = cameraVO;
    }

    public void exitConvert() {
        RtspToRtmpConvert rtspToRtmpConvert = CacheUtil.CONVERT_CACHE.get(cameraVO.getRtspUrl());
        if (rtspToRtmpConvert != null) {
            rtspToRtmpConvert.exitConvert();
        }
    }

    @Override
    public void run() {
        try {
            this.execute = Thread.currentThread();
            // 转流中的摄像头放入缓存
            CacheUtil.CONVERTING_CAMERA_CACHE.put(cameraVO.getRtspUrl(), cameraVO);
            // 创建转换器
            RtspToRtmpConvert rtspToRtmpConvert = new RtspToRtmpConvert(cameraVO);
            // 转换器放入缓存
            CacheUtil.CONVERT_CACHE.put(cameraVO.getRtspUrl(), rtspToRtmpConvert);
            // 执行转流
            rtspToRtmpConvert.convert();

            log.info("cameraVo[{}]转流被中断或转流完毕", cameraVO);
        } catch (FrameGrabber.Exception | FrameRecorder.Exception e) {
            log.error("执行转流任务时出错cameraVo[{}]", cameraVO, e);
            this.ex = e;
        } finally {
            // 不管怎么样都清除缓存
            CacheUtil.removeCache(cameraVO.getRtspUrl());
        }
    }
}
