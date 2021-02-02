package org.moonlight.dssdemo.common;

import java.util.regex.Pattern;

/**
 * 〈功能简述〉<br>
 * 〈〉
 *
 * @author Moonlight
 * @date 2020/11/18 14:12
 */
public final class Constant {

    private Constant () {}

    public static final Integer DEFAULT_KEEP_ALIVE_TIME = 180;

    public static final Integer DEFAULT_JOB_LIMIT = 5;

    public static final int DEFAULT_IMAGE_WIDTH = 640;

    public static final int DEFAULT_IMAGE_HEIGHT = 480;

    public static final Pattern NUMBER_PATTERN = Pattern.compile("^[1-9]\\d*$");

    public static final String PROTOCOL_IP_PORT = "http://192.168.164.188:80";

    public static final String CONTENT_PATH = "/admin/API";

    public static final String DEFAULT_BASE_CONTENT_PATH = PROTOCOL_IP_PORT + CONTENT_PATH;

    /* 对于鉴权、获取设备信息的流程 HLS 和 RTSP 是一样的 */
    public static final String DEFAULT_AUTHORIZE_URL = DEFAULT_BASE_CONTENT_PATH + "/accounts/authorize";
    /** 更新 TOKEN 的URL **/
    public static final String DEFAULT_UPDATE_TOKEN = DEFAULT_BASE_CONTENT_PATH + "/accounts/updateToken";
    /** 获取设备信息的URL **/
    public static final String  DEFAULT_GET_DEVICES_URL = DEFAULT_BASE_CONTENT_PATH + "/tree/devices";

    /** 获取 HLS 实时流地址的URL **/
    public static final String DEFAULT_HLS_GET_LIVE_URL = DEFAULT_BASE_CONTENT_PATH + "/hls/getLiveUrl";
    /** 获取 HLS 回放流地址的URL **/
    public static final String DEFAULT_HLS_GET_RECORD_URL = DEFAULT_BASE_CONTENT_PATH + "/hls/getRecordUrl";

    /** 获取 RTSP 获取实时流地址的URL **/
    public static final String DEFAULT_RTSP_GET_LIVE_URL = DEFAULT_BASE_CONTENT_PATH + "/MTS/Video/StartVideo";
    /** 获取 RTSP 获取回放流地址的URL **/
    public static final String DEFAULT_RTSP_GET_RECORD_URL = DEFAULT_BASE_CONTENT_PATH + "/SS/Playback/StartPlaybackByTime";
}
