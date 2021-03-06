# rtsp2rtmp

#### 功能介绍
 * rtsp转rtmp
 * 大华DSS接口对接
 * 海康调用SDK获取设备通道并拼接rtsp地址，调用SDK控制摄像头
 
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
 1. 通过海康SDK获取到对应设备的通道数、通道号, 通过通道号拼接rtsp地址
 2. 通过海康SDK控制摄像头,可操控摄像头进行转向、调焦等操作, 自动巡航、调整预置点等暂未实现
     如有需求,请自行对照海康API文档进行实现, 另外需要注意的是, 这些操作是实时的, 但是想要实时
     看到摄像头做出相应的动作, 只能直连海康摄像头观看, 即本项目的预览是有一定延迟的, 经过调整
     可以把延迟压到5 - 10秒左右
     海康API: https://open.hikvision.com/docs/d2da3584e3859815a750128dd892fc34

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

 - nginx配置文件
     1. 参考本项目中的 rtsp2rtmp nginx conf.txt 文件

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

#### 配置说明
~~~
    一、convert.job-limit 配置
       1. 该配置应该尽量和线程池的核心线程数保持一致，任务上限尽量不要超过核心线程数的。
          试想一种情况: 核心线程数已经满了，但是转流任务还未达到上限。那么后续创建的转流任务就会被扔到任务队列中去，同时将
          拼接好的HLS地址返回给前端，如果此时前端去请求这个地址肯定是404的，因为任务都还没开始执行。即使你在创建新的转流任
          务时中断旧的转流任务，这个中断也并不是立即生效的。
       2. 该配置应该结合服务器实际情况进行合理配置，应该根据服务器的CPU、内存、磁盘、网络带宽等多方面进行综合评估。从笔者
          的经历来看，网络带宽相对其他方面影响大一点。
    二、 降低延迟配置
         在推流过程中，推流端、服务器端、播放端、网络带宽等等都会影响延迟问题。
       1. 在推流端，可以通过调整关键帧间隔，因为服务器是以关键帧进行缓存的，如果关键帧间隔过大服务器端的缓存也会变大，但是同码率下关键帧间隔越小清晰度也会越低，建议视场景而定
                    减小缓冲区
                    h264 编码使用 H.264 baseline profile，减少编码时消耗的时间
       2. 在服务端，java转流程序设置tune为zerolatency
                    nginx配置降低切片大小: 
                         hls_fragment 1s; 
                         hls_max_fragment 2s; 
                         hls_playlist_length 3s;
                    hls_fragment和hls_max_fragment配置一起决定了一个ts切片文件的时长，ts文件的时长会小于hls_max_fragment，大于等于hls_fragment
       3. 在播放端，把播放端的缓冲区调小点
~~~

