<template>
  <aside class="video-collection__sidebar">
    <el-tabs v-model="activeName">
      <el-tab-pane name="first">
        <span slot="label"><i class="el-icon-video-camera"></i> 视频中心</span>
        <el-input
          placeholder="输入关键字进行过滤"
          v-model="filterText">
        </el-input>
        <el-tree
          ref="tree"
          :show-checkbox="app.layoutInfo.active !== 1"
          :data="app.videoList.data"
          :props="defaultProps"
          :filter-node-method="filterNode"
          node-key="id"
          @check="handleCheckChange"
          @node-click="handleClickNode"
        />
      </el-tab-pane>
      <el-tab-pane label="配置管理" name="second">
        <span slot="label"><i class="el-icon-timer"></i> 最近查看</span>
        最近查看
      </el-tab-pane>
    </el-tabs>
  </aside>
</template>

<script>
  export default {
    name: 'vc-sidebar',
    inject: ['app'],
    data () {
      return {
        activeName: 'first',
        deptList: [],
        historyList: [],
        filterText: '',
        defaultProps: {
          id: 'id',
          children: 'children',
          label: 'name'
        }
      }
    },
    watch: {
      filterText(val) {
        this.$refs.tree.filter(val);
      }
    },
    methods: {
      filterNode(value, data) {
        if (!value) return true;
        return data.name.indexOf(value) !== -1;
      },
      // 节点单选
      handleClickNode (item) {
        if (this.app.layoutInfo.active === 1 && item.isHls) {
          this.app.videoList.selected = [item.id]
          // 获取当前点击的视频列表
          this.app.getHlsLiveUrlList()
        }
      },
      handleCheckChange (data, checked) {
        this.app.videoList.selected = checked.checkedNodes.filter(item => item.isHls).map(item => item.id)
        this.app.getHlsLiveUrlList()
      }
    }
  }
</script>

<style lang="scss">
  .el-tabs__item {
    color: #666;
  }
  .el-tree {
    background: transparent;
    color: #fff;
  }
  .el-tree-node__content:hover,
  .el-tree-node:focus > .el-tree-node__content {
    background: rgba(0,0,0,0.55);
  }
  .el-input__inner {
    background: transparent;
    color: #fff;
  }
</style>
