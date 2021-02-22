package org.moonlight.dssdemo.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.moonlight.dssdemo.convert.ConvertJob;
import org.moonlight.dssdemo.convert.RtspToRtmpConvert;
import org.moonlight.dssdemo.vo.CameraVO;

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

    public static void removeCache(String rtspUrl) {
        if (CONVERTING_CAMERA_CACHE.containsKey(rtspUrl)) {
            CONVERTING_CAMERA_CACHE.remove(rtspUrl);
        }
        if (CONVERT_CACHE.containsKey(rtspUrl)) {
            CONVERT_CACHE.remove(rtspUrl);
        }
        if (CONVERT_JOB_CACHE.containsKey(rtspUrl)) {
            CONVERT_JOB_CACHE.remove(rtspUrl);
        }
    }
}