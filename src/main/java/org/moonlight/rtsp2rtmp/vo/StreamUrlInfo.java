package org.moonlight.rtsp2rtmp.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * 〈功能简述〉<br>
 * 〈〉
 * DSS视频流地址封装实体
 * @author Moonlight
 * @date 2020/11/18 18:36
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class StreamUrlInfo implements Serializable{
    private static final long serialVersionUID = 1279230348230337002L;
    /** HLS流的地址 - 根据文档的说法，可能会有多个地址，如果有多个则用会分号隔开 **/
    private String[] hlsUrls;

    public StreamUrlInfo setHlsUrls(String hlsUrl) {
        if (StringUtils.isNotBlank(hlsUrl) && hlsUrl.contains(";")) {
            this.hlsUrls = hlsUrl.split(";");
        } else if (StringUtils.isNotBlank(hlsUrl)){
            this.hlsUrls = new String[] {hlsUrl};
        }
        return this;
    }

    public StreamUrlInfo setHlsUrls(List<String> hlsUrl) {
        this.hlsUrls = hlsUrl.toArray(new String[hlsUrl.size()]);
        return this;
    }

    /** 状态码
     * 1000: 表示成功
     * 1001: 表示失败
     * 1008: 表示获取token时发生异常。moonlight 自定义状态，并不清楚DSS是否已存在该状态码
     * 1009: 表示获取播放流地址时发生异常。moonlight自定义状态，并不清楚DSS是否已存在该状态码
     * **/
    private Integer code;
    /** 描述信息 **/
    private String desc;

    /* RTSP流 */
    /** 最低码率,要求客户具备的最低码流 **/
    private String minRate;
    /** 协议：1=CNM3, 2=RTSP **/
    private String protocol;
    /** IP地址，根据类型可能是转发服务器IP，也可能是设备IP **/
    private String ip;
    /** 端口 **/
    private String port;
    /** 是否支持STUN协议：1=支持, 0=不支持 **/
    private String stunEnable;
    /** STUN协议端口 **/
    private String stunPort;
    /** 大华协议的RTSP地址，和VDTS IP/Port是互斥关系 **/
    private String url;
    /** 标准RTSP地址，可以在vlc软件上直接播放码流 **/
    private String url2;
    /** 类型：1=转发模式，2=直连模式 **/
    private String connectType;
    /** 转发会话ID **/
    private String session;
    /** 令牌 **/
    private String token;
    /** 轨道ID **/
    private String trackId;

    /** 是否从设备拉流(缺省时从MTS拉流)：1=直接从设备拉流(如EVS)，0=从MTS平台拉流 - 回放流特有字段 **/
    private String fromDevice;
}
