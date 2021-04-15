<template>
  <div class="video-collection">
    <header class="video-collection__header">
      <h1 class="video-collection__header-text"></h1>
      <span class="video-collection__header-time">{{headerOptions.currentTime}}</span>
    </header>
    <el-row class="video-collection__content">
      <!-- 侧栏-->
      <el-col class="hp-100" :span="sideBarOptions.visible ? 5 : 0">
        <aside class="video-collection__sidebar hp-100">
          <el-tabs v-model="sideBarOptions.activeTab">
            <el-tab-pane name="first">
              <span slot="label"><i class="el-icon-video-camera"></i> 视频</span>
              <el-input
                placeholder="输入关键字进行过滤"
                v-model="sideBarOptions.filterText">
              </el-input>
              <el-tree
                ref="tree"
                :data="mainContentOptions.videoList.data"
                :props="sideBarOptions.defaultProps"
                :filter-node-method="filterNode"
                :default-expanded-keys="[mainContentOptions.videoList.defaultExpandId]"
                :current-node-key="Number($route.query.id)"
                highlight-current
                node-key="id"
                @node-click="handleClickNode"
              />
            </el-tab-pane>
            <el-tab-pane label="配置管理" name="second">
              <span slot="label"><i class="el-icon-timer"></i> 最近查看</span>
              <el-tree
                :data="mainContentOptions.videoList.history"
                :props="sideBarOptions.defaultProps"
                node-key="id"
                @node-click="handleClickNodeHistory"
              />
            </el-tab-pane>
          </el-tabs>
        </aside>
      </el-col>
      <!-- 中心内容-->
      <el-col class="hp-100" :span="sideBarOptions.visible ? 19 : 24">
        <main class="video-collection__main hp-100">
          <!--视频内容-->
          <div class="video-collection__main-video-list">
            <component
              :is="`Layout${mainContentOptions.layoutInfo.active}`"
              :key="mainContentOptions.layoutInfo.active"
            />
          </div>
          <!--底部-->
          <div class="video-collection__main-footer">
            <!--布局设置-->
            <div class="video-collection__layout-setup">
            <span
              class="video-collection__layout-setup-icon"
              v-for="item in mainContentOptions.layoutInfo.data"
              :key="item.id"
              @click="handlerLayoutSetup(item)"
              :style="{backgroundPositionX: `-${item.left}px`, opacity: mainContentOptions.layoutInfo.active === item.layoutItem ? 1 : 0.5}"
            />
            </div>
            <!--功能操作-->
            <div class="video-collection__handler">
              <el-tooltip
                v-for="item in mainContentOptions.handlerList"
                :key="item.id"
                class="item"
                effect="dark"
                :content="item.title"
                placement="top"
              >
                <span
                  @click="handleOptions(item)"
                  class="video-collection__handler-icon"
                  :style="{backgroundPositionX: `-${item.left}px`}"
                />
              </el-tooltip>
            </div>
          </div>
          <!--侧栏收起按钮-->
          <div class="video-collection__sidebar-visible-toggle-btn" @click="sideBarToggle">
            <span class="el-icon-arrow-left" v-if="sideBarOptions.visible"></span>
            <span class="el-icon-arrow-right" v-else></span>
          </div>
          <el-dialog
            :show-close="false"
            fullscreen
            :visible.sync="mainContentOptions.dialogVisible">
            <template #title>
              <header class="video-collection__header">
                <h1 class="video-collection__header-text">视频监控</h1>
                <span class="video-collection__header-time">{{headerOptions.currentTime}}</span>
              </header>
            </template>
            <div class="video-collection__main-video-list" style="width: 100%; height: 100%;">
              <component
                v-if="mainContentOptions.dialogVisible"
                id="fullVideo"
                :is="`Layout${mainContentOptions.layoutInfo.active}`"
                :key="mainContentOptions.layoutInfo.active"
              />
            </div>
          </el-dialog>
        </main>
      </el-col>
    </el-row>
  </div>
</template>

