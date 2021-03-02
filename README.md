# rtsp2rtmp

#### 介绍
Java Rtsp转Rtmp及大华DSS接口对接，海康调用SDK获取设备通道并拼接rtsp地址

#### 软件架构
java后台将rtsp流转为rtmp流，并推到nginx上，nginx再将rtmp转为HLS，前端访问nginx来获取HLS流数据。

nginx编译rtmp模块：
  1. 先下载nginx-rtmp模块。传送门：https://github.com/arut/nginx-rtmp-module/

  2. 进入nginx安装目录，执行命令：
     ./configure --add-module=/home/software/nginx-rtmp-module-master
   
     make && make install
   
     动态添加rtmp模块参考：
       https://www.cnblogs.com/yanjieli/p/10615361.html
       https://blog.csdn.net/qq_33833327/article/details/109154307

Linux nginx配置文件参考项目中的rtsp2rtmp nginx conf.txt文件
  

Windows下Rtsp转Rtmp：
  
  相关资源在 resources/windows-resource,相关资源说明如下:
  
    一、Native HLS Playback插件-离线包.rar
        该压缩包是一个Chrome扩展程序，解压后将其添加到Chrome的扩展程序中，就可以直接在Chrome中播放HLS视频流了
  
    二、VLC3x64.rar
        vlc压缩包。开源的跨平台多媒体播放器，可以播放绝大部分多媒体文件，以及 DVD、音频CD、VCD，也支持rtsp、rtmp等多种流媒体协议。
        需要注意的是，如果尝试通过vlc将本地视频文件转RTSP，进行网络串流，然后使用ffmpeg或本项目进行rtsp转rtmp测试，这种做法是不可行的，
        会报错说Could not open input，原因未知。
      
    三、nginx 1.7.11.3 Gryphon.rar
        Windows环境下编译好的nginx，里面已经编译好了rtmp模块，解压即可使用