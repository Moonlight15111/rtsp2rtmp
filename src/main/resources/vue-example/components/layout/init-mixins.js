export default {
  props: {
    id: {
      type: String,
      default: () => 'myVideo'
    },
    replaceIndex: {
      type: Number,
      default: () => 0
    }
  },
  inject: ['app'],
  watch: {
    'app.mainContentOptions.videoList.hls': {
      handler () {
        this.init()
      }
    }
  },
  data () {
    return {
      urlValidateTask: 0
    }
  },
  beforeDestroy() {
    this.lists.forEach(item => {
      item.src = ''
      item.load = false
      item.loadText = ''
      item.showErr = false
      item.errMsg = ''
      item.playing = false
      item.player && item.player.dispose()
    })
  },
  async mounted () {
    await this.init()
  },
  methods: {
    clearRtspKeepAliveTimer (play) {
      let rtspKeepAliveTimer = this.app.rtspKeepAliveTimer
      const _this = this
      // 清理心跳任务
      rtspKeepAliveTimer.forEach(async (item, index) => {
        if (item.hls === play.src) {
          window.clearInterval(item.exec)
          rtspKeepAliveTimer.splice(index, 1)
          let res = await _this.$get('/screen/rtsp/exit/' + item.itemId)
          console.log('中止' + item.itemId + '的转流任务：', res)
          console.log('清除' + item.itemId + '的心跳任务完成')
        }
      })
    },
    async init () {
      let hlsList = this.app.mainContentOptions.videoList.hls
      const active = this.app.mainContentOptions.layoutInfo.active;
      // 如果是第一次进来，则hlsList必然只有一个url，那么直接初始化第一个播放器就好了
      // 如果是第n次进来，通过最后一个播放器的src可以判断出需不需要更新第一个播放器的src
      let play = this.lists[hlsList.length ? hlsList.length - 1 : 0]
      console.log('init-mixins hls length and last: ', hlsList.length, hlsList[hlsList.length - 1])
      console.log('init-mixins play: ', play)
      console.log('init-mixins active: ', active)
      if (!play.src && hlsList.length) {
        console.log('init-mixins.init() if : ', !play.src)
        // this.clearRtspKeepAliveTimer(play)
        // play.id += 4
        play.player && play.player.dispose()
        play.src = hlsList[hlsList.length ? hlsList.length - 1 : 0]
        play.load = true
        play.loadText = "拼命加载中..."
        play.playing = false
        play.showErr = false
        play.errMsg = ''
      } else if (hlsList.length && play.src !== hlsList[hlsList.length - 1]) {
        console.log('init-mixins.init() else if : ', play.src)
        if (active === 1) {
          let shift =  this.lists.splice(0, 1);
          shift = shift[0]

          shift.id++
          this.clearRtspKeepAliveTimer(shift)
          shift.player && shift.player.dispose()
          shift.src = hlsList[hlsList.length ? hlsList.length - 1 : 0]
          shift.load = true
          shift.loadText = "拼命加载中..."
          shift.playing = false
          shift.showErr = false
          shift.errMsg = ''

          this.lists.push(shift)
        }
        if (active === 4) {
          let shift = this.lists.splice(this.replaceIndex % 4, 1)
          shift = shift[0]

          this.clearRtspKeepAliveTimer(shift)

          shift.id += 4
          shift.player && shift.player.dispose()
          shift.src = hlsList[hlsList.length ? hlsList.length - 1 : 0]
          shift.load = true
          shift.loadText = "拼命加载中..."
          shift.playing = false
          shift.showErr = false
          shift.errMsg = ''
          this.lists.splice(this.replaceIndex % 4, 0, shift)

          this.replaceIndex++
        }
      }
      console.log('init-mixins - video list info: ')
      console.log(this.lists)
      await this.playerInit()
    },
    async playerInit () {
      await this.$nextTick()
      const _this = this
      this.lists.forEach( (item, index) => {
        this.syncPlay(item, index, _this)
      })
    },
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
                if (!res || (res.code && res.code !== 200)) {
                  item.playing = false
                  item.showErr = true
                  item.errMsg = '视频加载失败.无法连接到摄像头网络'
                } else {
                  item.playing = true
                  item.showErr = false
                  item.errMsg = ''
                  item.player = _this.$videojs(`${_this.id}${index}`, {
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
                item.loadText = ''
                item.load = false
                console.dir("转流使用时间: " + ((new Date().getTime() - begin) / 1000) + "s  result: " + res)
                window.clearInterval(intervalMethod)
              }
            }
            intervalMethod = window.setInterval(checkFunc, 5000)
          } else {
            item.loadText = ''
            item.load = false
            item.playing = true
            item.player = _this.$videojs(`${_this.id}${index}`, {
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
          console.error(e)
        }
      }
    }
  }
}
