package org.moonlight.rtsp2rtmp.controller;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.moonlight.rtsp2rtmp.config.HikSdkLibraryPath;
import org.moonlight.rtsp2rtmp.handler.StreamConvertHandler;
import org.moonlight.rtsp2rtmp.hikvision.HikVisionClient;
import org.moonlight.rtsp2rtmp.hikvision.HikVisionHandler;
import org.moonlight.rtsp2rtmp.vo.ReturnVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 〈功能简述〉<br>
 * 〈〉
 *
 * @author Moonlight
 * @date 2021/2/19 15:52
 */
@Slf4j
@RestController
@RequestMapping("/hik")
public class HikVisionController {

    @Value("${app.rtsp-main-url-tmplate}")
    private String rtspMainUrlTmplate;
    @Value("${app.rtsp-sub-url-tmplate}")
    private String rtspSubUrlTmplate;

    private final StreamConvertHandler streamConvertHandler;

    /** Note: 使用者请注意: 实际生产环境这么写的，应该拿来祭天!!! 现在为了测试先这么搞，后续再来优化海康这块的代码。生产环境中切记不能这么写！！！ **/
    private HikVisionHandler hikVisionHandler = null;

    @Autowired
    public HikVisionController(StreamConvertHandler streamConvertHandler) {
        this.streamConvertHandler = streamConvertHandler;
    }

    @GetMapping("/get/channels")
    public ReturnVO getDeviceTree(@RequestParam(name = "deviceIp", required = false) String deviceIp, // 设备IP
                                  @RequestParam(name = "devicePort", required = false) String devicePort, // 设备端口
                                  @RequestParam(name = "username", required = false) String username, // 登录用户名
                                  @RequestParam(name = "password", required = false) String password // 登录密码
    ) {
        if (StringUtils.isAnyBlank(deviceIp, username, password, devicePort)) {
            return ReturnVO.error(1, "参数错误");
        }
        try (HikVisionClient client = new HikVisionClient(null, username, password, deviceIp, devicePort, rtspMainUrlTmplate, rtspSubUrlTmplate)) {
            if (client.deviceRegist() != 0) {
                return ReturnVO.error(2, "设备登录失败");
            }
            hikVisionHandler.put(client);
            return ReturnVO.ok().put("channels", client.getChannels());
        } catch (Throwable e) {
            log.error("海康SDK路径[{}]设备ip[{}]端口[{}]获取通道时发生异常", HikSdkLibraryPath.HIKVISION_DLL_PATH, deviceIp, devicePort, e);
            return ReturnVO.error(3, e.getMessage());
        }
    }

    @GetMapping(value = "/rtsp/convert/start")
    public ReturnVO rtsp2rtmp() {
        return streamConvertHandler.rtsp2Rtmp("rtsp://127.0.0.1:8554/vlc");
    }

    @GetMapping(value = "/rtsp/convert/exit")
    public ReturnVO exit() {
        return streamConvertHandler.exitConvert("rtsp://127.0.0.1:8554/vlc");
    }

    @GetMapping(value = "/ptz/right/{id}")
    public ReturnVO right(@PathVariable(name = "id") String id) {
        try {
            hikVisionHandler.right(id, 1, 2);
            return ReturnVO.ok().put("desc", "右转 success " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
        } catch (Exception e){
            log.error("海康摄像头右转时出错", e);
            return ReturnVO.error(3, e.getMessage());
        }
    }

    @GetMapping(value = "/ptz/left/{id}")
    public ReturnVO left(@PathVariable(name = "id") String id) {
        try {
            hikVisionHandler.left(id, 1, 2);
            return ReturnVO.ok().put("desc", "左转 success " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
        } catch (Exception e){
            log.error("海康摄像头左转时出错", e);
            return ReturnVO.error(3, e.getMessage());
        }
    }

    @GetMapping(value = "/ptz/up/{id}")
    public ReturnVO up(@PathVariable(name = "id") String id) {
        try {
            hikVisionHandler.up(id, 1, 2);
            return ReturnVO.ok().put("desc", "上仰 success " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
        } catch (Exception e){
            log.error("海康摄像头上仰时出错", e);
            return ReturnVO.error(3, e.getMessage());
        }
    }

}
