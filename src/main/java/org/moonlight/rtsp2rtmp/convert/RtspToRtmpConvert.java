package org.moonlight.rtsp2rtmp.convert;

import org.moonlight.rtsp2rtmp.vo.convert.CameraVO;
import org.apache.commons.lang3.StringUtils;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.*;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 〈功能简述〉<br>
 * 〈〉
 * RTSP 转 RTMP
 *
 * ffmpeg错误码参考：http://t.zoukankan.com/wanggang123-p-5593145.html  https://blog.csdn.net/lipengshiwo/article/details/52610168
 *
 * @author Moonlight
 * @date 2021/1/23 16:38
 */
class RtspToRtmpConvert {

    static {
        // 开启javacv日志，方便查错
        System.setProperty("org.bytedeco.javacpp.logger", "slf4j");
        avutil.av_log_set_level(avutil.AV_LOG_INFO);
        avutil.setLogCallback(FFmpegLogCallback.getInstance());
    }

    private final AtomicBoolean exitConvert = new AtomicBoolean(false);

    private final CameraVO cameraVO;

    RtspToRtmpConvert(CameraVO cameraVO) {
        this.cameraVO = cameraVO;
    }

    void convert() throws FrameGrabber.Exception, FrameRecorder.Exception {
        FFmpegFrameGrabber grabber = null;
        FFmpegFrameRecorder recorder = null;
        try {
            grabber = FFmpegFrameGrabber.createDefault(cameraVO.getRtspUrl());
            // 使用tcp的方式，不然会丢包很严重
            grabber.setOption("rtsp_transport", "tcp");

            grabber.setImageWidth(cameraVO.getConfig().getWidth());
            grabber.setImageHeight(cameraVO.getConfig().getHeight());

            grabber.start();
            // 流媒体输出地址，分辨率（长，高），是否录制音频（0:不录制/1:录制）
            recorder = new FFmpegFrameRecorder(cameraVO.getRtmpUrl(), cameraVO.getConfig().getWidth(), cameraVO.getConfig().getHeight(), 0);
            recorder.setInterleaved(true);
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            // rtmp的类型
            recorder.setFormat("flv");
            // 降低编码延时
            if (StringUtils.isNotBlank(cameraVO.getConfig().getTune())) {
                recorder.setVideoOption("tune", cameraVO.getConfig().getTune());
            }
            /*
             ** 权衡quality(视频质量)和encode speed(编码速度) values(值)： *
             * ultrafast(终极快),superfast(超级快), veryfast(非常快), faster(很快), fast(快), *
             * medium(中等), slow(慢), slower(很慢), veryslow(非常慢) *
             * ultrafast(终极快)提供最少的压缩（低编码器CPU）和最大的视频流大小；而veryslow(非常慢)提供最佳的压缩（高编码器CPU）的同时降低视频流的大小
             * */
            if (StringUtils.isNotBlank(cameraVO.getConfig().getPreset())) {
                recorder.setVideoOption("preset", cameraVO.getConfig().getPreset());
            }
            // 画面质量参数，0~51；18~28 是一个合理范围 (详见 https://trac.ffmpeg.org/wiki/Encode/H.264)
            if (cameraVO.getConfig().getCrf() != null && cameraVO.getConfig().getCrf() >= 0 && cameraVO.getConfig().getCrf() <= 51) {
                recorder.setVideoOption("crf", String.valueOf(cameraVO.getConfig().getCrf()));
            }
            // 帧率
            recorder.setFrameRate(25);
            // 关键帧间隔，一般与帧率相同或者是视频帧率的两倍
            recorder.setGopSize(50);
            recorder.setImageWidth(cameraVO.getConfig().getWidth());
            recorder.setImageHeight(cameraVO.getConfig().getHeight());
            // yuv420p
            recorder.setPixelFormat(0);

            recorder.start();

            Frame frame;
            while(!exitConvert.get()){
                frame = grabber.grabImage();
                if(frame == null){
                    continue;
                }
                recorder.record(frame);
            }
        } finally {
            if (grabber != null) {
                grabber.close();
            }
            if (recorder != null) {
                recorder.close();
            }
        }
    }

    void exitConvert() {
        this.exitConvert.set(true);
    }
}