package org.moonlight.rtsp2rtmp.vo.dss;

import com.alibaba.fastjson.JSONObject;
import org.moonlight.rtsp2rtmp.common.Constant;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

/**
 * 〈功能简述〉<br>
 * 〈〉
 * DSS - rtsp视频流地址查询参数封装实体
 * @author Moonlight
 * @date 2020/11/18 18:46
 */
@Getter
@Setter
@Accessors(chain = true)
public class RtspStreamUrlQueryVO {
    /** 电脑名称 **/
    private String clientType;
    /** 电脑MAC地址 **/
    private String clientMac;
    /** 未知 - 可不填**/
    private String clientPushId;
    /** 可不填 **/
    private String project;
    /** 方法名 **/
    private String method;
    /** Json串 **/
    private Data data;

    public String paramCheck() {
        String res = "";
        if (data.getStreamType() == null || !Constant.NUMBER_PATTERN.matcher(data.getStreamType()).matches()) {
            res += "streamType不能为空或非数字;";
        }
        if (data.getDataType() == null || !Constant.NUMBER_PATTERN.matcher(data.getDataType()).matches()) {
            res += "dataType不能为空或非数字;";
        }
        if (StringUtils.isBlank(data.getChannelId())) {
            res += "channelId不能为空或空白字符串";
        }
        return res;
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("clientType", clientType);
        jsonObject.put("clientMac", clientMac);
        jsonObject.put("clientPushId", clientPushId == null ? "" : clientPushId);
        jsonObject.put("project", project == null ? "" : project);
        jsonObject.put("method", method);
        jsonObject.put("data", data);
        return jsonObject.toJSONString();
    }

    @Getter
    @Setter
    @ToString
    @Accessors(chain = true)
    public static class Data {
        /** 码流类型：1=主码流, 2=辅码流 **/
        private String streamType;
        /** URI信息 **/
        private String optional;
        /** 轨道ID **/
        private String trackId;
        /** 扩展数据 **/
        private String extend;
        /** 视频通道编码 **/
        private String channelId;
        /** 录像计划ID **/
        private String planId;
        /** 视频类型：1=视频, 2=音频, 3=音视频 **/
        private String dataType;
    }

}
