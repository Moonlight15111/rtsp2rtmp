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
        rtspKeepAliveTimer.forEach(async (item, index, array) => {
            if (item.hls === play.src) {
            window.clearInterval(item.exec)
            array.splice(index, 1)
            let res = await _this.$get('/screen/rtsp/exit/' + item.itemId)
            console.log('中止' + item.itemId + ' - ' + item.hls + '的转流任务：', res)
            console.log('清除' + item.itemId + ' - ' + item.hls +  '的心跳任务完成')
        }
    })
    },
    async init () {
        let hlsList = this.app.mainContentOptions.videoList.hls
        const active = this.app.mainContentOptions.layoutInfo.active;
        // 如果是第一次进来，则hlsList必然只有一个url，那么直接初始化第一个播放器就好了
        // 如果是第n次进来，通过最后一个播放器的src可以判断出需不需要更新第一个播放器的src
        let play = this.lists[hlsList.length ? hlsList.length - 1 : 0]
        console.log('init-mixins lists length and last: ', this.lists.length, this.lists[this.lists.length - 1])
        console.log('init-mixins hls length and last: ', hlsList.length, hlsList[hlsList.length - 1])
        console.log('init-mixins play: ', play)
        console.log('init-mixins active: ', active)
        if ((!play || !play.src) && hlsList.length) {
            console.log('init-mixins init if : ', !play || !play.src)
            if (!play) {
                const last = this.lists[this.lists.length - 1]
                play = {id: last && last.id ? last.id + 4 : 0, src: '', player: null, load: false, loadText: '', errMsg: '', showErr: false, playing: false}
            }
            if (play.player) {
                this.tryDispose(play)
            } else {
                play.player = null
            }
            console.log('init-mixins play: ', play)
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

                this.tryDispose(shift)

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

                this.tryDispose(shift)

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
    tryDispose (play) {
        try {
            play.player && play.player.dispose()
        } catch (e) {
            console.log('移除视频播放DOM时出错.', play, e)
            play = {id: 0, src: '', player: null, load: false, loadText: '', errMsg: '', showErr: false, playing: false}
        }
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
                            item.player = _this.$videojs(`${_this.id}${index}`, {
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