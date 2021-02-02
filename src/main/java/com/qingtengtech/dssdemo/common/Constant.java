package com.qingtengtech.dssdemo.common;

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

    public static final String PROTOCOL_IP_PORT = "http://10.0.15.200:80";

    public static final String CONTENT_PATH = "/admin/API";

    public static final String DEFAULT_BASE_CONTENT_PATH = PROTOCOL_IP_PORT + CONTENT_PATH;

    /* 对于鉴权、获取设备信息的流程 HLS 和 RTSP 是一样的 */
    /** 鉴权的URL - 根据对接文档的说明,需要进行两次鉴权
     * 详见金山云文档: 团队文档 - 项目 - 教育 - 202004顺德大良教育智慧数据应用 - 设计 - 大华视频监控对接 - DSS平台第三方平台对接文档 **/
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
