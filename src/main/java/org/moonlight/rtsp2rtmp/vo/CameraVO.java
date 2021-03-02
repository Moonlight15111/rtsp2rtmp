package org.moonlight.rtsp2rtmp.vo;

import org.moonlight.rtsp2rtmp.config.CameraConfig;
import lombok.Data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 〈功能简述〉<br>
 * 〈〉
 * 摄像头对象
 * @author Moonlight
 * @date 2021/1/23 16:03
 */
@Data
public class CameraVO implements Serializable {
    /** rtsp地址 **/
    private String rtspUrl;
    /** rtmp地址 **/
    private String rtmpUrl;
    /** HLS地址 - 这个地址由 NGINX代理 + 转流的编号拼接，实际播放的也是这个地址 **/
    private String hlsUrl;
    /** 开始转流的时间 **/
    private String beginTime;
    /** 当前观看这个流的人数, Note: 目前这个字段实际上并没有多大作用，因为没有唯一标识 **/
    private int watchCount;
    /** 保活时间 - 如果当前时间 - 保活时间 > 配置的值，则结束推流 **/
    private Date keepAliveTime;
    /** 配置项 **/
    private CameraConfig config;

    public CameraVO(String rtspUrl, String rtmpUrl, String hlsUrl, CameraConfig config) {
        this.rtspUrl = rtspUrl;
        this.rtmpUrl = rtmpUrl;
        this.hlsUrl = hlsUrl;
        this.config = config;
        this.beginTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
        this.watchCount = 1;
        this.keepAliveTime = new Date();
    }

}
