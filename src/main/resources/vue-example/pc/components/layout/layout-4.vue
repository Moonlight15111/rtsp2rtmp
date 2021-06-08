<template>
  <!--布局2-->
  <el-row class="wp-100 hp-100">
    <el-col
      :span="12"
      class="video-collection__main-video-list-item hp-50"
      v-for="(item, index) in lists"
      :key="item.id"
      v-loading="item.load"
      :element-loading-text="item.loadText"
      element-loading-spinner="el-icon-loading"
      element-loading-background="rgba(255, 255, 255, 0.5)"
      style="position: relative;"
    >
      <el-alert
        v-if="item.showErr"
        :title="item.errMsg"
        type="info"
        center
        style="z-index: 99999; width: 45%; position: absolute; left: 32%;top: 40%; background: rgba(255, 255, 255, 0.0001); color: white;"
        :closable="false"
      >
      </el-alert>
      <video
        :id="`${id}${index}`"
        :key="item.id"
        class="video-js vjs-default-skin vjs-big-play-centered"
        muted
        controls
        preload="auto"
        style='width: 100%;height: 100%;'
      >
      </video>
    </el-col>
  </el-row>
</template>

<script>
  import InitMixins from './init-mixins'

  export default {
    name: 'layout-4',
    mixins: [InitMixins],
    data () {
      return {
        lists: [
          {id: 1, src: '', player: null, load: false, loadText: '', errMsg: '', showErr: false, playing: false},
          {id: 2, src: '', player: null, load: false, loadText: '', errMsg: '', showErr: false, playing: false},
          {id: 3, src: '', player: null, load: false, loadText: '', errMsg: '', showErr: false, playing: false},
          {id: 4, src: '', player: null, load: false, loadText: '', errMsg: '', showErr: false, playing: false}
        ]
      }
    }
  }
</script>
