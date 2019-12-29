<template>
  <div class="mod-log">
    <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">
      <el-form-item>
        <el-input v-model="dataForm.nameOrDesc" placeholder="用户名／用户操作" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-button @click="getDataList()">查询</el-button>
        <el-button type="danger"  @click="deleteHandle()" :disabled="dataListSelections.length <= 0">批量删除</el-button>
      </el-form-item>
    </el-form>
    <el-table
      :data="dataList"
      border
      @selection-change="selectionChangeHandle"
      v-loading="dataListLoading"
      style="width: 100%">
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
        prop="username"
        header-align="center"
        align="center"
        width="150"
        label="用户名">
      </el-table-column>
      <el-table-column
        prop="operation"
        header-align="center"
        align="center"
        width="200"
        label="用户操作">
      </el-table-column>
      <el-table-column
        prop="method"
        header-align="center"
        align="center"
        width="150"
        :show-overflow-tooltip="true"
        label="请求方法">
      </el-table-column>
      <el-table-column
        prop="params"
        header-align="center"
        align="center"
        :show-overflow-tooltip="true"
        label="请求参数">
      </el-table-column>
      <el-table-column
        prop="timeMs"
        header-align="center"
        align="center"
        width="130"
        label="执行时长">
      </el-table-column>
      <el-table-column
        prop="ip"
        header-align="center"
        align="center"
        width="150"
        label="IP地址">
      </el-table-column>
      <el-table-column
        prop="ipInfo"
        header-align="center"
        align="center"
        label="IP地点">
      </el-table-column>
      <el-table-column
        prop="createTime"
        header-align="center"
        align="center"
        width="180"
        label="创建时间"
        :formatter="formateCreateTime">
      </el-table-column>
      <el-table-column
        header-align="center"
        fixed="right"
        align="center"
        width="150"
        label="操作">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="getRequestParams(scope.row)">查看请求参数</el-button>
          <el-button type="text" size="small"  @click="deleteHandle(scope.row.id)">删除</el-button>
        </template>
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
      :title="'请求参数'"
      :close-on-click-modal="false"
      :visible.sync="requestVisible"
      :before-close="cancleRequestForm"
      width="25%">
      <json-viewer
        :value="requestParam" :expand-depth=5
        copyable
        boxed
        sort></json-viewer>
    </el-dialog>
  </div>
</template>

<script>
  export default {
    data () {
      return {
        dataForm: {
          nameOrDesc: ''
        },
        requestParam: {
        },
        dataListSelections: [],
        requestVisible: false,
        dataList: [],
        pageIndex: 1,
        pageSize: 10,
        totalPage: 0,
        dataListLoading: false,
        selectionDataList: []
      }
    },
    created () {
      this.getDataList()
    },
    methods: {
      // 删除
      deleteHandle(id) {
        var logIdS = id ? [id] : this.dataListSelections.map(item => {
          return item.id
        })
        this.$confirm(`确定要进行删除操作吗?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl('/log/delete'),
            method: 'post',
            data: this.$http.adornData(logIdS, false)
          }).then(({data}) => {
            if (data && data.code === 200) {
              this.$message({
                message: '操作成功',
                type: 'success',
                duration: 1500,
                onClose: () => {
                  this.getDataList()
                }
              })
            } else {
              this.$message.error(data.msg)
            }
          })
        }).catch(() => {
        })
      },
      // 多选
      selectionChangeHandle(val) {
        this.dataListSelections = val
      },
      cancleRequestForm (){
        this.requestParam = ''
        this.requestVisible = false;
      },
      getRequestParams (row){
        let params = row.params
        this.requestParam = JSON.parse(params)
        this.requestVisible = true
      },
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
      // 获取数据列表
      getDataList () {
        this.dataListLoading = true
        this.$http({
          url: this.$http.adornUrl('/log/list'),
          method: 'post',
          data: this.$http.adornData({
            'nameOrDesc': this.dataForm.nameOrDesc,
            'pageNum': this.pageIndex,
            'pageSize': this.pageSize
          })
        }).then(({data}) => {
          if (data && data.code === 200) {
            this.dataList = data.data.records
            this.dataList.forEach((item) => {
              item.timeMs = item.time + 'ms'
            })
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
      }
    }
  }
</script>
