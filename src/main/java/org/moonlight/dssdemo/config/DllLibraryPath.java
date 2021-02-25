package org.moonlight.dssdemo.config;

import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * 〈功能简述〉<br>
 * 〈〉
 *
 * @author Moonlight
 * @date 2021/2/24 15:57
 */
@Configuration
public class DllLibraryPath implements EnvironmentAware {

    public static String HIKVISION_DLL_PATH_PREFIX;

    @Override
    public void setEnvironment(Environment environment) {
        HIKVISION_DLL_PATH_PREFIX = environment.getProperty("app.hikvision-dll-path-prefix") + "\\";
        System.out.println("HIKVISION_DLL_PATH_PREFIX = " + HIKVISION_DLL_PATH_PREFIX);
    }

}