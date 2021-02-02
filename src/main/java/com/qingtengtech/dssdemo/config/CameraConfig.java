package com.qingtengtech.dssdemo.config;

import com.qingtengtech.dssdemo.common.Constant;
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
    /** rtmpUrl的前缀 - rtmp地址的前缀，示例: rtmp://NginxIP:Nginx-rtmp-server-监听端口/Nginx-rtmp-server-application/ **/
    private String rtmpUrlPrefix;

    /** hlsUrl模板 - 拼接hls流地址的模板,示例: http://NginxIP:Nginx-server-监听端口/Nginx-server-location/SEQ/index.m3u8 **/
    private String hlsUrlTmplate;

    /** 考虑到服务器的压力，设定一个转流任务上限, 该值的设定必须大于0才有效 **/
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
}