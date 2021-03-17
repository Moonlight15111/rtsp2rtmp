package org.moonlight.rtsp2rtmp.controller;

import org.moonlight.rtsp2rtmp.common.Constant;
import org.moonlight.rtsp2rtmp.handler.StreamConvertHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.moonlight.rtsp2rtmp.handler.VideoStreamHandler;
import org.moonlight.rtsp2rtmp.vo.*;
import org.moonlight.rtsp2rtmp.vo.dss.Device;
import org.moonlight.rtsp2rtmp.vo.dss.HlsStreamUrlQueryVO;
import org.moonlight.rtsp2rtmp.vo.dss.RtspStreamUrlQueryVO;
import org.moonlight.rtsp2rtmp.vo.dss.StreamUrlInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈功能简述〉<br>
 * 〈〉
 *
 * @author Moonlight
 * @date 2020/11/18 20:18
 */
@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    private final VideoStreamHandler videoStreamHandler;
    private final StreamConvertHandler streamConvertHandler;

    @Value("${app.rtsp-url}")
    private String rtspUrl;

    @Autowired
    public TestController(VideoStreamHandler videoStreamHandler, StreamConvertHandler streamConvertHandler) {
        this.videoStreamHandler = videoStreamHandler;
        this.streamConvertHandler = streamConvertHandler;
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * 获取token
     * @return String
     * @since 1.0.0
     * @author Moonlight
     * @date 2020/11/23 14:57
     */
    @GetMapping(value = "/token/get")
    public String getToken() {
        return videoStreamHandler.getToken();
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * 获取详细的token信息描述、如：token、保活时间、下次更新token的时间等
     * @return String
     * @since 1.0.0
     * @author Moonlight
     * @date 2020/11/23 14:57
     */
    @GetMapping(value = "/token/get/info")
    public String getTokenInfoStr() {
        return videoStreamHandler.getTokenInfoToString();
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * 更新token
     * @return String 更新token是否成功，新旧token分别是什么
     * @since 1.0.0
     * @author Moonlight
     * @date 2020/11/23 14:58
     */
    @GetMapping(value = "/token/update")
    public String updateToken() {
        String res = "old token: " + getToken() + " \r\n new token: ";
        res += videoStreamHandler.updateToken() ? getToken() : "刷新token失败了";
        return res;
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * 获取设备信息
     * @param orgCode 组织节点OrgCode
     * @param deviceCodes 设备编码，如果为空，则表示获取所有设备
     * @param categories 设备大类：1.编码器；5.卡口；8.门禁等
     * @return List<Device> 设备列表
     * @since 1.0.0
     * @author Moonlight
     * @date 2020/11/23 14:59
     */
    @GetMapping(value = "/get/devices")
    public List<Device> getDevices(@RequestParam(name = "orgCode", required = false) Integer orgCode
            , @RequestParam(name = "deviceCodes", required = false) String[] deviceCodes
            , @RequestParam(name = "categories", required = false) String[] categories) {
        return videoStreamHandler.getDevices(orgCode, deviceCodes, categories);
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * 获取HLS的实时流地址
     * @param deviceCode 设备编号
     * @param unitSeq 单元序号，因为都是编码通道，填1即可
     * @param chnSeq   通道序号
     * @param recordSource  录像来源， 2-设备，3-中心
     * @param recordType 录像类型 0-全部 1-普通录像 2-报警录像 当recordSource为3时，recordType的值要填0
     * @param streamType 码流类型 1-主码流，2-辅码流
     * @param beginTime 录像开始时间 格式：yyyy-MM-dd hh:mm:ss
     * @param endTime 录像结束时间 格式：yyyy-MM-dd hh:mm:ss
     * @return StreamUrlInfo
     * @since 1.0.0
     * @author Moonlight
     * @date 2020/11/23 15:01
     */
    @GetMapping(value = "/get/hls/url/live")
    public StreamUrlInfo getHlsLiveUrl(@RequestParam(name = "devicecode", required = false) String deviceCode, // 设备编号
                                       @RequestParam(name = "unitSeq", required = false) String unitSeq, // 单元序号，因为都是编码通道，填1即可
                                       @RequestParam(name = "chnSeq", required = false) String chnSeq, // 通道序号
                                       @RequestParam(name = "recordSource", required = false) String recordSource, // 录像来源， 2-设备，3-中心
                                       @RequestParam(name = "recordType", required = false) String recordType, // 录像类型 0-全部 1-普通录像 2-报警录像 当recordSource为3时，recordType的值要填0
                                       @RequestParam(name = "streamType", required = false) String streamType, // 码流类型 1-主码流，2-辅码流
                                       @RequestParam(name = "beginTime", required = false) String beginTime, // 录像开始时间 格式：yyyy-MM-dd hh:mm:ss
                                       @RequestParam(name = "endTime", required = false) String endTime) { // 录像结束时间 格式：yyyy-MM-dd hh:mm:ss
        List<HlsStreamUrlQueryVO> queryVOS = new ArrayList<>();
        queryVOS.add(new HlsStreamUrlQueryVO().setDevicecode(deviceCode).setUnitSeq(StringUtils.isBlank(unitSeq) ? "1" : unitSeq).setChnSeq(chnSeq)
                .setRecordSource(recordSource).setRecordType(recordType).setStreamType(streamType).setBeginTime(beginTime).setEndTime(endTime));

        return videoStreamHandler.getHlsLiveUrl(queryVOS);
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * 获取HLS的回放流地址
     * @param deviceCode 设备编号
     * @param unitSeq 单元序号，因为都是编码通道，填1即可
     * @param chnSeq   通道序号
     * @param recordSource  录像来源， 2-设备，3-中心
     * @param recordType 录像类型 0-全部 1-普通录像 2-报警录像 当recordSource为3时，recordType的值要填0
     * @param streamType 码流类型 1-主码流，2-辅码流
     * @param beginTime 录像开始时间 格式：yyyy-MM-dd hh:mm:ss
     * @param endTime 录像结束时间 格式：yyyy-MM-dd hh:mm:ss
     * @return StreamUrlInfo
     * @since 1.0.0
     * @author Moonlight
     * @date 2020/11/23 15:01
     */
    @GetMapping(value = "/get/hls/url/record")
    public StreamUrlInfo getHlsRecordUrl(@RequestParam(name = "devicecode", required = false) String deviceCode, // 设备编号
                                         @RequestParam(name = "unitSeq", required = false) String unitSeq, // 单元序号，因为都是编码通道，填1即可
                                         @RequestParam(name = "chnSeq", required = false) String chnSeq, // 通道序号
                                         @RequestParam(name = "recordSource", required = false) String recordSource, // 录像来源， 2-设备，3-中心
                                         @RequestParam(name = "recordType", required = false) String recordType, // 录像类型 0-全部 1-普通录像 2-报警录像 当recordSource为3时，recordType的值要填0
                                         @RequestParam(name = "streamType", required = false) String streamType, // 码流类型 1-主码流，2-辅码流
                                         @RequestParam(name = "beginTime", required = false) String beginTime, // 录像开始时间 格式：yyyy-MM-dd hh:mm:ss
                                         @RequestParam(name = "endTime", required = false) String endTime) { // 录像结束时间 格式：yyyy-MM-dd hh:mm:ss
        List<HlsStreamUrlQueryVO> queryVOS = new ArrayList<>();
        queryVOS.add(new HlsStreamUrlQueryVO().setDevicecode(deviceCode).setUnitSeq(StringUtils.isBlank(unitSeq) ? "1" : unitSeq).setChnSeq(chnSeq)
                .setRecordSource(recordSource).setRecordType(recordType).setStreamType(streamType).setBeginTime(beginTime).setEndTime(endTime));

        return videoStreamHandler.getHlsRecordUrl(queryVOS);
    }


    /**
     * 功能描述: <br>
     * 〈〉
     * 获取Rtsp的回放流地址
     * @param clientType 电脑名称
     * @param clientMac 电脑MAC地址
     * @param clientPushId   可不填
     * @param project  可不填
     * @param method  方法名
     * @param streamType 码流类型：1=主码流, 2=辅码流
//     * @param optional URI信息
     * @param trackId  轨道ID
     * @param extend   扩展数据
     * @param channelId  视频通道编码
     * @param planId  录像计划ID
     * @param dataType  视频类型：1=视频, 2=音频, 3=音视频
     * @return StreamUrlInfo
     * @since 1.0.0
     * @author Moonlight
     * @date 2020/11/23 15:01
     */
    @GetMapping(value = "/get/rtsp/url/live")
    public StreamUrlInfo getRtspLiveUrl(@RequestParam(name = "clientType", required = false) String clientType, // 电脑名称
                                        @RequestParam(name = "clientMac", required = false) String clientMac, // 电脑MAC地址
                                        @RequestParam(name = "clientPushId", required = false) String clientPushId, // 可不填
                                        @RequestParam(name = "project", required = false) String project, // 可不填
                                        @RequestParam(name = "method", required = false) String method, // 方法名
                                        @RequestParam(name = "streamType", required = false) String streamType, // 码流类型：1=主码流, 2=辅码流
//                                        @RequestParam(name = "optional", required = false) String optional,  // URI信息  这个应该要后台拼接才对
                                        @RequestParam(name = "trackId", required = false) String trackId, // 轨道ID
                                        @RequestParam(name = "extend", required = false) String extend, // 扩展数据
                                        @RequestParam(name = "channelId", required = false) String channelId, // 视频通道编码
                                        @RequestParam(name = "planId", required = false) String planId,  // 录像计划ID
                                        @RequestParam(name = "dataType", required = false) String dataType) { // 视频类型：1=视频, 2=音频, 3=音视频
        RtspStreamUrlQueryVO vo = new RtspStreamUrlQueryVO().setClientType(clientType)
                .setClientMac(clientMac).setClientPushId(clientPushId)
                .setProject(project).setMethod(method)
                .setData(new RtspStreamUrlQueryVO.Data()
                        .setStreamType(streamType)
                        .setOptional(Constant.DEFAULT_RTSP_GET_LIVE_URL.replace(Constant.PROTOCOL_IP_PORT, "") + "?token=")
                        .setTrackId(trackId)
                        .setExtend(extend)
                        .setChannelId(channelId)
                        .setPlanId(planId)
                        .setDataType(dataType));
        return videoStreamHandler.getRtspLiveUrl(vo);
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * 获取Rtsp的回放流地址
     * @param clientType 电脑名称
     * @param clientMac 电脑MAC地址
     * @param clientPushId   可不填
     * @param project  可不填
     * @param method  方法名
     * @param streamType 码流类型：1=主码流, 2=辅码流
//     * @param optional URI信息
     * @param trackId  轨道ID
     * @param extend   扩展数据
     * @param channelId  视频通道编码
     * @param planId  录像计划ID
     * @param dataType  视频类型：1=视频, 2=音频, 3=音视频
     * @return StreamUrlInfo
     * @since 1.0.0
     * @author Moonlight
     * @date 2020/11/23 15:01
     */
    @GetMapping(value = "/get/rtsp/url/record")
    public StreamUrlInfo getRtspRecordUrl(@RequestParam(name = "clientType", required = false) String clientType, // 电脑名称
                                          @RequestParam(name = "clientMac", required = false) String clientMac, // 电脑MAC地址
                                          @RequestParam(name = "clientPushId", required = false) String clientPushId, // 可不填
                                          @RequestParam(name = "project", required = false) String project, // 可不填
                                          @RequestParam(name = "method", required = false) String method, // 方法名
                                          @RequestParam(name = "streamType", required = false) String streamType, // 码流类型：1=主码流, 2=辅码流
//                                          @RequestParam(name = "optional", required = false) String optional,  // URI信息
                                          @RequestParam(name = "trackId", required = false) String trackId, // 轨道ID
                                          @RequestParam(name = "extend", required = false) String extend, // 扩展数据
                                          @RequestParam(name = "channelId", required = false) String channelId, // 视频通道编码
                                          @RequestParam(name = "planId", required = false) String planId,  // 录像计划ID
                                          @RequestParam(name = "dataType", required = false) String dataType) { // 视频类型：1=视频, 2=音频, 3=音视频
        RtspStreamUrlQueryVO vo = new RtspStreamUrlQueryVO().setClientType(clientType)
                .setClientMac(clientMac).setClientPushId(clientPushId)
                .setProject(project).setMethod(method)
                .setData(new RtspStreamUrlQueryVO.Data()
                        .setStreamType(streamType)
                        .setOptional(Constant.DEFAULT_RTSP_GET_LIVE_URL.replace(Constant.PROTOCOL_IP_PORT, "") + "?token=" + videoStreamHandler.getToken())
                        .setTrackId(trackId)
                        .setExtend(extend)
                        .setChannelId(channelId)
                        .setPlanId(planId)
                        .setDataType(dataType));
        return videoStreamHandler.getRtspRecordUrl(vo);
    }

    @GetMapping(value = "/rtmp_stream/convert")
    public ReturnVO rtmpStream() {
        try {
            return streamConvertHandler.rtsp2Rtmp(rtspUrl);
        } catch (RuntimeException e) {
            log.error("进行视频流转换时出错,rtsp[{}].", rtspUrl, e);
            return ReturnVO.error(-1, e.getMessage());
        }
    }

    @GetMapping(value = "/rtmp_stream/exit")
    public String exitConvert() {
       return String.valueOf(streamConvertHandler.exitConvert(rtspUrl));
    }

    @GetMapping(value = "/keep_alive")
    public String keepAlive() {
       return String.valueOf(streamConvertHandler.keepAlive(rtspUrl));
    }

}
