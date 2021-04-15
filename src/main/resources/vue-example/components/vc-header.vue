<template>
  <header class="video-collection__header">
    <h1 class="video-collection__header-text">视频监控中心</h1>
    <span class="video-collection__header-time">{{currentTime}}</span>
  </header>
</template>

<script>
  export default {
    name: 'vc-header',
    data () {
      return {
        currentTime: '',
        currentInterval: null
      }
    },
    mounted () {
      this.init()
    },
    beforeDestroy () {
      clearInterval(this.currentInterval)
    },
    methods: {
      init () {

      }
    }
  }
</script>

<style scoped>

</style>
