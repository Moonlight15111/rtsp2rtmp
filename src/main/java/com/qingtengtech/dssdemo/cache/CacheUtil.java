package com.qingtengtech.dssdemo.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.qingtengtech.dssdemo.convert.ConvertJob;
import com.qingtengtech.dssdemo.convert.RtspToRtmpConvert;
import com.qingtengtech.dssdemo.vo.CameraVO;

/**
 * 〈功能简述〉<br>
 * 〈〉
 *
 * @author Moonlight
 * @date 2021/1/23 15:58
 */
public final class CacheUtil {
    /** 转流中的摄像头 - key: rtsp地址  val: 摄像头对象 **/
    public static final Map<String, CameraVO> CONVERTING_CAMERA_CACHE = new ConcurrentHashMap<>(16);

    /** 执行中的Convert - key: rtsp地址 val: Convert对象 **/
    public static final Map<String, RtspToRtmpConvert> CONVERT_CACHE = new ConcurrentHashMap<>(16);

    /** 转流任务 - key: rtsp地址 val: Convert对象 **/
    public static final Map<String, ConvertJob> CONVERT_JOB_CACHE = new ConcurrentHashMap<>(16);
}