#### 常见问题说明：
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
   三、转流任务的线程名相同
          1. 异常描述: 检查日志时，发现转流任务的日志的线程名都是一样的，使用jstack追踪进去发现实际上是不同的线程，但是线程名确实是一模一样。
          2. 解决办法: 在getAsyncExecutor()上加@Bean注解。通过debug、查阅Spring源码，发现是开发脚手架的异步任务线程池配置有问题，并没有将Executor纳入Spring管理。也就是说在调用
                       getAsyncExecutor()方法执行转流任务时，每次都会生成一个新的线程池，在新生成的线程池中创建一条新的线程，由于线程池的配置都是一致的，所以线程的名称也会一致
                       虽然不影响使用，但是影响日志查阅、线程池也无法复用造成不必要的开销。
          3. 补充说明: 由于一些原因，实际线上并没有使用上述方式解决，而是自己自定义了一个ThreadPoolExecutor进行管理。另外关于 getAsyncExecutor() 返回的 Executor 的更多说明，
                       请自行查看@EnableAsync 注解的源码注释。最重要的一段注释如下:  
                       * <p>Note: In the above example the {@code ThreadPoolTaskExecutor} is not a fully managed
                       * Spring bean. Add the {@code @Bean} annotation to the {@code getAsyncExecutor()} method
                       * if you want a fully managed bean. In such circumstances it is no longer necessary to
                       * manually call the {@code executor.initialize()} method as this will be invoked
                       * automatically when the bean is initialized.
   四、转流任务全部吊死，半天没有响应，直至连接超时
          1. 异常描述: 在存在多个转流任务时，如果第一个获取到互斥资源进行转流的任务出现网络不通等异常状况时，会阻塞住，导致后面的转流任务全部吊死等着。
          2. 解决办法: 为转流任务配置stimeout、rwtimeout等参数
          3. 补充说明: 这个问题的本质在于FFmpegFrameGrabber的start()是用org.bytedeco.ffmpeg.global.avcodec.class锁上的，在start()方法内部调用的是ffmpeg的avformat_open_input
                       方法，这个方法是底层是走一个tcp连接，如果你不设置超时时间，就是使用默认的tcp超时时间，这个时间要长一点，这段时间内这个线程也不会释放锁，其他线程就只有等着了
  五、摄像头通过路由器端口映射，RTSP地址访问不通
          1. 异常描述: 有一个摄像头192.168.1.10，通过TP-LINK路由器10.10.10.10的 81 端口映射出来，向外提供RTSP流，RTSP默认端口为554。TP-LINK映射配置如下所示:
                       外部端口       内部端口      IP地址            协议类型 
                        81              554         192.168.1.10        TCP  
                       配置好以后, 死活不能通过 10.10.10.10:81 访问到RTSP流, 但是内部又能通过 192.168.1.10:554 访问到. 一顿操作, 开放端口、关掉防火墙都试了一遍
                       还是不起作用.
          2. 解决办法: 打开海康摄像头的管理界面, 打开 网络 - 基本配置 - 配置 - 端口, 将RTSP端口改成 81, TP-LINK映射更改如下:
                       外部端口       内部端口      IP地址            协议类型 
                        81              81         192.168.1.10        TCP  
                       这下就好了, 能正常访问了
          3. 补充说明: 起先怀疑是路由器到海康摄像头之间的网络有问题，但是直接通过 192.168.1.10:554 又能访问, 所以想着是不是端口的问题，把 554 端口改掉以后果然可行。
                       到底是为什么554不行，换个端口又行了，也没想明白，猜测可能是端口被占用或者被屏蔽了？如果有人知道具体原因，还请赐教。
  六、苹果手机上视频播放会自动进入全屏状态且无法多播
          1. 异常描述: 苹果手机上，点击视频播放后，在数据流推过来并进行播放时，会自动进入全屏状态，且无法同时播放多个视频，同一时刻只能有一个视频进行播放，其他的视频会自动暂停
          2. 解决办法: video 标签去掉 data-setup 属性，preload属性设置为metadata  同时为 video 标签设置 webkit-playsinline、playsinline、x5-playsinline、x-webkit-airplay="allow"
                       上述配置可以解决自动进入全屏状态的问题，为 video 标签设置 autoplay、muted 属性可以解决无法同时播放多个视频的问题
          3. 补充说明: 出现上述情况，和IOS、safari 对视频、音频的播放策略有关，结合文档，推测应该是为了替用户省钱，因为用户可以处于使用移动网络的状态下，是按数据单位收费的，所以
                       IOS、safari 不会对视频、音频进行任何预加载、自动播放等操作，只有用户实际触发这个动作才会加载数据，另一个方面可能是减少带宽压力。
                       文档内容摘抄如下: 
                           In Safari on iOS (for all devices, including iPad), where the user may be on a cellular network and be charged per data unit,
                           preload and autoplay are disabled. No data is loaded until the user initiates it. This means the JavaScript play() and load()
                           methods are also inactive until the user initiates playback, unless the play() or load() method is triggered by user action.
                           In other words, a user-initiated Play button works, but an onLoad="play()" event does not.             
                       详见: https://developer.apple.com/library/archive/documentation/AudioVideo/Conceptual/Using_HTML5_Audio_Video/PlayingandSynthesizingSounds/PlayingandSynthesizingSounds.html#//apple_ref/doc/uid/TP40009523-CH6-SW1
                             https://webkit.org/blog/6784/new-video-policies-for-ios/   
  七、苹果手机多播时出现即使后台已经推流成功了，但还是不能播放
          1. 异常描述: 苹果手机多播时出现即使后台已经推流成功了，但还是不能播放，并提示
                       The media playback was aborted due to a corruption problem or because the media used features your browser did not support.
                       在刷新页面后重新点进来又可以正常播. 如果不多播，只播放一个视频又不会出现上述情况
          2. 解决办法: 技术上并没有解决掉这个问题，另外考虑到移动端屏幕较小，多播也看不到什么东西，所以直接砍掉了这个需求，移动端只能单播
          3. 补充说明: 翻了很多文档、资料，没有查到什么有用的东西，猜测还是和IOS、Safair 自身的视频、音频播放策略有关
  ~~~     