<script>
  import Layout1 from './components/layout/layout-1'
  import Layout4 from './components/layout/layout-4'
  import {treeDataTranslate} from '@/utils'
  import html2canvas from "html2canvas"

  const VIDEO_HISTORY = 'VIDEO_HISTORY'

  export default {
    name: 'video-collection',
    provide () {
      return {
        app: this
      }
    },
    components: {
      Layout1,
      Layout4,
    },
    data () {
      return {
        headerOptions: {
          currentTime: '',
          currentInterval: null
        },
        sideBarOptions: {
          visible: true,
          activeTab: 'first',
          deptList: [],
          historyList: [],
          filterText: '',
          defaultProps: {
            id: 'id',
            children: 'children',
            label: 'name'
          }
        },
        mainContentOptions: {
          dialogVisible: false,
          layoutInfo: {
            active: 1,
            data: [
              { id: 12314, layoutItem: 1, left: 0 },
              { id: 12315, layoutItem: 4, left: 40 },
              // { id: 12316, layoutItem: 6, left: 80 },
              // { id: 12317, layoutItem: 8, left: 120 },
              // { id: 12318, layoutItem: 9, left: 160 }
            ]
          },
          handlerList: [
            {id: 15431, value: 1, title: '全屏', left: 0},
            // {id: 15432, value: 2, title: '声音', left: 50},
            // {id: 15433, value: 3, title: '暂停', left: 100},
            // {id: 15434, value: 4, title: '保存', left: 150},
            {id: 15435, value: 5, title: '截图', left: 200}
          ],
          videoList: {
            selected: [],
            defaultExpandId: -99,
            data: [],
            history: JSON.parse(sessionStorage.getItem('VIDEO_HISTORY') || '[]'),
            hls: []
          }
        },
        // rtsp转流心跳保持记录
        rtspKeepAliveTimer: [],
        rtspKeepAliveId: 0
      }
    },
    watch: {
      'sideBarOptions.filterText': {
        handler (nVal) {
          this.$refs.tree.filter(nVal)
        }
      }
    },
    async mounted() {
      await this.init()
    },
    destroyed () {
      this.rtspKeepAliveTimer.map((item) => {
        window.clearInterval(item.exec)
      })
      this.rtspKeepAliveTimer = []
      this.rtspKeepAliveId = 0
    },
    methods: {
      async init () {
        // 头部时间初始化
        this.headerInit()
        // 部门初始化
        await this.getDeptTreeList()
      },
      /* 头部
       =========================== */
      headerInit () {
        this.headerOptions.currentTime = this.$moment().format('YYYY-MM-DD HH:mm:ss')
        this.headerOptions.currentInterval = setInterval(() => {
          this.headerOptions.currentTime = this.$moment().format('YYYY-MM-DD HH:mm:ss')
        }, 1000)
      },
      /* 侧栏
      ============================= */
      sideBarToggle () {
        this.sideBarOptions.visible = !this.sideBarOptions.visible
      },
      // 节点筛选
      filterNode(value, data) {
        if (!value) return true;
        return data.name.indexOf(value) !== -1;
      },
      // 查看历史列表
      handleClickNodeHistory (item) {
        const {videoList, layoutInfo} = this.mainContentOptions
        let contains = false
        videoList.selected.forEach(id => {
          if (id === item.itemId) {
            contains = true
          }
        })
        if (contains) {
          return
        }
        if (videoList.hls.length >= layoutInfo.active) {
          // videoList.selected = []
          videoList.selected.splice(0, 1)
        }
        if (item.channelSource && item.channelSource === '手动添加' && item.rtspUrl && item.rtspUrl.startsWith('rtsp')) {
          this.createRtspConvertJob(item, item.itemId)
        } else {
          videoList.hls.push(item.src)
        }
      },
      // 节点单选
      handleClickNode (item) {
        const {videoList, layoutInfo} = this.mainContentOptions
        let contains = false
        videoList.selected.forEach(id => {
          if (id === item.id) {
            contains = true
          }
        })
        if (contains) {
          return
        }
        if (videoList.selected.length >= layoutInfo.active) {
          videoList.selected.splice(0, 1)
        }
        if (item.channelSource && item.channelSource === '手动添加' && item.rtspUrl && item.rtspUrl.startsWith('rtsp')) {
          videoList.selected.push(item.id)
          this.createRtspConvertJob(item, item.id)
        } else if (item.isHls) {
          videoList.selected.push(item.id)
          this.getHlsLiveUrlList([], item)
        }
      },
      // 创建rtsp转流任务
      async createRtspConvertJob (item, itemId) {
        const {videoList, layoutInfo} = this.mainContentOptions
        try {
          const data = await this.$get('/screen/rtsp/convert/' + itemId)
          if (data && data.code === 0) {
            const {url} = data

            // 为每个rtsp转流任务都创建心跳任务，进行保活
            // 该心跳任务应该在这个视频被关闭或被替换掉时停止掉
            const timerId = this.rtspKeepAliveId++
            const _this = this
            const task = window.setInterval(this.rtspKeepAlive, 60000, itemId, timerId, _this)
            const timer = {
              id: timerId,
              itemId: itemId,
              hls: url,
              exec: task
            }
            this.rtspKeepAliveTimer.unshift(timer)

            if (videoList.hls.length >= layoutInfo.active) {
              videoList.hls.splice(0, 1)
            }
            videoList.hls.push(url)
            // 保存到查看历史
            let contains = false
            videoList.history.map(val => {
              if (val.name === item.name && val.itemId === itemId) {
                contains = true
              }
            })
            if (!contains) {
              const historyItem = {
                id: Math.ceil(Math.random() * 10000),
                itemId: itemId,
                name: item.name,
                isHls: false,
                parentId: null,
                channelSource: item.channelSource,
                rtspUrl: item.rtspUrl,
                src: url
              }
              videoList.history.unshift(historyItem);
              sessionStorage.setItem(VIDEO_HISTORY, JSON.stringify(videoList.history));
            }
          } else {
            this.$message.error(data.msg)
          }
        } catch (e) {
          console.error(e)
        }
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
      /* 中心区域
      =========================== */
      // 功能操作
      handleOptions (item) {
        const foo = {
          '全屏': this.handleFullScreen,
          '截图': this.handleScreenCapture
        }
        foo[item.title]()
      },
      // 全屏
      handleFullScreen () {
        this.mainContentOptions.dialogVisible = true
      },
      // 截图
      async handleScreenCapture () {
        const loading = this.$loading({
          lock: true,
          text: '正在保存截图...',
          spinner: 'el-icon-loading',
          background: 'rgba(0, 0, 0, 0.7)'
        })
        try {
          const canvas = await html2canvas(
            document.querySelector('.video-collection'),
            {
              allowTaint: true,
              backgroundColor: '#f1f1f1',
              useCORS: true,
              scale: 2,
              removeContainer: false
            }
          )
          const imgUrl = canvas.toDataURL('image/png', 1.0)

          const dowLink = document.createElement('a')
          dowLink.download = this.$moment().format('YYYY-MM-DD HH-mm-ss')
          dowLink.href = imgUrl
          dowLink.click()

          this.$message.success('截图保存成功')
        } catch (e) {
          console.error(e)
          this.$message.error('截图保存失败')
        } finally {
          loading.close()
        }
      },
      // 布局设置
      handlerLayoutSetup (item) {
        this.mainContentOptions.layoutInfo.active = item.layoutItem
        // 清空视频列表
        const {videoList} = this.mainContentOptions
        videoList.selected = []
        videoList.hls = []
      },
      /* 获取数据
      ============================== */
      // 获取视频列表
      async getDeptTreeList () {
        try {
          const data = await this.$get('/screen/getTreeList')
          if (data && data.code === 0) {
            let {channelTreeList, deptTreeList} = data
            channelTreeList.map(item => {
              item.isHls = true
              return item
            })
            deptTreeList.map(item => {
              item.isHls = false
              return item
            })
            this.mainContentOptions.videoList.data = treeDataTranslate([...channelTreeList, ...deptTreeList])

            const bmid = this.$route.query.id
            if (bmid) {
              const defaultDept = deptTreeList.filter(item => item.id === Number(bmid))
              const defaultHls = channelTreeList.filter(item => item.parentId === Number(bmid))
              if (defaultDept.length && defaultHls.length) {
                this.mainContentOptions.videoList.selected = [defaultHls[0].id]
                this.mainContentOptions.videoList.defaultExpandId = defaultHls[0].id
                await this.getHlsLiveUrlList([], defaultHls[0])
              }
            }
          } else {
            this.$message.error(data.msg)
          }
        } catch (e) {
          console.error(e)
        }
      },
      // 视频流列表
      async getHlsLiveUrlList (testData, item) {
        const {videoList, layoutInfo} = this.mainContentOptions
        try {
          // videoList.hls = testData
          const data = await this.$post('/screen/getHlsLiveUrls', [item.id], false)
          if (data && data.code === 0) {
            const {hlsUrls} = data
            if (hlsUrls && hlsUrls.length && hlsUrls.length >= layoutInfo.active) {
              videoList.hls = hlsUrls
            } else if (hlsUrls && hlsUrls.length && hlsUrls.length + videoList.hls.length <= layoutInfo.active) {
              for (let url of hlsUrls) {
                videoList.hls.push(url)
              }
            } else if (hlsUrls && hlsUrls.length) {
              videoList.hls.splice(0, hlsUrls.length)
              for (let url of hlsUrls) {
                videoList.hls.push(url)
              }
            }
            console.dir('index - hls list: ')
            console.dir(videoList.hls)
            // 保存查看历史
            let contains = false
            videoList.history.map(val => {
              if (val.name === item.name && val.itemId === item.id) {
                contains = true
              }
            })
            if (!contains) {
              const historyItem = {
                id: Math.ceil(Math.random() * 10000),
                itemId: item.id,
                name: item.name,
                isHls: true,
                parentId: null,
                channelSource: item.channelSource,
                rtspUrl: item.rtspUrl,
                src: videoList.hls[videoList.hls.length - 1]
              }
              videoList.history.unshift(historyItem);
              sessionStorage.setItem(VIDEO_HISTORY, JSON.stringify(videoList.history));
            }
          } else {
            this.$message.error(data.msg)
          }
        } catch (e) {
          console.error(e)
        }
      }
    }
  }
</script>

<style lang="scss">
  @import "main";
</style>
