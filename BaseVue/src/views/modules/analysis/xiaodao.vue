<template>
  <div class="mod-user">
    <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">
      <el-form-item>
        <el-input v-model="dataForm.title" placeholder="标题" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-button round  @click="getDataList()">查询</el-button>
        <el-button round type="primary" @click="openWxPushXiaoDaoImage = true">推送</el-button>
        <el-button round type="info" @click="reset()">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table
      :data="dataList"
      border
      stripe
      v-loading="dataListLoading"
      @selection-change="selectionChangeHandle"
      style="width: 100%;">
      <el-table-column
        type="selection"
        header-align="center"
        align="center"
        width="50">
      </el-table-column>
      <el-table-column
        label="序号"
        type="index"
        width="50">
      </el-table-column>
      <el-table-column
        prop="title"
        header-align="center"
        align="center"
        label="标题">
      </el-table-column>
      <el-table-column
        prop="url"
        header-align="center"
        align="center"
        width="270"
        label="url">
        <template slot-scope="scope">
          <a :href="scope.row.url" target="_blank" class="buttonText">{{scope.row.url}}</a>
        </template>
      </el-table-column>
      <el-table-column
        prop="pushStatus"
        header-align="center"
        align="center"
        width="120"
        label="推送状态">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.pushStatus === 0" size="small" type="danger">未推送</el-tag>
          <el-tag v-else size="small">已推送</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="pushTime"
        header-align="center"
        align="center"
        label="推送时间"
        width="160px"
        :formatter="formateTime">
      </el-table-column>
      <el-table-column
        prop="createTime"
        header-align="center"
        align="center"
        label="创建时间"
        width="160px"
        :formatter="formateTime">
      </el-table-column>
    </el-table>
    <el-pagination
      @size-change="sizeChangeHandle"
      @current-change="currentChangeHandle"
      :current-page="pageIndex"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="pageSize"
      :total="totalPage"
      layout="total, sizes, prev, pager, next, jumper">
    </el-pagination>

    <el-dialog
      title="扫码关注推送微信"
      :visible.sync="openWxPushXiaoDaoImage"
      width="18%"
      center>
      <div align="center">
        <img :src="imgUrl" height="200" width="200">
      </div>
    </el-dialog>
  </div>
</template>

<script>
  export default {
    data () {
      return {
        imgUrl: 'https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=gQFq8TwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAyZlZCc1VKSVFjWWoxT0trYWh2Yy0AAgSuh6JfAwQAjScA',
        openWxPushXiaoDaoImage: false,
        dataForm: {
          title: ''
        },
        dataList: [],
        pageIndex: 1,
        pageSize: 10,
        totalPage: 0,
        dataListLoading: false,
        dataListSelections: [],
        addOrUpdateVisible: false
      }
    },
    activated () {
      this.getDataList()
    },
    methods: {
      formateTime (row, column,cell) {
        if(!cell){
          return ''
        }
        return new Date(cell).Format('yyyy-MM-dd hh:mm:ss');
      },
      reset () {
        this.dataForm.title = ''
        this.pageIndex = 1
        this.pageSize = 10
        this.getDataList()
      },
      // 获取数据列表
      getDataList () {
        this.dataListLoading = true
        this.$http({
          url: this.$http.adornUrl('/xiaodao/list'),
          method: 'post',
          data: this.$http.adornData({
            'title': this.dataForm.title,
            'pageNum': this.pageIndex,
            'pageSize': this.pageSize
          })
        }).then(({data}) => {
          if (data && data.code === 200) {
            this.dataList = data.data.records
            this.totalPage = data.data.total
          } else {
            this.dataList = []
            this.totalPage = 0
          }
          this.dataListLoading = false
        })
      },
      // 每页数
      sizeChangeHandle (val) {
        this.pageSize = val
        this.pageIndex = 1
        this.getDataList()
      },
      // 当前页
      currentChangeHandle (val) {
        this.pageIndex = val
        this.getDataList()
      },
      // 多选
      selectionChangeHandle (val) {
        this.dataListSelections = val
      }
    }
  }
</script>
