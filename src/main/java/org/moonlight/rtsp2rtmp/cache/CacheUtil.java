package org.moonlight.rtsp2rtmp.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.moonlight.rtsp2rtmp.convert.ConvertJob;
import org.moonlight.rtsp2rtmp.vo.convert.CameraVO;

/**
 * 〈功能简述〉<br>
 * 〈〉
 * rtsp 2 rtmp 任务缓存
 * @author Moonlight
 * @date 2021/1/23 15:58
 */
public final class CacheUtil {
    /** 转流中的摄像头 - key: rtsp地址  val: 摄像头对象 **/
    public static final Map<String, CameraVO> CONVERTING_CAMERA_CACHE = new ConcurrentHashMap<>(16);

    /** 转流任务 - key: rtsp地址 val: Convert对象 **/
    public static final Map<String, ConvertJob> CONVERT_JOB_CACHE = new ConcurrentHashMap<>(16);

    public static void removeCache(String rtspUrl) {
        if (CONVERT_JOB_CACHE.containsKey(rtspUrl)) {
            ConvertJob remove = CONVERT_JOB_CACHE.remove(rtspUrl);
            remove.exitConvert();
        }
        if (CONVERTING_CAMERA_CACHE.containsKey(rtspUrl)) {
            CONVERTING_CAMERA_CACHE.remove(rtspUrl);
        }
    }

    public static void clearJob() {
        for (String key : CacheUtil.CONVERT_JOB_CACHE.keySet()) {
            removeCache(key);
        }
        CacheUtil.CONVERTING_CAMERA_CACHE.clear();
        CacheUtil.CONVERT_JOB_CACHE.clear();
    }
}