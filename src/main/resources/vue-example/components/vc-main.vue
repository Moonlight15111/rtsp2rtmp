<template>
  <main class="video-collection__main">
    <!--视频内容-->
    <div class="video-collection__main-video-list">
      <component
        v-if="!dialogVisible"
        :is="`Layout${app.layoutInfo.active}`"
        :key="app.layoutInfo.active"
      />
    </div>
    <!--底部-->
    <div class="video-collection__main-footer">
      <!--布局设置-->
      <div class="video-collection__layout-setup">
        <span
          class="video-collection__layout-setup-icon"
          v-for="item in app.layoutInfo.data"
          :key="item.id"
          @click="handlerLayoutSetup(item)"
          :style="{backgroundPositionX: `-${item.left}px`, opacity: app.layoutInfo.active === item.layoutItem ? 1 : 0.5}"
        ></span>
      </div>
      <!--功能操作-->
      <div class="video-collection__handler">
        <el-tooltip
          v-for="item in app.handlerList"
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
          ></span>
        </el-tooltip>
      </div>
    </div>
    <!--侧栏收起按钮-->
    <div class="video-collection__sidebar-visible-toggle-btn" @click="sideBarToggle">
      <span class="el-icon-arrow-left" v-if="app.sideBarVisible"></span>
      <span class="el-icon-arrow-right" v-else></span>
    </div>
    <el-dialog
      :show-close="false"
      fullscreen
      :visible.sync="dialogVisible">
      <template #title>
        <vc-header />
      </template>
      <div class="video-collection__main-video-list" style="width: 100%; height: 100%;">
        <component
          v-if="dialogVisible"
          :is="`Layout${app.layoutInfo.active}`"
          :key="app.layoutInfo.active"
        />
      </div>
    </el-dialog>
  </main>
</template>

<script>
  import vcHeader from './vc-header'
  import Layout1 from './layout/layout-1'
  import Layout4 from './layout/layout-4'
  import Layout6 from './layout/layout-6'
  import Layout8 from './layout/layout-8'
  import Layout9 from './layout/layout-9'
  import html2canvas from 'html2canvas'

  export default {
    name: 'vc-main',
    inject: ['app'],
    components: {
      vcHeader,
      Layout1,
      Layout4,
      Layout6,
      Layout8,
      Layout9
    },
    data () {
      return {
        dialogVisible: false
      }
    },
    methods: {
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
        this.dialogVisible = true
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
        this.app.layoutInfo.active = item.layoutItem
      },
      // 侧栏切换
      sideBarToggle () {
        this.$emit('sideBarToggle', !this.app.sideBarVisible)
      }
    }
  }
</script>

<style lang="scss">
  .video-collection {
    .el-dialog {
      background: #0B1435;
    }
  }
</style>
