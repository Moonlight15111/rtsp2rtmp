package org.moonlight.rtsp2rtmp.hikvision;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 〈功能简述〉<br>
 * 〈〉
 *
 * @author Moonlight
 * @date 2021/2/19 17:13
 */
@Slf4j
@Component
public class HikVisionHandler implements ApplicationRunner, DisposableBean{

    private Map<String, HikVisionClient> clientMap;

    public HikVisionHandler() {
        this.clientMap = new HashMap<>(16);
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * 缓存海康设备客户端
     * @param client 海康设备客户端
     * @since 1.0.0
     * @author Moonlight
     * @date 2021/6/15 13:14
     */
    public void put(HikVisionClient client) {
        clientMap.put(client.getId(), client);
    }

    public void right(String id, int channelNum, int rotateSecond) {
        if (clientMap.get(id) == null) {
            throw new RuntimeException("根据ID" + id + "找不到对应的设备信息");
        }
        clientMap.get(id).right(channelNum, rotateSecond);
    }

    public void left(String id, int channelNum, int rotateSecond) {
        if (clientMap.get(id) == null) {
            throw new RuntimeException("根据ID" + id + "找不到对应的设备信息");
        }
        clientMap.get(id).left(channelNum, rotateSecond);
    }

    public void up(String id, int channelNum, int rotateSecond) {
        if (clientMap.get(id) == null) {
            throw new RuntimeException("根据ID" + id + "找不到对应的设备信息");
        }
        clientMap.get(id).up(channelNum, rotateSecond);
    }

    @Override
    public void destroy() throws Exception {
        for (HikVisionClient client : clientMap.values()) {
            client.logout();
        }
        clientMap.clear();
        // 释放SDK资源
        HCNetSDK.INSTANCE.NET_DVR_Cleanup();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 初始化海康设备到缓存里面去
        put(new HikVisionClient("12345","123", "123",
                "0.0.0.0", "8000",
                "rtsp://USERNAME:PASSWORD@IP:554/h265/chCHANNELNUM/main/av_stream",
                "rtsp://USERNAME:PASSWORD@IP:554/h265/chCHANNELNUM/sub/av_stream"));
    }
}