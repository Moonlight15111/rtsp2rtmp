package org.moonlight.rtsp2rtmp.hikvision;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import org.apache.commons.lang3.StringUtils;
import org.moonlight.rtsp2rtmp.exception.HikApiException;
import org.moonlight.rtsp2rtmp.vo.hikvision.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 〈功能简述〉<br>
 * 〈海康设备客户端〉
 *
 * @author Moonlight
 * @date 2021/6/15 15:46
 */
public class HikVisionClient {

    /**
     * 因为 {@link HCNetSDK#INSTANCE} 是一个共享的对象，且可能同时存在多个 {@link HikVisionClient} 对象需要使用
     * {@link HCNetSDK#INSTANCE} 对设备进行操作. 出于线程安全性考虑，所有涉及到设备操作的地方都需要加锁
     **/
    private static final Lock GLOBAL_LOCK = new ReentrantLock();

    /** Note: 这些个方法跑起来是没啥问题，但是这个SDK是个共享对象，存在线程安全问题，缓存没屌用
     *  Note: 后面得闲了考虑升级一下JNA版本，康康能不能把这个搞成个独占的，每个调一次就产生一个新对象，或者加锁、不缓存客户端信息
     * 参考资料:
     *   1. 使用JNA 加载同一library包并创建多个不同实例: https://stackoverflow.com/questions/33040652/how-to-create-multiple-instances-of-the-same-library-with-jna
     * **/
    private HCNetSDK hCNetSDK;

    /**
     * 设备信息
     **/
    private HCNetSDK.NET_DVR_DEVICEINFO_V30 mStrDeviceInfo;
    /**
     * 用户句柄
     **/
    private NativeLong userHandle;

    private String username;
    private String password;
    private String ip;
    private String devicePort;

    private String rtspMainUrlTemplate;
    private String rtspSubUrlTemplate;

    /** 一台设备的唯一标识 - 这个必不可能为空,默认为: username:pawword:ip:port **/
    private String id;

    public HikVisionClient(String id, String username, String password, String ip,
                           String devicePort, String rtspMainUrlTemplate, String rtspSubUrlTemplate) {
        this.hCNetSDK = HCNetSDK.INSTANCE;
        this.devicePort = devicePort == null ? "8000" : devicePort;
        this.id = StringUtils.isBlank(id) ? (username + ":" + password + ":" + ip + ":" +  this.devicePort) : id;
        this.userHandle = new NativeLong(-1);
        this.username = username;
        this.password = password;
        this.ip = ip;
        this.rtspMainUrlTemplate = rtspMainUrlTemplate.replace("USERNAME", username).replace("PASSWORD", password).replace("IP", ip);
        this.rtspSubUrlTemplate = rtspSubUrlTemplate.replace("USERNAME", username).replace("PASSWORD", password).replace("IP", ip);
    }

