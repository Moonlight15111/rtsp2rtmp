# rtsp2rtmp

#### 功能介绍
 * rtsp转rtmp
 * 大华DSS接口对接
 * 海康调用SDK获取设备通道并拼接rtsp地址
 
#### 功能详解
##### rtsp转rtmp
 1. java后台将rtsp流转为rtmp流，并推到nginx上
 2. nginx将数据流以ts文件的形式写到磁盘上，并通过index.m3u8记录
 3. 前端通过nginx访问对应的index.m3u8文件来获取对应的数据流。
##### 大华DSS对接
 1. 登录大华DSS平台
 2. 从大华获取设备信息
 3. 根据设备获取通道、rtsp流、hls流地址
##### 海康对接
 1. 通过海康SDK获取到对应设备的通道数、通道号
 2. 通过通道号拼接rtsp地址
 3. 将rtsp转为rtmp流

#### 模块说明
  ```
  ├── HikVision - 海康DLL及Java开发Demo压缩包
  ├── libraries - 第三方Jar包，调用海康DLL时需要用到，这两个Jar包的引入详见pom.xml
  ├── src
      ├── main
          ├── java
              ├── cache - 转流任务缓存管理
              ├── common - 常量类定义
              ├── config - 配置类，包括异步线程池配置、摄像头配置、海康DLL路径配置
              ├── controller - 测试接口用的controller
              ├── convert - 转流任务定义
              ├── handler - handler，包括转流的handler、大华接口的handler
              ├── hikvision - 海康SDK及handler
              ├── scheduling - 定时任务，只有一个清理转流任务的定时任务
              ├── util - util，只有一个httpUtil
              ├── vo
                  ├── convert，转流用到的VO
                  ├── dss，大华DSS用到的VO
                  ├── hikvision，海康需要用到的VO
          ├── resources
              ├── static
                  ├── hls-video 前端播放HLS流相关JS文件
                  ├── rtsp-video 前端播放rtsp流相关JS文件，但是不知为何在本人机器上播不了
              ├── templates index.html H5页面示例
              ├── vue-example vue页面示例，需要安装video.js(npm install video.js)，
                                 参考: https://www.jianshu.com/p/8b8023c7ed37
                                 中文文档: https://blog.csdn.net/qq285679784/article/details/86060723
              ├── windows-resource windows环境下rtsp转rtmp需要用到的资源
  ```

#### Nginx编译及配置
##### Linux环境
 - nginx编译rtmp模块：
    1. 先下载nginx-rtmp模块。传送门：https://github.com/arut/nginx-rtmp-module/

    2. 进入nginx安装目录，执行命令：
        1. ./configure --add-module=/home/software/nginx-rtmp-module-master
        2. make && make install
   
 - 动态添加rtmp模块参考：
     1. https://www.cnblogs.com/yanjieli/p/10615361.html
     2. https://blog.csdn.net/qq_33833327/article/details/109154307

- Linux nginx配置文件参考项目中的rtsp2rtmp nginx conf.txt文件

##### Windows环境：
  
  相关资源在 resources/windows-resource,相关资源说明如下:
  
    一、Native HLS Playback插件-离线包.rar
        该压缩包是一个Chrome扩展程序，解压后将其添加到Chrome的扩展程序中，就可以直接在Chrome中播放HLS视频流了
  
    二、VLC3x64.rar
        vlc压缩包。开源的跨平台多媒体播放器，可以播放绝大部分多媒体文件，以及 DVD、音频CD、VCD，也支持rtsp、rtmp等多种流媒体协议。
        需要注意的是，如果尝试通过vlc将本地视频文件转RTSP，进行网络串流，然后使用ffmpeg或本项目进行rtsp转rtmp测试，这种做法是不可行的，
        会报错说Could not open input，原因未知。
      
    三、nginx 1.7.11.3 Gryphon.rar
        Windows环境下编译好的nginx，里面已经编译好了rtmp模块，解压即可使用

#### 注意事项
~~~
    一、convert.job-limit 配置
       1. 该配置应该尽量和线程池的核心线程数保持一致，任务上限尽量不要超过核心线程数的。
          试想一种情况: 核心线程数已经满了，但是转流任务还未达到上限。那么后续创建的转流任务就会被扔到任务队列中去，同时将
          拼接好的HLS地址返回给前端，如果此时前端去请求这个地址肯定是404的，因为任务都还没开始执行。即使你在创建新的转流任
          务时中断旧的转流任务，这个中断也并不是立即生效的。
       2. 该配置应该结合服务器实际情况进行合理配置，应该根据服务器的CPU、内存、磁盘、网络带宽等多方面进行综合评估。从笔者
          的经历来看，网络带宽相对其他方面影响大一点。
~~~

