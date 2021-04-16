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