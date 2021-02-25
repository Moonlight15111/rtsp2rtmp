package org.moonlight.dssdemo.controller;


import org.apache.commons.lang3.StringUtils;
import org.moonlight.dssdemo.handler.StreamConvertHandler;
import org.moonlight.dssdemo.hikvision.HikVisionHandler;
import org.moonlight.dssdemo.vo.ReturnVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

/**
 * 〈功能简述〉<br>
 * 〈〉
 *
 * @author Moonlight
 * @date 2021/2/19 15:52
 */
@RestController
@RequestMapping("/hik")
public class HikVisionController {

    @Value("${app.rtsp-main-url-tmplate}")
    private String rtspMainUrlTmplate;
    @Value("${app.rtsp-sub-url-tmplate}")
    private String rtspSubUrlTmplate;


    private final StreamConvertHandler streamConvertHandler;

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
        try (HikVisionHandler handler = new HikVisionHandler(username, password, deviceIp, devicePort, rtspMainUrlTmplate, rtspSubUrlTmplate)) {
            if (handler.deviceRegist() != 0) {
                return ReturnVO.error(2, "设备登录失败");
            }
            return ReturnVO.ok().put("channels", handler.getChannels());
        } catch (Throwable e) {
            e.printStackTrace();
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

}
