<template>
  <div class="video-collection">
    <div class="video-collection__wrapper">
      <!--部门选择 start-->
      <dept-select
        isVideo
        @handleSelectDept="handleSelectDept"
      />
      <!--部门选择 end-->
      <!--视频选择 start-->
<!--      <div class="video-select">-->
<!--        &lt;!&ndash; 展示的标题 start&ndash;&gt;-->
<!--        <div-->
<!--          class="video-select__title"-->
<!--          @click="videoSelectInfo.visible = true"-->
<!--        >-->
<!--          <div class="video-select__title-text">-->
<!--            <i class="fa fa-video-camera"></i>-->
<!--            {{videoSelectInfo.title}}-->
<!--          </div>-->
<!--          <i class="fa fa-caret-down"></i>-->
<!--        </div>-->
<!--        &lt;!&ndash; 展示的标题 end&ndash;&gt;-->
<!--        &lt;!&ndash; 视频列表 start&ndash;&gt;-->
<!--        <van-popup-->
<!--          v-model="videoSelectInfo.visible"-->
<!--          position="bottom"-->
<!--          style="height: 65%;"-->
<!--        >-->
<!--          <div-->
<!--            class="video-select__video-list"-->
<!--            style="height: 90%; overflow: auto;"-->
<!--          >-->
<!--            <van-cell-group-->
<!--              v-for="item in videoSelectInfo.data"-->
<!--              :key="item.id"-->
<!--              class="video-select__video-list-item"-->
<!--              @click="handlerSelectVideoItem(item)"-->
<!--            >-->
<!--              <van-cell-->
<!--                :title="item.name"-->
<!--              >-->
<!--                <template #right-icon>-->
<!--                  <van-icon-->
<!--                    v-show="selectedId.includes(item.id)"-->
<!--                    name="success"-->
<!--                    color="#1989fa"-->
<!--                  />-->
<!--                </template>-->
<!--              </van-cell>-->
<!--            </van-cell-group>-->
<!--          </div>-->
<!--          <van-button-->
<!--            round-->
<!--            block-->
<!--            type="info"-->
<!--            @click="handlerConfirmVideoList"-->
<!--          >-->
<!--            确定-->
<!--          </van-button>-->
<!--        </van-popup>-->
<!--        &lt;!&ndash; 视频列表 end&ndash;&gt;-->
<!--      </div>-->
      <!--视频选择 end-->
      <!--视频展示区域 start-->
      <div class="video-show">
        <div
          v-for="(item, index) in videoList"
          :key="item.id"
          class="video-show__item"
          :style="itemStyle"
        >
          <van-loading
            v-if="item.load"
            style="color: #fff; width: 100%; height: 100%; display: flex; align-items: center; justify-content: center;"
            color="#1989fa">
            {{ item.loadText }}
          </van-loading>

          <div
            v-else
            style="height: 100%;"
          >
            <div
              v-if="item.showErr"
              style="color: #fff; width: 100%; height: 100%; display: flex; align-items: center; justify-content: center;"
            >
              {{ item.errMsg }}
            </div>

            <!-- data-setup='{}' preload="auto"   webkit-playsinline 到 muted 都是为了适配IOS设置的,前4个是允许不陷入全屏模式即可播放，后两个是为了多可以多播 -->
            <video
              v-if="item.src && !item.load && !item.showErr"
              :id="`video${item.id}`"
              class="video-js vjs-default-skin vjs-big-play-centered"
              controls
              preload="metadata"
              style='width: 100%;height: 100%;'
              webkit-playsinline
              playsinline
              x5-playsinline
              x-webkit-airplay="allow"
              autoplay
              muted
            />
            <div
              v-else-if="!item.src"
              style="color: #fff; width: 100%; height: 100%; display: flex; align-items: center; justify-content: center;"
            >
              请选择需要播放的摄像头
            </div>
          </div>
        </div>
      </div>
      <!--视频展示区域 end-->
      <!--底部布局切换 start-->
      <van-tabbar v-model="layoutType">
        <van-tabbar-item :name="1">
          <template #icon="props">
            <svg-icon
              v-if="props.active"
              icon-class="layout-1-active"
            />
            <svg-icon
              v-if="!props.active"
              icon-class="layout-1"
            />
          </template>
        </van-tabbar-item>
        <van-tabbar-item :name="4">
          <template #icon="props">
            <svg-icon
              v-if="props.active"
              icon-class="layout-4-active"
            />
            <svg-icon
              v-if="!props.active"
              icon-class="layout-4"
            />
          </template>
        </van-tabbar-item>
      </van-tabbar>
      <!--底部布局切换 end-->
    </div>
  </div>
</template>

<script>
  import Handler from './video-collection.handler'

  export default Handler
</script>

<style scoped lang="scss">
@import "./video-collection";
@import "./video-select";
@import "./video-show";
</style>
