package org.moonlight.rtsp2rtmp.config;

/**
 * 〈功能简述〉<br>
 * 〈〉
 * 摄像头配置信息提供者，本项目中{@link CameraConfiguration}实现了该接口，实际项目中可以考虑从数据库获取
 * @author Moonlight
 * @date 2021/1/26 18:04
 */
public interface CameraConfigProvide {

    /**
     * 功能描述: <br>
     * 〈〉
     * 提供一个摄像头配置
     * @return CameraConfig
     * @since 1.0.0
     * @author Moonlight
     * @date 2021/1/26 18:04
     */
     CameraConfig provide();
}
