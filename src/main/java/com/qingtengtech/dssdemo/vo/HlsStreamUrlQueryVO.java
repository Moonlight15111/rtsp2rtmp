package com.qingtengtech.dssdemo.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 〈功能简述〉<br>
 * 〈〉
 *
 * @author Moonlight
 * @date 2020/11/18 18:46
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class HlsStreamUrlQueryVO {
    /** 设备编号 **/
    private String devicecode;
    /** 单元序号，因为都是编码通道，填1即可 **/
    private String unitSeq;
    /** 通道序号 **/
    private String chnSeq;
    /** 录像来源 2-设备，3-中心 **/
    private String recordSource;
    /** 录像类型 0-全部 1-普通录像 2-报警录像 当recordSource为3时，recordType的值要填0 **/
    private String recordType;
    /** 码流类型 1-主码流，2-辅码流 **/
    private String streamType;
    /** 录像开始时间 格式：yyyy-MM-dd hh:mm:ss **/
    private String beginTime;
    /** 录像结束时间 格式：yyyy-MM-dd hh:mm:ss **/
    private String endTime;
}
