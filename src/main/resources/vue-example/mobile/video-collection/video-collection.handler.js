import DeptSelect from '@c/common/dept-select'

export default {
  name: 'video-collection',
  provide () {
    return {
      app: this
    }
  },
  components: {
    DeptSelect
  },
  data () {
    return {
      // 布局形式
      layoutType: 1,
      // 选中的视频列表
      // selectVideoList: [],
      videoSelectInfo: {
        visible: false,
        title: '视频选择',
        selected: [],
        hls: []
      },
      videoList: [],
      // rtsp转流心跳保持记录
      rtspKeepAliveTimer: [],
      rtspKeepAliveId: 0,
      replaceIndex: 0,
      playVideoMap: {}
    }
  },
  computed: {
    /* ------ 视频展示相关 start ------ */
    /**
     * 根据 layoutType 的大小动态计算 视频的宽度
     */
    itemStyle () {
      return {
        width: this.layoutType > 1 ? '50%' : '100%',
        height: this.layoutType > 1 ? '27vw' : `54vw`
      }
    },
    /* ------ 视频展示相关 end ------ */
    /* ------ 视频选择相关 start ------ */
    selectedId () {
      return this.videoSelectInfo.selected.map(item => item.id)
    }
    /* ------ 视频选择相关 end ------ */
  },
  watch: {
    /* ------ 视频选择相关 start ------ */
    // 视频播放最大限制 改变时需要清空 已经选中的
    layoutType () {
      this.initVideoList()
      this.replaceIndex = 0
      this.videoSelectInfo.selected = []
      this.videoSelectInfo.hls = []
      this.videoSelectInfo.title = '视频选择'
    }
    /* ------ 视频选择相关 end ------ */
  },
  mounted () {
    this.initVideoList()
  },
  /**
   * 整个播放流程如下:
   *   1. 点击节点，从 dept-select 组件中将选中的结点通过emit回传到本组件，由本组件的handleSelectDept方法接收
   *   2. 调用handlerSelectVideoItem方法，将选中的节点更新到videoSelectInfo.selected中，并获取到选中节点的HLS地址，将HLS地址也更新到videoSelectInfo.hls
   *   3. 调用updateVideoList方法，更新videoList
   *   4. 调用playerInit方法，根据videoList中的摄像头初始化播放器
   *   5. 调用syncPlay方法，构建videoJs实例，尝试进行视频播放
   *
   * 另外关于IOS或其他苹果系列中，会存在无法同时播放多个视频的情况,
   * 是因为Safari自身的策略导致的，说明如下:
   *   In Safari on iOS (for all devices, including iPad), where the user may be on a cellular network and be charged per data unit,
   *   preload and autoplay are disabled. No data is loaded until the user initiates it. This means the JavaScript play() and load()
   *   methods are also inactive until the user initiates playback, unless the play() or load() method is triggered by user action.
   *   In other words, a user-initiated Play button works, but an onLoad="play()" event does not.
   * 详见: https://developer.apple.com/library/archive/documentation/AudioVideo/Conceptual/Using_HTML5_Audio_Video/PlayingandSynthesizingSounds/PlayingandSynthesizingSounds.html#//apple_ref/doc/uid/TP40009523-CH6-SW1
   * add by moonlight 2021/6/7
   *
   * 同时播放多个视频的问题已经解决，相关文档请见: https://webkit.org/blog/6784/new-video-policies-for-ios/   update by moonlight 2021/6/8
   *
   * 又有一个新问题: IOS 有时会出现即使已经推流成功了，播放器还是不能播放，显示
   * 猜测还是和 IOS 的视频、音频播放策略有关
   *
   */
  methods: {
    /* ------ 视频展示相关 start ------ */
    /**
     * 初始化 video 列表
     */
    initVideoList () {
      const videoList = []
      for (let i = 0; i < this.layoutType; i++) {
        videoList.push({
          id: Math.ceil(Math.random() * 10000),
          src: null,
          player: null,
          load: false,
          loadText: null,
          errMsg: null,
          showErr: false,
          playing: false
        })
      }
      this.videoList = videoList
    },
    /**
     * 部门选择
     * 默认选中最顶级部门
     * @param dept
     */
    async handleSelectDept (dept) {
      console.log('handle select dept: ', dept)
      if (dept && dept.isVideo) {
        this.handlerSelectVideoItem(dept)
      }
    },
    /**
     * 处理选中的摄像头节点:
     *   1. 判断当前选中节点在之前是否已被选过了
     *   2. 更新选中节点列表
     *   3. 获取节点的HLS地址
     */
    async handlerSelectVideoItem (item) {
      // 判断是否已经选择
      const isSelect = this.videoSelectInfo.selected.filter(sItem => sItem.id === item.id)
      if (isSelect.length) {
        return
      }
      // 更新选中节点列表，如果选中的节点已经达到了布局数，就把最旧的节点踢掉
      if (this.videoSelectInfo.selected.length >= this.layoutType) {
        this.videoSelectInfo.selected.splice(0, 1)
      }
      this.videoSelectInfo.selected.push(item)

      // 获取节点的HLS地址并塞到 videoSelectInfo.hls 里面去
      if (this.videoSelectInfo.hls.length >= this.layoutType) {
        this.videoSelectInfo.hls.splice(0, 1)
      }
      if (item.channelSource && item.channelSource === '手动添加' && item.rtspUrl && item.rtspUrl.startsWith('rtsp')) {
        await this.createRtspConvertJob(item)
      } else {
        await this.getHlsLiveUrlList(item)
      }
      console.log('videoSelectInfo.selected: ', this.videoSelectInfo.selected)
      this.updateVideoList()
    },
    // 通过 DSS 平台 获取 HLS 地址
    async getHlsLiveUrlList (item) {
      try {
        const data = await this.$post('/screen/getHlsLiveUrls', [item.id], false)
        if (data && data.code === 0) {
          const {hlsUrls} = data
          if (hlsUrls && hlsUrls.length > 0) {
            this.videoSelectInfo.hls.push(hlsUrls[0])
          } else {
            this.$toast.fail('没有获取到可用的视频地址')
            console.log('没有获取到可用的视频地址: ', item, data)
          }
        } else {
          this.$toast.fail(data.msg)
        }
      } catch (e) {
        console.error(e)
      }
    },
    // 创建rtsp转流任务，通过 Java 后台将 RTSP 流转为 RTMP 流
    async createRtspConvertJob (item) {
      try {
        const data = await this.$get('/screen/rtsp/convert/' + item.id)
        if (data && data.code === 0) {
          const {url} = data
          this.videoSelectInfo.hls.push(url)
          // 为每个rtsp转流任务都创建心跳任务，进行保活
          // 该心跳任务应该在这个视频被关闭或被替换掉时停止掉
          const timerId = this.rtspKeepAliveId++
          const _this = this
          const task = window.setInterval(this.rtspKeepAlive, 60000, item.id, timerId, _this)
          const timer = {
            id: timerId,
            itemId: item.id,
            hls: url,
            exec: task
          }
          this.rtspKeepAliveTimer.unshift(timer)
        } else {
          this.$toast.fail(data.msg)
        }
      } catch (e) {
        console.error(e)
      }
      console.log('this.videoSelectInfo.hls: ', this.videoSelectInfo.hls)
    },
    // rtsp转流任务心跳保活
    async rtspKeepAlive (itemId, timerId, _this) {
      let success = false
      try {
        const data = await this.$get('/screen/rtsp/keep_alive/' + itemId)
        if (data && data.code === 0) {
          console.log(itemId + data.msg)
          success = true
        } else {
          console.error(itemId + "保活失败" + data.msg + ' 准备移除该心跳任务')
        }
      } catch (e) {
        console.error(itemId + '保活时发生异常', e)
      }
      if (!success) {
        _this.rtspKeepAliveTimer.map((item) => {
          if (timerId === item.id) {
            window.clearInterval(item.exec)
          }
        })
        _this.rtspKeepAliveTimer = _this.rtspKeepAliveTimer.filter(item => item.id !== timerId)
        console.log(_this.rtspKeepAliveTimer)
      }
    },
    clearRtspKeepAliveTimer (play) {
      const _this = this
      // 清理心跳任务
      this.rtspKeepAliveTimer.forEach(async (item, index, array) => {
        if (item.hls === play.src) {
          window.clearInterval(item.exec)
          array.splice(index, 1)
          let res = await _this.$get('/screen/rtsp/exit/' + item.itemId)
          console.log('clear - 中止' + item.itemId + ' - ' + item.hls + '的转流任务：', res)
          console.log('clear - 清除' + item.itemId + ' - ' + item.hls +  '的心跳任务完成')
        }
      })
    },
    // 尝试dispose摄像头
    tryDispose (play) {
      try {
        play.player && play.player.dispose()
      } catch (e) {
        console.log('移除视频播放DOM时出错.', play, e)
        play = {id: 0, src: '', player: null, load: false, loadText: '', errMsg: '', showErr: false, playing: false}
      }
    },
    // 更新摄像头列表
    async updateVideoList () {
      const hlsList = this.videoSelectInfo.hls

      let play = this.videoList[hlsList.length ? hlsList.length - 1 : 0]

      console.log('video list ', this.videoList)
      console.log('hls list ', hlsList)
      console.log('play ', play)
      if ((!play || !play.src) && hlsList.length) {
        if (!play) {
          const last = this.videoList[this.videoList.length - 1]
          play = {id: last && last.id ? last.id + this.layoutType : 0, src: '', player: null, load: false, loadText: '', errMsg: '', showErr: false, playing: false}
        }
        if (play.player) {
          this.tryDispose(play)
        } else {
          play.player = null
        }
        play.src = hlsList[hlsList.length ? hlsList.length - 1 : 0]
        play.load = true
        play.loadText = "拼命加载中..."
        play.playing = false
        play.showErr = false
        play.errMsg = ''
      } else if (hlsList.length && play.src !== hlsList[hlsList.length - 1]) {
        let shift
        if (this.layoutType === 1) {
          shift =  this.videoList.splice(0, 1)[0]

          shift.id++
          this.clearRtspKeepAliveTimer(shift)

          this.tryDispose(shift)

          shift.src = hlsList[hlsList.length ? hlsList.length - 1 : 0]
          shift.load = true
          shift.loadText = "拼命加载中..."
          shift.playing = false
          shift.showErr = false
          shift.errMsg = ''

          this.videoList.push(shift)
        } else {
          shift = this.videoList.splice(this.replaceIndex % this.layoutType, 1)[0]

          shift.id += this.layoutType
          this.clearRtspKeepAliveTimer(shift)

          this.tryDispose(shift)

          shift.src = hlsList[hlsList.length ? hlsList.length - 1 : 0]
          shift.load = true
          shift.loadText = "拼命加载中..."
          shift.playing = false
          shift.showErr = false
          shift.errMsg = ''
          this.videoList.splice(this.replaceIndex % this.layoutType, 0, shift)

          this.replaceIndex++
        }
        console.log('update video list else if shift ', shift)
      }
      console.log('update video list - video list: ', this.videoList)
      await this.playerInit()
    },
    // 初始化播放器
    async playerInit () {
      await this.$nextTick()
      const _this = this
      this.videoList.forEach( (item, index) => {
        this.syncPlay(item, index, _this)
      })
    },
    // 播放视频
    // 只有当 item 的 src 有值，且当前摄像头没有处于播放中，且 item 的
    // src 地址是可用的才能正常播放
    async syncPlay (item, index, _this) {
      if (item.src && !item.playing) {
        try {
          let res = await _this.$http.get(item.src)
          if (!res || (res.code && res.code === 404)) {
            const begin = new Date().getTime()
            let intervalMethod
            let retry = 12
            const checkFunc = async function () {
              res = await _this.$http.get(item.src)
              if ((!res || (res.code && res.code === 404)) && retry >= 0) {
                retry--
              } else {
                item.loadText = ''
                item.load = false

                // 防止找不到节点 播放前更新一下节点
                await _this.$nextTick()

                item.player = _this.$videojs(`video${item.id}`, {
                  bigPlayButton: false,
                  textTrackDisplay: false,
                  posterImage: true,
                  errorDisplay: true,
                  controlBar: true
                })

                if (!res || (res.code && res.code !== 200)) {
                  item.playing = false
                  item.showErr = true
                  item.errMsg = '视频加载失败.无法连接到摄像头网络或摄像头存在故障'
                  _this.clearRtspKeepAliveTimer(item)
                } else {
                  item.playing = true
                  item.showErr = false
                  item.errMsg = ''
                  item.player.src({
                    src: item.src,
                    type: 'application/x-mpegURL'
                  })

                  item.player.play()
                }
                console.dir("转流使用时间: " + ((new Date().getTime() - begin) / 1000) + "s  result: " + res)
                window.clearInterval(intervalMethod)
              }
            }
            intervalMethod = window.setInterval(checkFunc, 5000)
          } else {
            item.load = false
            item.loadText = ''
            item.playing = true

            // 防止找不到节点 播放前更新一下节点
            await _this.$nextTick()

            item.player = _this.$videojs(`video${item.id}`, {
              bigPlayButton: false,
              textTrackDisplay: false,
              posterImage: true,
              errorDisplay: true,
              controlBar: true
            })

            item.player.src({
              src: item.src,
              type: 'application/x-mpegURL'
            })

            item.player.play()
          }
        } catch (e) {
          item.loadText = ''
          item.load = false
          item.playing = false
          item.showErr = true
          item.errMsg = '视频加载失败.无法连接到摄像头网络或浏览器不支持内嵌播放器'
          _this.clearRtspKeepAliveTimer(item)
          console.error(e)
        }
      }
    }
    /* ------ 视频展示相关 end ------ */
    /**
     * 视频选择
     * @param videoList
     */
    // handlerSelectVideo (videoList) {
    //   console.log('已选的视频列表', videoList)
    //   this.selectVideoList = videoList
    // }
  }
}
