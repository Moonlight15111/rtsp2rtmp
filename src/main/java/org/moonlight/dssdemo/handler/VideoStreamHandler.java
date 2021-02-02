package org.moonlight.dssdemo.handler;

import com.alibaba.fastjson.JSONObject;
import org.moonlight.dssdemo.common.Constant;
import org.moonlight.dssdemo.util.HttpUtil;
import org.moonlight.dssdemo.vo.Device;
import org.moonlight.dssdemo.vo.HlsStreamUrlQueryVO;
import org.moonlight.dssdemo.vo.RtspStreamUrlQueryVO;
import org.moonlight.dssdemo.vo.StreamUrlInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 〈功能简述〉<br>
 * 〈〉
 *
 * @author Moonlight
 * @date 2020/11/18 14:13
 */
@Slf4j
@Component(value = "videoStreamHandler")
public class VideoStreamHandler {

    private static final HlsStreamHandler HLS_STREAM_HANDLER = new HlsStreamHandler();

    private static final RtspStramHandler RTSP_STREAM_HANDLER = new RtspStramHandler();

    @Value("${dss.username}")
    private String username;

    @Value("${dss.password}")
    private String password;

    /**
     * 功能描述: <br>
     * 〈〉
     * 获取token
     * @return String
     * @see TokenInfo#token
     * @since 1.0.0
     * @author Moonlight
     * @date 2020/11/23 14:57
     */
    public String getToken() {
        return HLS_STREAM_HANDLER.getToken(username, password);
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * 获取详细的token信息描述、如：token、保活时间、下次更新token的时间等
     * @return String
     * @see TokenInfo
     * @since 1.0.0
     * @author Moonlight
     * @date 2020/11/23 14:57
     */
    public String getTokenInfoToString() {
        return BaseStreamHandler.TOKEN_INFO.setCurrentTimeMillis().toString();
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * 更新token
     * @return boolean 更新token是否成功
     * @since 1.0.0
     * @author Moonlight
     * @date 2020/11/23 14:58
     */
    public boolean updateToken() {
        return HLS_STREAM_HANDLER.updateToken(username, password);
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * 获取设备信息
     * REMIND: deviceCodes、categories，可能是String数组类型，因为请求示例中是用[]表示,且都加了s表示复数？但是文档中却又说是String类型
     *
     * @param orgCode     组织节点OrgCode
     * @param deviceCodes 设备编码，如果为空，则表示获取所有设备
     * @param categories  设备大类：1.编码器；5.卡口；8.门禁等
     * @return List<Device>
     * @see Device
     * @author Moonlight
     * @date 2020/11/18 17:44
     * @since 1.0.0
     */
    public List<Device> getDevices(Integer orgCode, String[] deviceCodes, String[] categories) {
       return HLS_STREAM_HANDLER.getDevices(username, password, orgCode, deviceCodes, categories);
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * 获取HLS的实时流地址
     * @param queryVO 查询VO
     * @see HlsStreamUrlQueryVO
     * @return StreamUrlInfo 封装的流地址实体
     * @see StreamUrlInfo
     * @since 1.0.0
     * @author Moonlight
     * @date 2020/11/23 15:32
     */
    public StreamUrlInfo getHlsLiveUrl(HlsStreamUrlQueryVO queryVO) {
        List<HlsStreamUrlQueryVO> queryVOList = new ArrayList<>();
        queryVOList.add(queryVO);

        return getHlsLiveUrl(queryVOList);
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * 获取HLS的实时流地址
     * @param queryVOList 查询VO
     * @see HlsStreamUrlQueryVO
     * @return StreamUrlInfo 封装的流地址实体
     * @see StreamUrlInfo
     * @since 1.0.0
     * @author Moonlight
     * @date 2020/11/23 15:32
     */
    public StreamUrlInfo getHlsLiveUrl(List<HlsStreamUrlQueryVO> queryVOList) {
        return HLS_STREAM_HANDLER.getHlsLiveUrl(username, password, queryVOList);
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * 获取HLS的回放流地址
     * @param queryVO 查询VO
     * @see HlsStreamUrlQueryVO
     * @return StreamUrlInfo 封装的流地址实体
     * @see StreamUrlInfo
     * @since 1.0.0
     * @author Moonlight
     * @date 2020/11/23 15:37
     */
    public StreamUrlInfo getHlsRecordUrl(HlsStreamUrlQueryVO queryVO) {
        List<HlsStreamUrlQueryVO> queryVOList = new ArrayList<>();
        queryVOList.add(queryVO);

        return getHlsRecordUrl(queryVOList);
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * 获取HLS的回放流地址
     * @param queryVOList 查询VO
     * @see HlsStreamUrlQueryVO
     * @return StreamUrlInfo 封装的流地址实体
     * @see StreamUrlInfo
     * @since 1.0.0
     * @author Moonlight
     * @date 2020/11/23 15:37
     */
    public StreamUrlInfo getHlsRecordUrl(List<HlsStreamUrlQueryVO> queryVOList) {
        return HLS_STREAM_HANDLER.getHlsRecordUrl(username, password, queryVOList);
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * 获取Rtsp的实时流地址
     * @param queryVO 查询VO
     * @see RtspStreamUrlQueryVO
     * @return StreamUrlInfo 封装的流地址实体
     * @see StreamUrlInfo
     * @since 1.0.0
     * @author Moonlight
     * @date 2020/11/23 15:43
     */
    public StreamUrlInfo getRtspLiveUrl(RtspStreamUrlQueryVO queryVO) {
        return RTSP_STREAM_HANDLER.getRtspLiveUrl(username, password, queryVO);
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * 获取Rtsp的回放流地址
     * @param queryVO 查询VO
     * @see RtspStreamUrlQueryVO
     * @return StreamUrlInfo 封装的流地址实体
     * @see StreamUrlInfo
     * @since 1.0.0
     * @author Moonlight
     * @date 2020/11/23 15:43
     */
    public StreamUrlInfo getRtspRecordUrl(RtspStreamUrlQueryVO queryVO) {
        return RTSP_STREAM_HANDLER.getRtspRecordUrl(username, password, queryVO);
    }

    @Data
    private static class TokenInfo {
        /**
         * token
         **/
        private String token;
        /**
         * token的保活持续时间
         * remind: 单位未知
         **/
        private Integer duration;
        /**
         * 下次更新token的毫秒数
         * 根据文档保活间隔建议为保活持续时间的3/4
         * remind: 即 nextUpdateTokenTimeMillis = 当前毫秒数 + (duration * 3/4) 这只是粗略地计算方式实际上要考虑 duration 的单位
         **/
        private Long nextUpdateTokenTimeMillis;
        /**
         * token的存活时间
         */
        private Long tokenLiveTimeMillis;
        /**
         * 获取到token的时间戳
         */
        private Long obtainedTimeMillis;
        /**
         * 当前的时间戳
         */
        private Long currentTimeMillis;

        TokenInfo setCurrentTimeMillis() {
            this.currentTimeMillis = System.currentTimeMillis();
            return this;
        }
        /**
         * 用户ID
         **/
        private String userId;
        /**
         * 版本信息
         **/
        private VersionInfo versionInfo;
        /**
         * 未知信息
         **/
        private String sipNum;
        /**
         * 签名 - REMIND：注意，此处的签名取的是二阶段鉴权时 第四次 的计算结果。详见 calculationAndSetSignature 方法。这个字段目前来说,只用于更新token
         **/
        private String signature;

        @Data
       private static class VersionInfo {
            private String lastVersion;
            private String updateUrl;

            VersionInfo() {}

            VersionInfo(JSONObject jsonObject) {
                this.lastVersion = jsonObject.getString("lastVersion");
                this.updateUrl = jsonObject.getString("updateUrl");
            }
        }
    }

    private static class BaseStreamHandler {

        private static final Lock LOCK = new ReentrantLock();

        static final TokenInfo TOKEN_INFO = new TokenInfo();

        String getToken(String userName, String password) {
            if (!checkToken(userName, password, false)) {
                String res = "检测token时不通过, 无法获取Token";
                log.warn(res);
                return res;
            }
            return TOKEN_INFO.getToken();
        }

        /**
         * 功能描述: <br>
         * 〈〉
         * 获取设备信息
         *
         * @param orgCode     组织节点OrgCode
         * @param deviceCodes 设备编码，如果为空，则表示获取所有设备
         * @param categories  设备大类：1.编码器；5.卡口；8.门禁等
         * @return List<Device>
         * @author Moonlight
         * @date 2020/11/18 17:44
         * @since 1.0.0
         */
        List<Device> getDevices(String userName, String password, Integer orgCode, String[] deviceCodes, String[] categories) {
            if (!checkToken(userName, password, false)) {
                log.warn("检测token时不通过, 无法获取设备信息");
                return null;
            }
            String devicesParams = getDevicesParams(orgCode, deviceCodes, categories);
            try {
                JSONObject devicesJsonObj = HttpUtil.doPost(Constant.DEFAULT_GET_DEVICES_URL + "?token=" + TOKEN_INFO.getToken(), devicesParams);

                if (checkResult(devicesJsonObj, userName, password)) {
                    devicesJsonObj = HttpUtil.doPost(Constant.DEFAULT_GET_DEVICES_URL + "?token=" + TOKEN_INFO.getToken(), devicesParams);
                }

                log.info("根据参数[{}]获取到设备信息的返回值[{}]", devicesParams, devicesJsonObj);
                if (!devicesJsonObj.containsKey("data") || !devicesJsonObj.getJSONObject("data").containsKey("devices")) {
                    log.error("使用用户名[{}]userId[{}]token[{}]参数[{}]去获取设备信息时,返回值[{}]出现异常,缺失必要的数据项data或data中的devices",
                            userName, TOKEN_INFO.getUserId(), TOKEN_INFO.getToken(), devicesParams, devicesJsonObj);
                    return null;
                }

                return devicesJsonObj.getJSONObject("data").getJSONArray("devices").toJavaList(Device.class);
            } catch (IOException | RuntimeException e) {
                log.error("使用用户名[{}]userId[{}]token[{}]参数[{}]去获取设备信息时出错", userName, TOKEN_INFO.getUserId(), TOKEN_INFO.getToken(), devicesParams, e);
            }
            return null;
        }

        /**
         * 功能描述: <br>
         * 〈〉
         * 获取设备信息的参数组装   全部不给参数，好像是获取所有的device
         * @param orgCode 组织节点OrgCode
         * @param deviceCodes 设备编码，如果为空，则表示获取所有设备
         * @param categories 设备大类：1.编码器；5.卡口；8.门禁等
         * @return String
         * @since 1.0.0
         * @author Moonlight
         * @date 2020/11/23 15:55
         */
        private String getDevicesParams(Integer orgCode, String[] deviceCodes, String[] categories) {
            JSONObject params = new JSONObject();
            params.put("orgCode", orgCode);
            params.put("deviceCodes", deviceCodes);
            params.put("categories", categories);

            return params.toString();
        }

        /**
         * 功能描述: <br>
         * 〈〉
         * 判断是否需要更新token
         * @param result DSS系统的返回结果
         * @param userName 登录用户名
         * @return boolean 是否需要更新token 或 更新token是否成功
         * @since 1.0.0
         * @author Moonlight
         * @date 2020/11/23 15:56
         */
        boolean checkResult(JSONObject result, String userName, String password) {
            return result.containsKey("randomKey") && result.containsKey("publickey") && checkToken(userName, password, true);
        }

        /**
         * 功能描述: <br>
         * 〈〉
         * 检查token
         * @param userName 用户名
         * @param password 登录密码
         * @param compelUpdateToken 强制刷新
         * @return boolean token是否有效
         * @since 1.0.0
         * @author Moonlight
         * @date 2020/11/18 18:06
         */
        boolean checkToken(String userName, String password, boolean compelUpdateToken) {
            LOCK.lock();
            try {
                // 之前没有进行过登录 或 token需要更新
                if (StringUtils.isBlank(TOKEN_INFO.getToken()) || TOKEN_INFO.getNextUpdateTokenTimeMillis() == null) {
                    log.warn("没有token准备登录获取Token");
                    return authorize(userName, password);
                } else if (System.currentTimeMillis() >= TOKEN_INFO.getNextUpdateTokenTimeMillis() || compelUpdateToken) {
                    log.warn("准备更新Token,因为Token即将过期或需要强制更新token,是否需要强制更新Token[{}]", compelUpdateToken);
                    int retry = 5;
                    while (!updateToken(userName, password)) {
                        log.warn("更新token失败,将再尝试更新token[{}]次,如果还是不行,那么将会进行重新登录", retry);
                        if (retry-- == 0) {
                            break;
                        }
                    }
                    return retry > 0 || authorize(userName, password);
                }
                return true;
            } finally {
                LOCK.unlock();
            }
        }

        /**
         * 功能描述: <br>
         * 〈〉
         * 鉴权登录
         * @param userName 用户名
         * @param password 登录密码
         * @return boolean 是否登录成功
         * @since 1.0.0
         * @author Moonlight
         * @date 2020/11/18 18:15
         */
        private boolean authorize(String userName, String password) {
            try {
                String firstPhaseParams = authorizeFirstPhaseParams(userName);
                JSONObject firstPhase = HttpUtil.doPost(Constant.DEFAULT_AUTHORIZE_URL, firstPhaseParams);

                log.info("获取到一阶段鉴权返回值[{}]", firstPhase);
                if (!firstPhase.containsKey("realm") || !firstPhase.containsKey("randomKey") || !firstPhase.containsKey("encryptType")) {
                    log.error("尝试使用用户名[{}]进行DSS登录鉴权时,一阶段返回值[{}]中缺少realm/randomKey/encryptType中的任意一种数据,请求参数为[{}]", userName, firstPhase, firstPhaseParams);
                    return false;
                }

                String secondPhaseParams = authorizeSecondPhaseParams(userName, password, firstPhase);
                JSONObject secondPhase = HttpUtil.doPost(Constant.DEFAULT_AUTHORIZE_URL, secondPhaseParams);

                log.info("获取到二阶段鉴权返回值[{}]", secondPhase);
                if (!secondPhase.containsKey("token") || !secondPhase.containsKey("duration")) {
                    log.error("尝试使用用户名[{}]进行DSS登录鉴权时,二阶段返回值[{}]中缺少token/duration中的任意一种数据,请求参数为[{}]", userName, secondPhase, secondPhaseParams);
                    return false;
                }

                parseAuthorizeResult(secondPhase);

                log.info("使用用户名[{}]登录DSS成功,userId为[{}]token为[{}]duration[{}]当前时间戳为[{}]下次更新token的时间戳为[{}]", userName, TOKEN_INFO.getUserId(), TOKEN_INFO.getToken(),
                        TOKEN_INFO.getDuration(), System.currentTimeMillis(), TOKEN_INFO.getNextUpdateTokenTimeMillis());
            } catch (IOException e) {
                log.error("使用用户名[{}]进行DSS登录鉴权时出现异常", userName, e);
                return false;
            }
            return true;
        }

        /**
         * 功能描述: <br>
         * 〈〉
         * 解析鉴权成功后的返回值，获取token、保活持续时间、userId、sipNum、versionInfo信息并计算下次应该在何时更新token
         *
         * @param authorizeSuccessResult 鉴权成功后的返回值
         * @author Moonlight
         * @date 2020/11/18 15:50
         * @since 1.0.0
         */
        private void parseAuthorizeResult(JSONObject authorizeSuccessResult) {
            TOKEN_INFO.setToken(authorizeSuccessResult.getString("token"));
            TOKEN_INFO.setDuration(authorizeSuccessResult.getInteger("duration"));
            TOKEN_INFO.setUserId(authorizeSuccessResult.getString("userId"));
            TOKEN_INFO.setSipNum(authorizeSuccessResult.getString("sipNum"));
            TOKEN_INFO.setVersionInfo(new TokenInfo.VersionInfo(authorizeSuccessResult.getJSONObject("versionInfo")));

            calculationTokenTimeMillis();
        }

        private void calculationTokenTimeMillis() {
            // remind: 先按分钟换算，可能也不是按分钟算的
            // change: 按秒换算试试   moonlight 20201123
            long tokenLiveTimeMillis = ((TOKEN_INFO.getDuration() * 1000) / 4) * 3;
            long currentTimeMillis = System.currentTimeMillis();
            TOKEN_INFO.setNextUpdateTokenTimeMillis( currentTimeMillis + tokenLiveTimeMillis);
            TOKEN_INFO.setObtainedTimeMillis(currentTimeMillis);
            TOKEN_INFO.setTokenLiveTimeMillis(tokenLiveTimeMillis);
        }

        /**
         * 功能描述: <br>
         * 〈〉
         * DSS系统一阶段鉴权参数组装
         *
         * @param userName 用户名
         * @return 一阶段鉴权参数
         * @author Moonlight
         * @date 2020/11/18 15:48
         * @since 1.0.0
         */
        private String authorizeFirstPhaseParams(String userName) {
            JSONObject params = new JSONObject();
            params.put("userName", userName);
            params.put("ipAddress", "");
            params.put("clientType", "WINPC");
            return params.toString();
        }

        /**
         * 功能描述: <br>
         * 〈〉
         * DSS系统二阶段鉴权参数组装
         *
         * @param userName         用户名
         * @param password         登录密码
         * @param firstPhaseResult 一阶段鉴权成功后的返回值，应该包含realm、randomKey、encryptType三种数据
         * @return 二阶段鉴权参数
         * @author Moonlight
         * @date 2020/11/18 15:48
         * @since 1.0.0
         */
        private String authorizeSecondPhaseParams(String userName, String password, JSONObject firstPhaseResult) {
            JSONObject params = new JSONObject();

            params.put("userName", userName);
            params.put("randomKey", firstPhaseResult.getString("randomKey"));
            params.put("mac", "");
            params.put("encryptType", firstPhaseResult.getString("encryptType"));
            params.put("ipAddress", "");
            params.put("signature", calculationAndSetSignature(password, userName, firstPhaseResult.getString("realm"), firstPhaseResult.getString("randomKey")));
            params.put("clientType", "WINPC");

            return params.toString();
        }

        /**
         * 功能描述: <br>
         * 〈〉
         * 计算二阶段鉴权需要的签名 并保存第四次计算出来的签名到tokenInfo中用于后续的token更新
         * @param password 登录密码 - 来自于配置文件配置
         * @param userName 登录用户名 - 来自于配置文件配置
         * @param realm 权限作用域 - 来自于一阶段鉴权成功后的返回值
         * @param randomKey 随机秘钥- 来自于一阶段鉴权成功后的返回值
         * @return String 返回的是第五次签名计算结果
         * @since 1.0.0
         * @author Moonlight
         * @date 2020/11/23 15:50
         */
        private String calculationAndSetSignature(String password, String userName, String realm, String randomKey) {
            String signature = DigestUtils.md5DigestAsHex(password.getBytes());
            signature = DigestUtils.md5DigestAsHex((userName + signature).getBytes());
            signature = DigestUtils.md5DigestAsHex(signature.getBytes());
            signature = DigestUtils.md5DigestAsHex((userName + ":" + realm + ":" + signature).getBytes());

            TOKEN_INFO.setSignature(signature);
            log.info("calculationAndSetSignature token signature [{}]", signature);

            signature = DigestUtils.md5DigestAsHex((signature + ":" + randomKey).getBytes());

            return signature;
        }

        /**
         * 功能描述: <br>
         * 〈〉
         * 更新token
         * @param userName 用户名
         * @return boolean 更新token是否成功
         * @since 1.0.0
         * @author Moonlight
         * @date 2020/11/18 18:34
         */
        boolean updateToken(String userName, String password) {
            String updateTokenParams = updateTokenParams();
            try {
                JSONObject updateTokenResult = HttpUtil.doPost(Constant.DEFAULT_UPDATE_TOKEN + "?token=" + TOKEN_INFO.getToken(), updateTokenParams);

                log.info("获取到更新Token的返回值[{}]参数为[{}]", updateTokenResult, updateTokenParams);

                if (updateTokenResult.containsKey("randomKey") && updateTokenResult.containsKey("publickey")) {
                    log.warn("更新token时失败，本次登录可能已经失效，将尝试重新登录。");
                    return authorize(userName, password);
                }

                if (!updateTokenResult.containsKey("data") || !updateTokenResult.getJSONObject("data").containsKey("token") || !updateTokenResult.getJSONObject("data").containsKey("duration")) {
                    log.error("当前时间戳[{}]下次更新token时间戳[{}].更新userName[{}]userId[{}]的token[{}]时发生异常,返回值[{}]中缺少token/duration中的任意一种数据,请求参数为[{}]",
                            System.currentTimeMillis(), TOKEN_INFO.getNextUpdateTokenTimeMillis(), userName, TOKEN_INFO.getUserId(), TOKEN_INFO.getToken(), updateTokenResult, updateTokenParams);
                    return false;
                }

                TOKEN_INFO.setToken(updateTokenResult.getJSONObject("data").getString("token"));
                TOKEN_INFO.setDuration(updateTokenResult.getJSONObject("data").getInteger("duration"));
                calculationTokenTimeMillis();

                log.info("用户名[{}]userId[{}]更新token成功.新的token为[{}]duration为[{}]当前时间戳为[{}]下次更新token的时间戳为[{}]", userName, TOKEN_INFO.getUserId(), TOKEN_INFO.getToken(),
                        TOKEN_INFO.getDuration(), System.currentTimeMillis(), TOKEN_INFO.getNextUpdateTokenTimeMillis());
            } catch (IOException e) {
                log.error("更新用户名[{}]userId[{}]的token[{}]时发生错误,当前时间戳[{}]下次更新token时间戳[{}].参数为[{}]", userName, TOKEN_INFO.getUserId(),
                        TOKEN_INFO.getToken(), System.currentTimeMillis(), TOKEN_INFO.getNextUpdateTokenTimeMillis(), updateTokenParams, e);
                return false;
            }
            return true;
        }
        /**
         * 功能描述: <br>
         * 〈〉
         * Token更新的参数组装
         * @return String
         * @since 1.0.0
         * @author Moonlight
         * @date 2020/11/23 15:54
         */
        private String updateTokenParams() {
            JSONObject params = new JSONObject();

            log.info("updateTokenParams token signature [{}]", TOKEN_INFO.getSignature());

            params.put("signature", DigestUtils.md5DigestAsHex((TOKEN_INFO.getSignature() + ":" + TOKEN_INFO.getToken()).getBytes()));
            return params.toString();
        }

    }

    private static class HlsStreamHandler extends BaseStreamHandler {

        StreamUrlInfo getHlsLiveUrl(String userName, String password, List<HlsStreamUrlQueryVO> queryVOList) {
            if (!checkToken(userName, password, false)) {
                log.warn("检测token时不通过, 无法获取HLS实时流地址信息");
                return null;
            }

            return getUrl(userName, password, queryVOList, Constant.DEFAULT_HLS_GET_LIVE_URL + "?token=" + TOKEN_INFO.getToken(), "实时流");
        }

        StreamUrlInfo getHlsRecordUrl(String userName, String password, List<HlsStreamUrlQueryVO> queryVOList) {
            if (!checkToken(userName, password, false)) {
                log.warn("检测token时不通过, 无法获取HLS回放流地址信息");
                return null;
            }
            return getUrl(userName, password, queryVOList, Constant.DEFAULT_HLS_GET_RECORD_URL + "?token=" + TOKEN_INFO.getToken(), "回放流");
        }

        private StreamUrlInfo getUrl(String userName, String password, List<HlsStreamUrlQueryVO> queryVOList, String url, String type) {
            JSONObject params = new JSONObject();
            params.put("hlsBeanXoList", queryVOList);
            try {
                JSONObject data = new JSONObject();
                data.put("data", params);
                JSONObject getHlsUrls = HttpUtil.doPost(url, data.toString());

                if (checkResult(getHlsUrls, userName, password)) {
                    getHlsUrls = HttpUtil.doPost(url, data.toString());
                }

                log.info("获取到HLS[{}]的返回值[{}]参数为[{}]", type, getHlsUrls, data.toString());
                if (!getHlsUrls.containsKey("data") || !getHlsUrls.getJSONObject("data").containsKey("urls")) {
                    log.error("使用用户名[{}]userId[{}]token[{}]参数[{}]去获取[{}]地址信息时,返回值[{}]出现异常,缺失必要的数据项data",
                            userName, TOKEN_INFO.getUserId(), TOKEN_INFO.getToken(), params.toString(), type, getHlsUrls);
                    return null;
                }

                List<String> list = getHlsUrls.getJSONObject("data").getJSONArray("urls").toJavaList(String.class);
                StreamUrlInfo streamUrlInfo = new StreamUrlInfo().setHlsUrls(list);

                log.info("HLS[{}] getUrl result: streamUrlInfo[{}]", type, streamUrlInfo.toString());

                return streamUrlInfo;
            } catch (IOException e) {
                log.error("使用用户名[{}]userId[{}]token[{}]参数[{}]去获取[{}]地址信息时出错", userName, TOKEN_INFO.getUserId(), TOKEN_INFO.getToken(), params.toString(), type, e);
            }
            return null;
        }
    }

    private static class RtspStramHandler extends BaseStreamHandler {

        StreamUrlInfo getRtspLiveUrl(String userName, String password, RtspStreamUrlQueryVO queryVO) {
            String paramCheck = queryVO.paramCheck();
            if (paramCheck.length() > 1) {
                return new StreamUrlInfo().setCode(1007).setDesc("参数有误." + paramCheck);
            }
            if (!checkToken(userName, password, false)) {
                log.warn("检测token时不通过, 无法获取RTSP实时流地址信息");
                return null;
            }
            return getUrl(userName, password, queryVO, Constant.DEFAULT_RTSP_GET_LIVE_URL + "?token=" + TOKEN_INFO.getToken(), "实时流");
        }

        StreamUrlInfo getRtspRecordUrl(String userName, String password, RtspStreamUrlQueryVO queryVO) {
            String paramCheck = queryVO.paramCheck();
            if (paramCheck.length() > 1) {
                return new StreamUrlInfo().setCode(1007).setDesc("参数有误." + paramCheck);
            }
            if (!checkToken(userName, password, false)) {
                log.warn("检测token时不通过, 无法获取RTSP回放流地址信息");
                return null;
            }
            return getUrl(userName, password, queryVO, Constant.DEFAULT_RTSP_GET_RECORD_URL + "?token=" + TOKEN_INFO.getToken(), "回放流");
        }

        private StreamUrlInfo getUrl(String userName, String password, RtspStreamUrlQueryVO queryVO, String url, String type) {
            try {
                JSONObject getRtspUrls = HttpUtil.doPost(url, queryVO.toString());

                if (checkResult(getRtspUrls, userName, password)) {
                    getRtspUrls = HttpUtil.doPost(url, queryVO.toString());
                }

                log.info("获取到RTSP[{}]的返回值[{}]参数为[{}]", type, getRtspUrls, queryVO.toString());

                if (!getRtspUrls.containsKey("data")) {
                    log.error("使用用户名[{}]userId[{}]token[{}]参数[{}]去获取[{}]地址信息时,返回值[{}]出现异常,缺失必要的数据项data",
                            userName, TOKEN_INFO.getUserId(), TOKEN_INFO.getToken(), queryVO.toString(), type, getRtspUrls);
                    return null;
                }

                StreamUrlInfo result = getRtspUrls.getObject("data", StreamUrlInfo.class);

                log.info("RTSP[{}] getUrl result: streamUrlInfo[{}]", type, result);

                return result;
            } catch (IOException e) {
                log.error("使用用户名[{}]userId[{}]token[{}]参数[{}]去获取[{}]地址信息时出错", userName, TOKEN_INFO.getUserId(), TOKEN_INFO.getToken(), queryVO.toString(), type, e);
            }
            return null;
        }
    }

}
