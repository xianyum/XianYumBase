<template>
  <div class="mod-user">
    <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">
      <el-form-item>
        <el-input v-model="dataForm.proxyName" placeholder="客户端名称" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-input v-model="dataForm.proxyId" placeholder="客户端秘钥" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-input v-model="dataForm.inetPort" placeholder="公网端口" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-button @click="getDataList()" round>查询</el-button>
        <el-button type="success" @click="addOrUpdateHandle()" round>新增</el-button>
        <el-tooltip class="item" effect="light" content="当您对数据有做更改的时候，需要把数据刷入系统，保证客户端生效！" placement="top-start">
          <el-button round type="warning" @click="flushProxy">刷入系统</el-button>
        </el-tooltip>
        <el-button round type="info" @click="reset()">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table
      :data="dataList"
      border
      stripe
      v-loading="dataListLoading"
      @selection-change="selectionChangeHandle"
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
        prop="proxyName"
        header-align="center"
        align="center"
        label="客户端名称"
        sortable>
      </el-table-column>
      <el-table-column
        prop="name"
        header-align="center"
        align="center"
        label="代理名称">
      </el-table-column>
      <el-table-column
        prop="inetPort"
        header-align="center"
        align="center"
        label="外网端口"
		sortable>
      </el-table-column>
      <el-table-column
        prop="lan"
        header-align="center"
        align="center"
        label="本地代理信息">
      </el-table-column>
      <el-table-column
        prop="writeBytesStr"
        header-align="center"
        align="center"
        label="当前写入量"
        sortable>
      </el-table-column>
      <el-table-column
        prop="readBytesStr"
        header-align="center"
        align="center"
        label="当前读取量"
        sortable>
      </el-table-column>
      <el-table-column
        prop="connectCount"
        header-align="center"
        align="center"
        label="当前连接数">
      </el-table-column>
      <el-table-column
        prop="createTime"
        header-align="center"
        align="center"
        :formatter="formatTime"
        width="160px"
        label="创建时间"
        sortable>
      </el-table-column>
      <el-table-column
        fixed="right"
        header-align="center"
        align="center"
        width="150"
        label="操作">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">修改</el-button>
          <el-button type="text" size="small" @click="deleteHandle(scope.row.id)">删除</el-button>
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

    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @refreshDataList="getDataList"></add-or-update>
  </div>
</template>

<script>
import AddOrUpdate from './proxy-details-add-or-update'

export default {
  data () {
    return {
      dataForm: {
        proxyName: '',
        proxyId: '',
        inetPort: ''
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
    this.dataForm.proxyId = this.$route.params.proxyId
    this.dataForm.inetPort = this.$route.params.inetPort
    this.getDataList()
  },
  components: {
    AddOrUpdate
  },
  methods: {
    flushProxy () {
      this.$confirm(`确定要进行刷入系统操作吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: this.$http.adornUrl('/proxy/flushProxy'),
          method: 'post',
          data: {
            'id': 111111
          }
        }).then(({data}) => {
          if (data && data.code === 200) {
            this.$message({
              message: '已成功刷入系统中',
              type: 'success'
            })
          } else {
            this.$message.error(data.msg)
          }
        })
      }).catch(() => {
      })
    },
    reset () {
      this.dataForm.proxyName = ''
      this.dataForm.proxyId = ''
      this.pageIndex = 1
      this.pageSize = 10
      this.getDataList()
    },
    formatTime (row, column, cell) {
      if (!cell) {
        return ''
      }
      return new Date(cell).Format('yyyy-MM-dd hh:mm:ss')
    },
    // 获取数据列表
    getDataList () {
      this.dataListLoading = true
      this.$http({
        url: this.$http.adornUrl('/proxyDetails/getPage'),
        method: 'post',
        data: this.$http.adornData({
          'proxyName': this.dataForm.proxyName,
          'proxyId': this.dataForm.proxyId,
          'inetPort': this.dataForm.inetPort,
          'pageNum': this.pageIndex,
          'pageSize': this.pageSize
        })
      }).then(({data}) => {
        if (data && data.code === 200) {
          this.dataList = data.data.records
          this.totalPage = data.data.total
        } else {
          this.$message.error(data.msg)
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
    },
    // 新增 / 修改
    addOrUpdateHandle (id) {
      this.addOrUpdateVisible = true
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id)
      })
    },
    // 删除
    deleteHandle (id) {
      var ids = id ? [id] : this.dataListSelections.map(item => {
        return item.id
      })
      this.$confirm(`确定要进行删除操作吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: this.$http.adornUrl('/proxyDetails/delete'),
          method: 'post',
          data: this.$http.adornData(ids, false)
        }).then(({data}) => {
          if (data && data.code === 200) {
            this.$message({
              message: '删除成功',
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
    }
  }
}
</script>
