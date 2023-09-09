<template>
  <div class="mod-log">
    <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">
      <el-form-item>
        <el-input v-model="dataForm.username" placeholder="用户名" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-input v-model="dataForm.loginSystem" placeholder="登录系统" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-button round @click="getDataList()">查询</el-button>
        <el-button round type="danger"  @click="deleteHandle()" :disabled="dataListSelections.length <= 0">批量踢出</el-button>
      </el-form-item>
    </el-form>

    <el-table
      :data="dataList"
      border
      stripe
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
        align="center"
        width="50">
      </el-table-column>
      <el-table-column
        prop="token"
        header-align="center"
        align="center"
        width="160"
        :show-overflow-tooltip="true"
        label="会话编号">
      </el-table-column>
      <el-table-column
        prop="username"
        header-align="center"
        align="center"
        width="120"
        label="用户名">
      </el-table-column>
      <el-table-column
        prop="ipaddr"
        header-align="center"
        align="center"
        width="150"
        label="登录IP">
      </el-table-column>
      <el-table-column
        prop="loginLocation"
        header-align="center"
        align="center"
        :show-overflow-tooltip="true"
        label="登录地点">
      </el-table-column>
      <el-table-column
        prop="loginType"
        header-align="center"
        align="center"
        label="登录方式">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.loginType === 1" size="small" type="success">QQ登录</el-tag>
          <el-tag v-else-if="scope.row.loginType === 2" size="small" type="success">支付宝登录</el-tag>
          <el-tag v-else size="small">账号登录</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="browser"
        header-align="center"
        align="center"
        label="浏览器">
      </el-table-column>
      <el-table-column
        prop="os"
        header-align="center"
        align="center"
        label="操作系统">
      </el-table-column>
      <el-table-column
        prop="loginTime"
        header-align="center"
        align="center"
        width="180"
        label="登录时间"
        :formatter="formatTime">
      </el-table-column>
      <el-table-column
        header-align="center"
        fixed="right"
        align="center"
        width="100"
        label="操作">
        <template slot-scope="scope">
          <el-button type="text" size="small"  @click="deleteHandle(scope.row.token)">踢出</el-button>
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
  </div>
</template>

<script>
  export default {
    data () {
      return {
        dataForm: {
          username: '',
          loginSystem: ''
        },
        dataListSelections: [],
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
      deleteHandle(token) {
        let tokenIds = token ? [token] : this.dataListSelections.map(item => {
          return item.token
        })
        this.$confirm(`确定要进行踢出操作吗?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl('/online/delete'),
            method: 'post',
            data: this.$http.adornData(tokenIds, false)
          }).then(({data}) => {
            if (data && data.code === 200) {
              this.$message({
                message: '踢出成功',
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
      // 获取数据列表
      getDataList () {
        this.dataListLoading = true
        this.$http({
          url: this.$http.adornUrl('/online/list'),
          method: 'post',
          data: this.$http.adornData({
            'username': this.dataForm.username,
            'loginSystem': this.dataForm.loginSystem,
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
      formatTime (row, column,cell) {
        if(!cell){
          return ''
        }
        return new Date(cell).Format('yyyy-MM-dd hh:mm:ss');
      },
    }
  }
</script>
