package org.moonlight.rtsp2rtmp.config;

import org.moonlight.rtsp2rtmp.common.Constant;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 〈功能简述〉<br>
 * 〈〉
 * 摄像头相关配置
 * @author Moonlight
 * @date 2021/1/26 17:25
 */
@Data
@Accessors(chain = true)
public class CameraConfig {
    /** rtmpUrl的前缀 - 根据这个前缀来拼接rtmp地址,示例: rtmp://192.168.53.21:1935/hls/1 **/
    private String rtmpUrlPrefix;

    /** hlsUrl模板 - 根据这个模板来生成HLS Url,前端访问的时候也是访问这个地址，示例: http://192.168.53.21:8080/hls/1/index.m3u8 **/
    private String hlsUrlTmplate;

    /** 考虑到服务器的压力，设定一个转流任务上限, 默认设置为5 **/
    private Integer jobLimit;

    public Integer getJobLimit() {
        return jobLimit == null || jobLimit <= 0 ? Constant.DEFAULT_JOB_LIMIT : jobLimit;
    }

    /** 转流任务保活时间 **/
    private Integer keepAliveTime;

    public Integer getKeepAliveTime() {
        return keepAliveTime == null || keepAliveTime <= 0 ? Constant.DEFAULT_KEEP_ALIVE_TIME : keepAliveTime;
    }

    /** 图像宽 **/
    private Integer width;

    public Integer getWidth() {
        return width == null || width <= 0 ? Constant.DEFAULT_IMAGE_WIDTH : width;
    }

    /** 图像高 **/
    private Integer height;

    public Integer getHeight() {
        return height == null || height <= 0 ? Constant.DEFAULT_IMAGE_HEIGHT : height;
    }

    /** 降低编码延时 - zerolatency **/
    private String tune;
    /** crf 画面质量参数，0~51；18~28 是一个合理范围 (详见 https://trac.ffmpeg.org/wiki/Encode/H.264) **/
    private Integer crf;
    /** preset 权衡quality(视频质量)和encode speed(编码速度) values(值):
     * ultrafast(终极快),superfast(超级快), veryfast(非常快), faster(很快), fast(快),
     * medium(中等), slow(慢), slower(很慢), veryslow(非常慢)
     * ultrafast(终极快)提供最少的压缩（低编码器CPU）和最大的视频流大小；而veryslow(非常慢)提供最佳的压缩（高编码器CPU）的同时降低视频流的大小
     * **/
    private String preset;

    /** socket TCP IO 超时时间。这个参数主要是为了防止程序在{@link org.bytedeco.javacv.FFmpegFrameGrabber#start()}方法上吊死，
     * 这个方法是加了sync的里面默认是阻塞的，遇到网络不通、数据流错误等异常情况时，会导致该函数长时间不返回
     * ffmpeg中单位为微秒，所以实际使用时会将其乘以 1_000_000
     * 另外值得一提的是在连接有问题是{@link org.bytedeco.javacv.FFmpegFrameGrabber#start()}中会有重试机制，所以这个值不应该设置的很大
     * **/
    private Integer socketTimeout;

    public Integer getSocketTimeout() {
        return socketTimeout == null || socketTimeout <= 0 ? Constant.DEFAULT_SOCKET_TIME_OUT_SECOND : socketTimeout;
    }

    /** 等待（网络）读/写操作完成的最长时间 ffmpeg中单位为微秒，所以实际使用时会将其乘以 1_000_000 **/
    private Integer rwTimeout;

    public Integer getRwTimeout() {
        return rwTimeout == null || rwTimeout <= 0 ? Constant.DEFAULT_RW_TIME_OUT_SECOND : rwTimeout;
    }
}