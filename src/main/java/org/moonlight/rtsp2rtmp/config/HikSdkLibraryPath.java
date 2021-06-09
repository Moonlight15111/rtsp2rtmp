package org.moonlight.rtsp2rtmp.config;

import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * 〈功能简述〉<br>
 * 〈〉
 * HikVision - 海康Dll依赖路径配置
 * @author Moonlight
 * @date 2021/2/24 15:57
 */
@Configuration
public class HikSdkLibraryPath implements EnvironmentAware {

    public static String HIKVISION_DLL_PATH;

    @Override
    public void setEnvironment(Environment environment) {
        HIKVISION_DLL_PATH = environment.getProperty("app.hikvision-dll-path");
    }

}