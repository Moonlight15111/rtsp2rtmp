# rtsp2rtmp

#### 介绍
Java Rtsp转Rtmp及大华DSS接口对接

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

nginx配置文件：

#user  root;
worker_processes  1;

#error_log  logs/error.log;
#error_log  logs/error.log  notice;
#error_log  logs/error.log  info;

#pid        logs/nginx.pid;


events {
    worker_connections  1024;
}

rtmp {  
    server {  
        listen 1935;
        application dssdemo {
            live on;  
       }  
       application hls {
         live on;  
         hls on;  
         hls_path /tmp/hls;         # 请提前建好该目录或给予相应的权限
         wait_key on;               # 对视频切片进行保护，这样就不会产生马赛克了
         hls_fragment 10s;          # 每个视频切片的时长
         hls_playlist_length 60s;   # 总共可以回看的事件
         hls_continuous on;         # 连续模式。 
         hls_cleanup on;            # 对多余的切片进行删除。 
         hls_nested on;             # 嵌套模式
       }
    } 
}

http {
    include       mime.types;
    default_type  application/octet-stream;

    #log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
    #                  '$status $body_bytes_sent "$http_referer" '
    #                  '"$http_user_agent" "$http_x_forwarded_for"';

    #access_log  logs/access.log  main;

    sendfile        on;
    #tcp_nopush     on;

    keepalive_timeout  65;

    #gzip  on;

    server {
        listen       8080;
        server_name  localhost;

        #charset koi8-r;

        #access_log  logs/host.access.log  main;

        location / {
            root   html;
            index  index.html index.htm;
        }
        location /stat {
            rtmp_stat all;
            rtmp_stat_stylesheet stat.xsl;
        }
        
        location /control {
            rtmp_control all;
        }
        
        location /hls { # http://192.168.2.45:8080/hls/1/index.m3u8
            types{  
                application/vnd.apple.mpegurl m3u8;  
                video/mp2t ts;  
            }
            root /tmp;
        }

        #error_page  404              /404.html;

        # redirect server error pages to the static page /50x.html
        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }
    }
}
