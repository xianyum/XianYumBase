<template>
  <div class="mod-user">
    <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">
      <el-form-item>
        <el-input v-model="dataForm.title" placeholder="标题" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="getDataList()">查询</el-button>
        <el-button type="success" @click="openWxPushXiaoDaoImage = true">推送</el-button>
        <el-button type="info" @click="reset()">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table
      :data="dataList"
      border
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
        prop="time"
        header-align="center"
        align="center"
        width="100"
        label="时间">
      </el-table-column>
      <el-table-column
        prop="pushStatus"
        header-align="center"
        align="center"
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
        :formatter="formatePushTime">
      </el-table-column>
      <el-table-column
        prop="createTime"
        header-align="center"
        align="center"
        label="创建时间"
        :formatter="formateCreateTime">
      </el-table-column>
      <el-table-column
        prop="params"
        header-align="center"
        align="center"
        show-overflow-tooltip
        label="请求参数">
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
      formateCreateTime (row, column) {
        var objD = row.createTime
        if (!objD) {
          return ''
        }
        objD = new Date(objD)
        var str
        var yy = objD.getYear()
        if (yy < 1900) yy = yy + 1900
        var MM = objD.getMonth() + 1
        if (MM < 10) MM = '0' + MM
        var dd = objD.getDate()
        if (dd < 10) dd = '0' + dd
        var hh = objD.getHours()
        if (hh < 10) hh = '0' + hh
        var mm = objD.getMinutes()
        if (mm < 10) mm = '0' + mm
        var ss = objD.getSeconds()
        if (ss < 10) ss = '0' + ss
        str = yy + '-' + MM + '-' + dd + ' ' + hh + ':' + mm + ':' + ss
        return (str)
      },
      formatePushTime (row, column) {
        var objD = row.pushTime
        if (!objD) {
          return ''
        }
        objD = new Date(objD)
        var str
        var yy = objD.getYear()
        if (yy < 1900) yy = yy + 1900
        var MM = objD.getMonth() + 1
        if (MM < 10) MM = '0' + MM
        var dd = objD.getDate()
        if (dd < 10) dd = '0' + dd
        var hh = objD.getHours()
        if (hh < 10) hh = '0' + hh
        var mm = objD.getMinutes()
        if (mm < 10) mm = '0' + mm
        var ss = objD.getSeconds()
        if (ss < 10) ss = '0' + ss
        str = yy + '-' + MM + '-' + dd + ' ' + hh + ':' + mm + ':' + ss
        return (str)
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
