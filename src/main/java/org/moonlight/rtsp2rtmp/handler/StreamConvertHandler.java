package org.moonlight.rtsp2rtmp.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.moonlight.rtsp2rtmp.cache.CacheUtil;
import org.moonlight.rtsp2rtmp.config.CameraConfig;
import org.moonlight.rtsp2rtmp.config.CameraConfigProvide;
import org.moonlight.rtsp2rtmp.convert.ConvertJob;
import org.moonlight.rtsp2rtmp.util.HttpUtil;
import org.moonlight.rtsp2rtmp.vo.convert.CameraVO;
import org.moonlight.rtsp2rtmp.vo.ReturnVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 〈功能简述〉<br>
 * 〈〉
 * rtsp 转 rtmp
 * @author Moonlight
 * @date 2020/12/14 17:14
 */
@Slf4j
@Component(value = "streamConvertHandler")
public class StreamConvertHandler {
    /**
     * 自增编号 - 拼接rtmp、hls地址的编号,示例: rtmp: rtmp://NginxIP:Nginx-rtmp-server-监听端口/Nginx-rtmp-server-application/1  hls: http://NginxIP:Nginx-server-监听端口/Nginx-server-location/1/index.m3u8
     **/
    private final AtomicLong seq;

//    /**
//     * 异步线程池配置 - 转流任务将通过异步线程来执行，需要一个异步线程池，如果没有配置异步线程池创建转流任务时将会抛出RuntimeException
//     **/
//    private final AsyncConfigurer asyncConfigurer;

    /**
     * 异步线程池配置 - 转流任务将通过异步线程来执行，需要一个异步线程池，如果没有配置异步线程池创建转流任务时将会抛出RuntimeException
     **/
    private final ThreadPoolExecutor covertJobExecutor;

    /**
     * 可重入锁 - 保证创建转流任务、中止转流任务时的线程安全性
     **/
    private final Lock lock;

    private CameraConfigProvide cameraConfigProvide;

