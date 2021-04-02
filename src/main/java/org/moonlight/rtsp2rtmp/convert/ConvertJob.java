package org.moonlight.rtsp2rtmp.convert;

import lombok.Getter;
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

    private final CameraVO cameraVO;

    private final RtspToRtmpConvert rtspToRtmpConvert;

    @Getter
    private Throwable ex;

    @Getter
    private Thread execute;

    public ConvertJob(CameraVO cameraVO) {
        this.cameraVO = cameraVO;
        this.rtspToRtmpConvert = new RtspToRtmpConvert(this.cameraVO);
    }

    public void exitConvert() {
        rtspToRtmpConvert.exitConvert();
    }

    @Override
    public void run() {
        try {
            // 转流中的摄像头放入缓存
            CacheUtil.CONVERTING_CAMERA_CACHE.put(this.cameraVO.getRtspUrl(), this.cameraVO);
            log.info("cameraVo[{}]转流开始", this.cameraVO);
            // 执行转流
            this.rtspToRtmpConvert.convert();

            log.info("cameraVo[{}]转流被中断或转流完毕", this.cameraVO);
        } catch (FrameGrabber.Exception | FrameRecorder.Exception e) {
            log.error("执行转流任务时出错cameraVo[{}]", this.cameraVO, e);
        } finally {
            CacheUtil.removeCache(this.cameraVO.getRtspUrl());
        }
    }
}
