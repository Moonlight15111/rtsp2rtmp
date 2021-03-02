package org.moonlight.rtsp2rtmp.hikvision;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import lombok.extern.slf4j.Slf4j;
import org.moonlight.rtsp2rtmp.vo.Channel;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈功能简述〉<br>
 * 〈〉
 * HikVision - 登录/注册 设备，获取设备的通道，并拼接每个通道的rtsp主码流地址，rtsp辅码流地址
 *
 * HikVision官方Demo见本项目 HikVision / 海康威视开发包_win64_java.zip
 * HikVision官方地址：https://www.hikvision.com/cn/download_more_570.html
 * HikVision错误码(last error)参考: https://blog.csdn.net/qq_36051316/article/details/85259713  https://blog.csdn.net/p942005405/article/details/106101029/
 *
 * @author Moonlight
 * @date 2021/2/19 17:13
 */
@Slf4j
public class HikVisionHandler implements AutoCloseable {

    private HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;

    /**
     * 设备信息
     **/
    private HCNetSDK.NET_DVR_DEVICEINFO_V30 mStrDeviceInfo;
    /**
     * 用户句柄
     **/
    private NativeLong lUserID;

    private String username;
    private String password;
    private String ip;
    private String devicePort;

    private String rtspMainUrlTmplate;
    private String rtspSubUrlTmplate;

    public HikVisionHandler(String username, String password, String ip, String devicePort, String rtspMainUrlTmplate, String rtspSubUrlTmplate) {
        // 初始化失败
        if (!hCNetSDK.NET_DVR_Init()) {
            log.error("海康SDK初始化失败,init res [false],get last error[{}]", hCNetSDK.NET_DVR_GetLastError());
            throw new RuntimeException("海康SDK初始化失败");
        }
        lUserID = new NativeLong(-1);
        this.username = username;
        this.password = password;
        this.ip = ip;
        this.devicePort = devicePort == null ? "8000" : devicePort;
        this.rtspMainUrlTmplate = rtspMainUrlTmplate.replace("USERNAME", username).replace("PASSWORD", password).replace("IP", ip);
        this.rtspSubUrlTmplate = rtspSubUrlTmplate.replace("USERNAME", username).replace("PASSWORD", password).replace("IP", ip);
    }

    /**
     * 设备注册
     */
    public int deviceRegist() {
        log.info("设备ip[{}]准备登录", ip);
        // 先注销,在登录
        if (lUserID.longValue() > -1) {
            hCNetSDK.NET_DVR_Logout_V30(lUserID);
            lUserID = new NativeLong(-1);
        }
        // 注册(既登录设备)开始

        // 获取设备参数结构
        mStrDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V30();
        // 登录设备
        lUserID = hCNetSDK.NET_DVR_Login_V30(ip, (short) Integer.parseInt(devicePort), username, password, mStrDeviceInfo);
        long userID = lUserID.longValue();
        if (userID == -1) {
            log.error("设备ip[{}]登录结果[{}],get last error[{}]", ip, lUserID, hCNetSDK.NET_DVR_GetLastError());
            // "注册失败";
            return 3;
        }
        return 0;
    }

    /**
     * 获取设备通道
     */
    public List<Channel> getChannels() {
        List<Channel> channels = new ArrayList<>();

        // 获取IP接入配置参数
        IntByReference ibrBytesReturned = new IntByReference(0);
        boolean bRet;

        /* IP参数 */
        HCNetSDK.NET_DVR_IPPARACFG mStrIpParaCfg = new HCNetSDK.NET_DVR_IPPARACFG();
        mStrIpParaCfg.write();
        Pointer lpIpParaConfig = mStrIpParaCfg.getPointer();
        bRet = hCNetSDK.NET_DVR_GetDVRConfig(lUserID, HCNetSDK.NET_DVR_GET_IPPARACFG, new NativeLong(0),
                lpIpParaConfig, mStrIpParaCfg.size(), ibrBytesReturned);
        mStrIpParaCfg.read();

        log.info("设备ip[{}] mStrDeviceInfo[{}] bRet[{}]", ip, mStrDeviceInfo, bRet);

        int channelNum;
        if (!bRet) {
            // 设备不支持,则表示没有IP通道
            for (int iChanNum = 0; iChanNum < mStrDeviceInfo.byChanNum; iChanNum++) {
                channelNum = iChanNum + mStrDeviceInfo.byStartChan;

                channels.add(new Channel().setChannelNum(iChanNum + mStrDeviceInfo.byStartChan)
                        .setRtspMainUrl(rtspMainUrlTmplate.replace("CHANNELNUM", "" + channelNum))
                        .setRtspSubUrl(rtspSubUrlTmplate.replace("CHANNELNUM", "" + channelNum)));
            }
        } else {
            for (int iChanNum = 0; iChanNum < HCNetSDK.MAX_IP_CHANNEL; iChanNum++) {
                if (mStrIpParaCfg.struIPChanInfo[iChanNum].byEnable == 1) {
                    // IP通道号要加32
                    channelNum = iChanNum + mStrDeviceInfo.byStartChan + 32;

                    channels.add(new Channel().setChannelNum(iChanNum + mStrDeviceInfo.byStartChan)
                            .setRtspMainUrl(rtspMainUrlTmplate.replace("CHANNELNUM", "" + channelNum))
                            .setRtspSubUrl(rtspSubUrlTmplate.replace("CHANNELNUM", "" + channelNum)));
                }
            }
        }

        return channels;
    }

    @Override
    public void close() throws Exception {
        //如果已经注册,注销
        if (lUserID.longValue() > -1) {
            hCNetSDK.NET_DVR_Logout_V30(lUserID);
        }
        hCNetSDK.NET_DVR_Cleanup();
    }
}