    public String getId() {
        return this.id;
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * SDK初始化和设备注册
     *
     * 该方法包含两个部分:
     *   1. SDK 初始化, 按照海康文档的说法是进行内存预分配等动作, 但是并没有详细说明, 这个初始化的具体内容, 多次进行初始化是否会相互影响
     *      由于一些特殊原因,现在没有了测试环境,没法进行实际测试，根据之前的经验暂时先认为多次初始化不会互相影响
     *      如果实际生产中，确实会相互影响，那么可以考虑加个全局初始化标志位，来标记SDK是否初始化过，或者在加载类的时候就初始化SDK
     *
     *   2. 登录至设备
     *
     * @since 1.0.0
     * @author Moonlight
     * @date 2021/6/25 12:07
     */
    public void initAndRegist() {
        GLOBAL_LOCK.lock();
        try {
            if (userHandle.longValue() > -1) {
                return;
            }
            // 初始化失败
            if (!hCNetSDK.NET_DVR_Init()) {
                throw new HikApiException("海康SDK初始化失败", hCNetSDK.NET_DVR_GetLastError());
            }
            // 注册(既登录设备)开始
            // 获取设备参数结构
            mStrDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V30();
            // 登录设备
            userHandle = hCNetSDK.NET_DVR_Login_V30(ip, (short) Integer.parseInt(devicePort), username, password, mStrDeviceInfo);
            if (userHandle.longValue() == -1) {
                throw new HikApiException("设备登录失败", hCNetSDK.NET_DVR_GetLastError());
            }
        } finally {
            GLOBAL_LOCK.unlock();
        }
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * 注销
     *
     * 在本方法中不会对SDK进行clear up，只有在{@link HikVisionHandler}销毁时才会通过{@link HikVisionHandler#destroy()}
     * 对SDK clear up
     * @since 1.0.0
     * @author Moonlight
     * @date 2021/6/25 12:17
     */
    public void logout() {
        // 如果已经注册,注销
        if (userHandle.longValue() > -1) {
            hCNetSDK.NET_DVR_Logout_V30(userHandle);
        }
    }

    /**
     * 获取设备通道
     */
    public List<Channel> getChannels() {
        List<Channel> channels = new ArrayList<>();

        // 获取IP接入配置参数
        IntByReference ibrBytesReturned = new IntByReference(0);
        boolean ipChannel;
        /* IP参数 */
        HCNetSDK.NET_DVR_IPPARACFG mStrIpParaCfg = new HCNetSDK.NET_DVR_IPPARACFG();
        mStrIpParaCfg.write();
        Pointer lpIpParaConfig = mStrIpParaCfg.getPointer();

        GLOBAL_LOCK.lock();
        try {
            ipChannel = hCNetSDK.NET_DVR_GetDVRConfig(userHandle, HCNetSDK.NET_DVR_GET_IPPARACFG, new NativeLong(0),
                    lpIpParaConfig, mStrIpParaCfg.size(), ibrBytesReturned);
            mStrIpParaCfg.read();

            if (ipChannel) {
                processIpChannel(mStrIpParaCfg, channels);
            } else {
                // 设备不支持,则表示没有IP通道
                for (int iChanNum = 0, channelNum; iChanNum < mStrDeviceInfo.byChanNum; iChanNum++) {
                    channelNum = iChanNum + mStrDeviceInfo.byStartChan;

                    channels.add(new Channel(channelNum,
                            rtspMainUrlTemplate.replace("CHANNELNUM", "" + channelNum),
                            rtspSubUrlTemplate.replace("CHANNELNUM", "" + channelNum)));
                }
            }
        } finally {
            GLOBAL_LOCK.unlock();
        }

        return channels;
    }

    private void processIpChannel(HCNetSDK.NET_DVR_IPPARACFG mStrIpParaCfg, List<Channel> channels) {
        for (int iChanNum = 0, channelNum; iChanNum < HCNetSDK.MAX_IP_CHANNEL; iChanNum++) {
            if (mStrIpParaCfg.struIPChanInfo[iChanNum].byEnable == 1) {
                // IP通道号要加32
                channelNum = iChanNum + mStrDeviceInfo.byStartChan + 32;
                channels.add(new Channel(channelNum,
                        rtspMainUrlTemplate.replace("CHANNELNUM", "" + channelNum),
                        rtspSubUrlTemplate.replace("CHANNELNUM", "" + channelNum)));
            }
        }
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * 摄像头右转
     * @param channelNum 通道号
     * @param rotateSecond 转动秒数
     * @since 1.0.0
     * @author Moonlight
     * @date 2021/6/15 13:05
     */
    public void right(Integer channelNum, Integer rotateSecond) {
        control(new NativeLong(channelNum), HCNetSDK.PAN_RIGHT, rotateSecond, "右转");
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * 摄像头左转
     * @param channelNum 通道号
     * @param rotateSecond 转动秒数
     * @since 1.0.0
     * @author Moonlight
     * @date 2021/6/15 13:05
     */
    public void left(int channelNum, Integer rotateSecond) {
        control(new NativeLong(channelNum), HCNetSDK.PAN_LEFT, rotateSecond, "左转");
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * 摄像头上仰
     * @param channelNum 通道号
     * @param rotateSecond 转动秒数
     * @since 1.0.0
     * @author Moonlight
     * @date 2021/6/15 13:05
     */
    public void up(Integer channelNum, Integer rotateSecond) {
        control(new NativeLong(channelNum), HCNetSDK.TILT_UP, rotateSecond, "上仰");
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * 摄像头下俯
     * @param channelNum 通道号
     * @param rotateSecond 转动秒数
     * @since 1.0.0
     * @author Moonlight
     * @date 2021/6/15 13:05
     */
    public void down(Integer channelNum, Integer rotateSecond) {
        control(new NativeLong(channelNum), HCNetSDK.TILT_DOWN, rotateSecond, "上仰");
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * 摄像头焦距变大(倍率变大)
     * @param channelNum 通道号
     * @param rotateSecond 转动秒数
     * @since 1.0.0
     * @author Moonlight
     * @date 2021/6/15 13:05
     */
    public void zoomIn(Integer channelNum, Integer rotateSecond) {
        control(new NativeLong(channelNum), HCNetSDK.ZOOM_IN, rotateSecond, "焦距变大");
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * 摄像头焦距变小(倍率变小)
     * @param channelNum 通道号
     * @param rotateSecond 转动秒数
     * @since 1.0.0
     * @author Moonlight
     * @date 2021/6/15 13:05
     */
    public void zoomOut(Integer channelNum, Integer rotateSecond) {
        control(new NativeLong(channelNum), HCNetSDK.ZOOM_OUT, rotateSecond, "焦距变小");
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * 摄像头焦点前调
     * @param channelNum 通道号
     * @param rotateSecond 转动秒数
     * @since 1.0.0
     * @author Moonlight
     * @date 2021/6/15 13:05
     */
    public void focusNear(Integer channelNum, Integer rotateSecond) {
        control(new NativeLong(channelNum), HCNetSDK.FOCUS_NEAR, rotateSecond, "焦点前调");
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * 摄像头焦点后调
     * @param channelNum 通道号
     * @param rotateSecond 转动秒数
     * @since 1.0.0
     * @author Moonlight
     * @date 2021/6/15 13:05
     */
    public void focusFar(Integer channelNum, Integer rotateSecond) {
        control(new NativeLong(channelNum), HCNetSDK.FOCUS_FAR, rotateSecond, "焦点后调");
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * 控制摄像头
     *
     * 请注意:
     *    1. 通过该函数控制摄像头进行左、右、上、下、调焦等等操作后，摄像头总是会自动回归到预置点,
     *       你可以理解为回滚，除非你将摄像头更改至另一个预置点，否则它总是会回归到最近设置的预置点
     *    2. 这个控制是有延迟的，这里的延迟是指你成功发送控制指令后，在播放页面是无法实时看到摄像头做出的对应操作的，
     *       因为播放页面播放的视频是有延迟的，这个延迟由多方因素导致，比如: 推流端的编码速度、服务端的缓存、播放端的缓存、java转流需要采集、解码、解析、编码等操作
     *       目前通过各种配置，能把这个延迟压到 5 - 10 秒以内
     *
     * 海康API文档: https://open.hikvision.com/docs/d2da3584e3859815a750128dd892fc34
     *                  https://open.hikvision.com/hardware/definitions/NET_DVR_PTZControl_Other.html?_blank
     *
     * @param channelNum 通道号
     * @param command 转向命令
     * @param rotateSecond 转动秒数
     * @param type 转向的类型
     * @since 1.0.0
     * @author Moonlight
     * @date 2021/6/15 13:07
     */
    private void control(NativeLong channelNum, int command, Integer rotateSecond, String type) {
        // 海康API文档: https://open.hikvision.com/docs/d2da3584e3859815a750128dd892fc34
        GLOBAL_LOCK.lock();
        try {
            // 没有用户句柄，表示还没有登录，需要先行登录
            if (userHandle.longValue() == -1) {
                initAndRegist();
            }

            boolean start = hCNetSDK.NET_DVR_PTZControl_Other(userHandle, channelNum, command, 0);

            if (!start) {
                throw new HikApiException("摄像头" + type + "失败 ", hCNetSDK.NET_DVR_GetLastError());
            }

            rotateSecond = rotateSecond == null || rotateSecond < 1 ? 1000 : rotateSecond * 1000;
            try {
                Thread.sleep(rotateSecond);
            } catch (InterruptedException ignore) {}

            hCNetSDK.NET_DVR_PTZControl_Other(userHandle, channelNum, command, 1);
        } finally {
            GLOBAL_LOCK.unlock();
        }
    }
}
