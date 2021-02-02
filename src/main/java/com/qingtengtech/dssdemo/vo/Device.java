package com.qingtengtech.dssdemo.vo;

import lombok.Data;

import java.util.List;

/**
 * 〈功能简述〉<br>
 * 〈〉
 * 设备信息实体
 * @author Moonlight
 * @date 2020/11/18 17:02
 */
@Data
public class Device {
    /** 设备编码 **/
    private String code;
    /** 设备名称 **/
    private String name;
    /** 设备大类 **/
    private String category;
    /** 设备类型,详情见附录 **/
    private String type;
    /** 设备型号 **/
    private String model;
    /** 设备状态 **/
    private String status;
    /** 离线原因 **/
    private String offlineReason;
    /** 协议 **/
    private String protocol;
    /** 厂商,详情见附录 **/
    private String manufacturer;
    /** 设备IP **/
    private String deviceIp;
    /** 设备端口 **/
    private String devicePort;
    /** 主动注册ID **/
    private String registId;
    /** 代理IP **/
    private String proxyIp;
    /** 代理端口 **/
    private String proxyPort;
    /** 用户名 **/
    private String userName;
    /** 密码 **/
    private String password;
    /** SIP号码 **/
    private String callNumber;
    /** 登录类型 **/
    private String loginType;
    /** SN编号 **/
    private String sn;
    /** 组织编码 **/
    private String orgCode;
    /** 单元信息 **/
    private List<Unit> units;
    /** 对讲设备单元使能 **/
    private String unitEnable;
    /** 对讲设备幢使能 **/
    private String buildingEnable;
    /** 软件版本 **/
    private String softwareVersion;
    /** 硬件版本 **/
    private String hardwareVersion;
    /** 域ID **/
    private String domainId;
    /** 修改时间 **/
    private String modifyTime;

    @Data
    static class Unit {
        /** 单元类型,详情见附录 **/
        private String unitType;
        /** 单元序号 **/
        private String unitSeq;
        /** 辅码流 **/
        private String assistStream;
        /** 零通道编码 **/
        private String zeroChnEncode;
        /** 存储服务ID **/
        private String ssServiceId;
        /** PTS服务ID **/
        private String ptsServiceId;
        /** 码流类型 **/
        private String streamType;
        /** 解码模式 **/
        private String decodeMode;
        /** 流处理模式 **/
        private String streamMode;
        /** 电视墙融合状态：0 = 不支持融合，1 = 支持融合 **/
        private String conbineStatus;
        /** 第三方控制 **/
        private String thirdPartyControl;
        /** 语音服务地址 **/
        private String voiceServerIp;
        /** 语音服务端口 **/
        private String voiceServerPort;
        /** 语音状态端口 **/
        private String voiceStatusPort;
        /** 语音客户端地址 **/
        private String voiceClientIp;
        /** 动环资源编码 **/
        private String dynCode;
        /** 动环资源名称 **/
        private String dynName;
        /** 动环资源类型 **/
        private String dynType;
        /** 是否支持指纹鉴权: 0-未知，兼容以前 默认；1- 不支持；2- 支持 **/
        private String fingerPrintAuth;
        /** 是否支持卡片鉴权:1-是 0-否 **/
        private String cardAuth;
        /** 是否支持人脸识别鉴权:1-是 0-否 **/
        private String faceAuth;
        /** 是否人卡分离:1-是 0-否 **/
        private String userIsolate;
        /** 开门方式组合 **/
        private String unlockModes;
        /** 通道信息 **/
        private List<Channel> channels;

        @Data
        static class Channel {
            /** 通道编码 **/
            private String channelCode;
            /** 通道名称 **/
            private String channelName;
            /** 通道序号 **/
            private String channelSeq;
            /** 启用状态 **/
            private String status;
            /** SN编号 **/
            private String sn;
            /** GPS(X) **/
            private String gpsX;
            /** GPS(Y) **/
            private String gpsY;
            /** 光栅图ID **/
            private String mapId;
            /** 摄像头类型 **/
            private String cameraType;
            /** 通道类型,详情见附录 **/
            private String channelType;
            /** 远程类型 **/
            private String remoteType;
            /** 通道功能集 **/
            private String cameraFunctions;
            /** 多播IP **/
            private String multicastIp;
            /** 多播端口 **/
            private String multicastPort;
            /** IPC(IP) **/
            private String ipcIp;
            /** 录像类型 **/
            private String recordType;
            /** 是否用于客流统计 **/
            private String forPeopleCount;
            /** 电视墙最大分割数 **/
            private String maxSplitNum;
            /** 接口类型 **/
            private String interfaceType;
            /** 报警类型，详情见附录 **/
            private String alarmType;
            /** 报警等级 **/
            private String alarmLevel;
            /** 信号类型 **/
            private String signalType;
            /** 门禁类型 **/
            private String accessType;
            /** pos类型 **/
            private String posType;
            /** 文档上写的是：门禁类型   但是根据字段名称来看应该叫做：pos模式 **/
            private String posModel;
            /** 动环资源 **/
            private String dynCode;
            /** 人像功能 **/
            private String faceFunctions;
            /** 键盘码 **/
            private String keyCode;
            /** 地标码 **/
            private String db33Code;
            /** 智能状态 **/
            private String intelliState;
            /** 分析类型 **/
            private String faceAnalyseType;
            /** 视频源，提供给虚拟通道使用 **/
            private String videoSource;
            /** 目标检测功能 **/
            private String targetDetection;
            /** 能力集 **/
            private String capability;
            /** 域ID **/
            private String domainId;
            /** SIP ID **/
            private String sipId;
            /** SIP密码 **/
            private String sipPwd;
        }
    }


}