    @Autowired
    public StreamConvertHandler(/* AsyncConfigurer asyncConfigurer, */
                                ThreadPoolExecutor covertJobExecutor,
                                CameraConfigProvide cameraConfigProvide) {
//        this.asyncConfigurer = asyncConfigurer;
        this.covertJobExecutor = covertJobExecutor;
        this.cameraConfigProvide = cameraConfigProvide;
        this.seq = new AtomicLong(0);
        this.lock = new ReentrantLock();
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * 获取当前编号
     *
     * @return long 当前编号
     * @author Moonlight
     * @date 2021/1/25 11:12
     * @since 1.0.0
     */
    public long getSeq() {
        return this.seq.get();
    }

    /**
     * 功能描述: <br>
     * 〈将rtsp转为rtmp，并返回一个hls地址〉
     * 传入一个rtsp流地址:
     * 1. 如果该地址已经在进行转流了，那么直接从缓存中获取对应的hls地址返回
     * 2. 如果该地址没有在转流，那么：(1).获取自增编号{@link #seq}
     * (2).根据自增编号拼接处真正的rtmp、hls地址
     * (3).创建{@link CameraVO} {@link ConvertJob}
     * (4).将ConvertJob放入异步线程池{@link #covertJobExecutor}执行，返回hls地址
     *
     * @param rtspUrl rtsp流的地址
     * @return ReturnVO hls流地址 或 异常信息
     * @author Moonlight
     * @date 2021/1/25 11:13
     * @since 1.0.0
     */
    public ReturnVO rtsp2Rtmp(String rtspUrl) {
        lock.lock();

        CameraVO cameraVO = null;
        try {
            // 该RTSP地址已有转流任务，存在HLSUrl地址，且该地址可用( && HttpUtil.urlIsEffective(cameraVO.getHlsUrl()))，那么观看人数加1, 直接返回地址
            cameraVO = CacheUtil.CONVERTING_CAMERA_CACHE.get(rtspUrl);
            if (cameraVO != null) {
                if (HttpUtil.urlIsEffective(cameraVO.getHlsUrl())) {
                    cameraVO.setKeepAliveTime(new Date());
                    cameraVO.setWatchCount(cameraVO.getWatchCount() + 1);
                    return ReturnVO.ok().put("url", cameraVO.getHlsUrl());
                }
                CacheUtil.removeCache(cameraVO.getRtspUrl());
            }

            CameraConfig cameraConfig = cameraConfigProvide.provide();
            if (cameraConfig == null || StringUtils.isAnyBlank(cameraConfig.getHlsUrlTmplate(), cameraConfig.getRtmpUrlPrefix())) {
                return ReturnVO.error(-1, "摄像头配置为空或hls地址模板、rtmp地址前缀配置错误,无法创建转流任务");
            }
            if (CacheUtil.CONVERT_JOB_CACHE.size() >= cameraConfig.getJobLimit()) {
                return ReturnVO.error(-1, "转流任务已达上限,无法创建更多的转流任务");
            }

            long seq = this.seq.incrementAndGet();

            String hlsUrl = cameraConfig.getHlsUrlTmplate().replace("SEQ", String.valueOf(seq));

            cameraVO = new CameraVO(rtspUrl, cameraConfig.getRtmpUrlPrefix() + seq, hlsUrl, cameraConfig);
            ConvertJob convertJob = new ConvertJob(cameraVO);

            if (/* asyncConfigurer.getAsyncExecutor() != null */
                    covertJobExecutor != null) {
                CacheUtil.CONVERT_JOB_CACHE.put(cameraVO.getRtspUrl(), convertJob);

                /* asyncConfigurer.getAsyncExecutor().execute(convertJob); */
                covertJobExecutor.execute(convertJob);

                return ReturnVO.ok().put("url", hlsUrl);
            } else {
                throw new RuntimeException("执行转流任务时发生异常，线程池配置为空.");
            }
        } catch (Exception e) {
            log.error("创建并执行转流任务时出错rtspUrl[{}]cameraVo[{}]", rtspUrl, cameraVO, e);
            return ReturnVO.error(-1, e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    /**
     * 功能描述: <br>
     * 〈中止一个转流任务，如果存在的话〉
     * 传入一个rtsp地址，通过该地址获取对应的摄像头对象，并将摄像头观看人数 - 1:
     * 1.如果该摄像头对象观看人数大于0，则直接返回中止转流成功
     * 2.如果摄像头对象观看人数小于等于0，则获取当对应的convertJob，并中止这个转流任务
     *
     * @param rtspUrl rtsp流地址
     * @return ReturnVO code = 0 中止转流任务成功 其他表示中止转流任务失败
     * @author Moonlight
     * @date 2021/1/25 11:36
     * @since 1.0.0
     */
    public ReturnVO exitConvert(String rtspUrl) {
        lock.lock();

        CameraVO cameraVO = null;
        try {
            cameraVO = CacheUtil.CONVERTING_CAMERA_CACHE.get(rtspUrl);

            if (cameraVO == null) {
                return ReturnVO.error(-1, "找不到对应的转流任务,对应的任务可能已经被中止并移除");
            }

            cameraVO.setWatchCount(cameraVO.getWatchCount() - 1);
            if (cameraVO.getWatchCount() > 0) {
                return ReturnVO.ok();
            }

            CacheUtil.removeCache(rtspUrl);

            log.info("中止转流成功rtsp[{}]cameraVo[{}]", rtspUrl, cameraVO);

            return ReturnVO.ok();
        } catch (Exception e) {
            log.error("中止转流时出错rtspUrl[{}]cameraVo[{}]", rtspUrl, cameraVO);

            CacheUtil.removeCache(rtspUrl);

            return ReturnVO.error(-1, e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    /**
     * 功能描述: <br>
     * 〈转流任务保活〉
     * 更新cameraVO对象的keepAliveTime为当前时间
     *
     * @param rtspUrl rtsp地址
     * @return ReturnVO code == 0 保活成功，其他表示保活失败
     * @author Moonlight
     * @date 2021/1/25 11:46
     * @since 1.0.0
     */
    public ReturnVO keepAlive(String rtspUrl) {
        CameraVO cameraVO = CacheUtil.CONVERTING_CAMERA_CACHE.get(rtspUrl);
        if (cameraVO != null) {
            cameraVO.setKeepAliveTime(new Date());
            return ReturnVO.ok();
        }
        return ReturnVO.error(-1, "根据url找不到执行中的任务");
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * 获取当前转流任务的数量
     * @return ReturnVO
     * @since 1.0.0
     * @author Moonlight
     * @date 2021/6/1 12:21
     */
    public ReturnVO getConvertJonCnt() {
        lock.lock();
        try {
            return ReturnVO.ok().put("cnt", CacheUtil.CONVERT_JOB_CACHE.size());
        } finally {
            lock.unlock();
        }
    }

    /**
     * 功能描述: <br>
     * 〈〉
     * 清空所有的转流任务
     * @return ReturnVO
     * @since 1.0.0
     * @author Moonlight
     * @date 2021/6/1 14:36
     */
    public ReturnVO clearJob() {
        lock.lock();
        try {
            CacheUtil.clearJob();
        } finally {
            lock.unlock();
        }
        return ReturnVO.ok();
    }

}
