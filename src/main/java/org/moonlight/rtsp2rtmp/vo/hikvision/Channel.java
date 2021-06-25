package org.moonlight.rtsp2rtmp.vo.hikvision;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 〈功能简述〉<br>
 * 〈〉
 * HikVision - 通道信息封装实体
 * @author Moonlight
 * @date 2021/2/24 17:47
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Channel implements Serializable {

    /** 通道号 **/
    private Integer channelNum;
    /** 主码流 - rtsp://[用户名]:[密码]@[ip]:[rtsp端口，默认是554]/[视频流格式(h264、h265)]/ch[通道号]/main/av_stream **/
    private String rtspMainUrl;
    /** 辅码流 - rtsp://[用户名]:[密码]@[ip]:[rtsp端口，默认是554]/[视频流格式(h264、h265)]/ch[通道号]/sub/av_stream **/
    private String rtspSubUrl;

}