#### 异常说明：
  ~~~
   一、加密视频JavaCPP无法停止转流
          1. 异常描述: 通过海康的SDK获取到了海康NVR上面所有的通道，且RTSP地址拼接无误，设备、网络等一切正常。
                       根据获取到的RTSP地址创建好转流任务后，没有任何视频画面播出，一直404。发现nginx配置的路径下没有创建对应的文件夹及m3u8、ts文件，
                       nginx日志除了访问的地址404以外没有任何其他异常信息输出，JavaCPP日志一直输出如下信息:
                           `2021-04-08 17:00:06.695 [JavaCPP Thread ID 20248] WARN  org.bytedeco.javacv.FFmpegLogCallback - [hevc @ 00000030bc0056c0] Error parsing NAL unit #0.
                           2021-04-08 17:00:06.698 [gm-camera-demo-async-task-1] ERROR org.bytedeco.javacv.FFmpegLogCallback - [NULL @ 00000030bce44c00] PPS id out of range: 0
                           2021-04-08 17:00:06.698 [JavaCPP Thread ID 83092] ERROR org.bytedeco.javacv.FFmpegLogCallback - [hevc @ 00000030bc0024c0] PPS id out of range: 1
                           2021-04-08 17:00:06.701 [JavaCPP Thread ID 83092] WARN  org.bytedeco.javacv.FFmpegLogCallback - [hevc @ 00000030bc0024c0] Error parsing NAL unit #0.
                           2021-04-08 17:00:06.702 [JavaCPP Thread ID 23708] ERROR org.bytedeco.javacv.FFmpegLogCallback - [hevc @ 00000030bce45080] PPS id out of range: 0
                           2021-04-08 17:00:06.704 [JavaCPP Thread ID 23708] WARN  org.bytedeco.javacv.FFmpegLogCallback - [hevc @ 00000030bce45080] Error parsing NAL unit #0.
                           2021-04-08 17:00:06.704 [gm-camera-demo-async-task-1] ERROR org.bytedeco.javacv.FFmpegLogCallback - [NULL @ 00000030bce44c00] vps_reserved_three_2bits is not three
                           2021-04-08 17:00:06.707 [gm-camera-demo-async-task-1] ERROR org.bytedeco.javacv.FFmpegLogCallback - [NULL @ 00000030bce44c00] VPS 13 does not exist
                           2021-04-08 17:00:06.709 [gm-camera-demo-async-task-1] ERROR org.bytedeco.javacv.FFmpegLogCallback - [NULL @ 00000030bce44c00] SPS 0 does not exist.
                           2021-04-08 17:00:06.709 [gm-camera-demo-async-task-1] WARN  org.bytedeco.javacv.FFmpegLogCallback - [swscaler @ 00000030bc0369c0] deprecated pixel format used, make sure you did set range correctly
                           2021-04-08 17:00:06.710 [gm-camera-demo-async-task-1] ERROR org.bytedeco.javacv.FFmpegLogCallback - [NULL @ 00000030bce44c00] PPS id out of range: 1
                           2021-04-08 17:00:06.713 [gm-camera-demo-async-task-1] ERROR org.bytedeco.javacv.FFmpegLogCallback - [NULL @ 00000030bce44c00] PPS id out of range: 0
                           2021-04-08 17:00:06.713 [JavaCPP Thread ID 32048] ERROR org.bytedeco.javacv.FFmpegLogCallback - [hevc @ 00000030bce45500] vps_reserved_three_2bits is not three
                           2021-04-08 17:00:06.716 [JavaCPP Thread ID 32048] ERROR org.bytedeco.javacv.FFmpegLogCallback - [hevc @ 00000030bce45500] VPS 13 does not exist
                           2021-04-08 17:00:06.718 [JavaCPP Thread ID 32048] ERROR org.bytedeco.javacv.FFmpegLogCallback - [hevc @ 00000030bce45500] SPS 0 does not exist.
                           2021-04-08 17:00:06.719 [JavaCPP Thread ID 32048] ERROR org.bytedeco.javacv.FFmpegLogCallback - [hevc @ 00000030bce45500] PPS id out of range: 1
                           2021-04-08 17:00:06.721 [JavaCPP Thread ID 32048] WARN  org.bytedeco.javacv.FFmpegLogCallback - [hevc @ 00000030bce45500] Error parsing NAL unit #4.
                           2021-04-08 17:00:06.722 [gm-camera-demo-async-task-1] ERROR org.bytedeco.javacv.FFmpegLogCallback - [NULL @ 00000030bce44c00] PPS id out of range: 0
                           2021-04-08 17:00:06.722 [JavaCPP Thread ID 80840] ERROR org.bytedeco.javacv.FFmpegLogCallback - [hevc @ 00000030bce45980] PPS id out of range: 0
                           2021-04-08 17:00:06.725 [JavaCPP Thread ID 80840] WARN  org.bytedeco.javacv.FFmpegLogCallback - [hevc @ 00000030bce45980] Error parsing NAL unit #0.
                           2021-04-08 17:00:06.726 [gm-camera-demo-async-task-1] ERROR org.bytedeco.javacv.FFmpegLogCallback - [NULL @ 00000030bce44c00] PPS id out of range: 0`
                       即使在转流任务被清除后，该JavaCPP线程依旧在疯狂运行，占用了大量系统资源，没办法只能重启程序才能彻底结束这些线程。
          2. 解决办法: 经过排除、对比后发现，这个NVR启用了萤石云的加密，在后台关闭加密后，就能够正常转流、播放了。
          3. 遗留问题: 想不通为什么转流任务被清除后，对应的JavaCPP线程依旧在疯狂运行，后续去bytedeco提个issue问问看
   二、Nginx 不创建 m3u8 文件
          1. 异常描述: 在转流任务比较多(10个及以上,包括任务已经被中断,但是nginx HLS目录还未被销毁的) 的情况下，转流任务创建成功，Nginx中HLS文件夹、HLS文件夹中的ts文件也创建成功了，
                       但是 m3u8 文件没有创建出来或者需要等很久(30秒或以上)才会创建出来
          2. 解决办法: 减少转流任务数上限。根据nginx rtmp module 开发者的说法: M3u8 is always created after the first segment file (https://github.com/arut/nginx-rtmp-module/issues/1126)
          3. 遗留问题: 个人感觉还是和服务器的性能、网络带宽有很关, 但是目前条件有限不好实验
  ~~~     