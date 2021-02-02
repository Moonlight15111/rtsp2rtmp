package org.moonlight.dssdemo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 〈功能简述〉<br>
 * 〈〉
 *
 * @author Moonlight
 * @date 2021/1/26 18:32
 */
@Data
@Component
@ConfigurationProperties(prefix = "dss.convert")
public class CameraConfiguration implements CameraConfigProvide {
    private String rtmpUrlPrefix;
    private String hlsUrlTmplate;
    private Integer jobLimit;
    private Integer keepAliveTime;
    private Integer imageWidth;
    private Integer imageHeight;
    private String tune;
    private Integer crf;
    private String preset;
    private String clearInactiveTaskCron;

    @Override
    public CameraConfig provide() {
        return new CameraConfig()
                .setRtmpUrlPrefix(this.rtmpUrlPrefix)
                .setHlsUrlTmplate(this.hlsUrlTmplate)
                .setJobLimit(this.jobLimit)
                .setKeepAliveTime(this.keepAliveTime)
                .setWidth(this.imageWidth)
                .setHeight(this.imageHeight)
                .setTune(this.tune)
                .setCrf(this.crf)
                .setPreset(this.preset);
    }
}
