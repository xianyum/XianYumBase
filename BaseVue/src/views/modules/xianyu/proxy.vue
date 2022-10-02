<template>
  <div class="mod-user">
    <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">
      <el-form-item>
        <el-input v-model="dataForm.name" placeholder="客户端名称" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-input v-model="dataForm.id" placeholder="客户端秘钥" clearable></el-input>
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
      <el-table-column type="expand">
        <template slot-scope="props">
          <el-form label-position="left" inline class="demo-table-expand">
            <el-form-item label="最近登录时间：">
              <span>{{ new Date(props.row.loginTime).Format('yyyy-MM-dd hh:mm:ss') }}</span>
            </el-form-item>
            <el-form-item label="最近登录mac地址：">
              <span>{{ props.row.macAddress }}</span>
            </el-form-item>
            <el-form-item label="最近登录ip：">
              <span>{{ props.row.hostIp }}</span>
            </el-form-item>
            <el-form-item label="最近登录计算机名：">
              <span>{{ props.row.hostName }}</span>
            </el-form-item>
            <el-form-item label="最近登录操作系统：">
              <span>{{ props.row.osName }}</span>
            </el-form-item>
            <el-form-item label="最近登录版本号：">
              <span>V {{ numFilter(props.row.clientVersion) }}</span>
            </el-form-item>
          </el-form>
        </template>
      </el-table-column>
      <el-table-column
        label="序号"
        type="index"
        align="center"
        width="50">
      </el-table-column>
      <el-table-column
        prop="name"
        header-align="center"
        align="center"
        label="客户端名称"
        sortable>
        <template slot-scope="scope">
          <a type="primary" @click="toDetails(scope.row.id)">{{ scope.row.name }}</a>
        </template>
      </el-table-column>
      <el-table-column
        prop="id"
        header-align="center"
        align="center"
        width="280"
        label="客户端秘钥">
      </el-table-column>
      <el-table-column
        prop="status"
        header-align="center"
        align="center"
        label="连接状态"
        width="180"
        sortable>
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === 0" size="small" type="danger">离线</el-tag>
          <el-tag v-else type="success" size="small">在线</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="loginCount"
        header-align="center"
        align="center"
        label="登录次数"
        width="150"
        sortable>
      </el-table-column>
      <el-table-column
        prop="notify"
        header-align="center"
        align="center"
        label="上线通知"
        width="140px">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.notify"
            active-color="#13ce66"
            inactive-color="#00B6B6"
            :active-value=1
            :inactive-value=0
            @change="updateNotify(scope.row)">
          </el-switch>
        </template>
      </el-table-column>
      <el-table-column
        prop="notifyEmail"
        header-align="center"
        align="center"
        label="通知邮箱"
        width="190px"
        >
      </el-table-column>
      <el-table-column
        prop="loginTime"
        header-align="center"
        align="center"
        :formatter="formatTime"
        width="160px"
        label="最近登录时间"
        sortable>
      </el-table-column>
      <el-table-column
        fixed="right"
        header-align="center"
        align="center"
        width="230"
        label="操作">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="sendEmail(scope.row.id)">发送配置</el-button>
          <el-button type="text" size="small" @click="downloadConfig(scope.row.id)">生成配置</el-button>
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
import AddOrUpdate from './proxy-add-or-update'

export default {
  data () {
    return {
      dataForm: {
        name: ''
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
  components: {
    AddOrUpdate
  },
  methods: {
    downloadConfig (id){
      this.$http({
        url: this.$http.adornUrl('/proxy/downloadConfig'),
        method: 'post',
        data: this.$http.adornData({
          'id': id
        }),
        responseType: 'arraybuffer'
      }).then(({data, headers}) => {
        const link = document.createElement('a'); // 生成一个a标签。
        let blob = new Blob([data], {type: "text/plain;charset=UTF-8"});
        let objectUrl = URL.createObjectURL(blob);
        link.href = objectUrl
        link.download = decodeURI('config.ini') // 自定义文件名
        link.click() // 下载文件
        URL.revokeObjectURL(objectUrl); // 释放内存
      })
    },
    sendEmail (id) {
      this.$confirm(`确定要发送配置给客户端吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: this.$http.adornUrl('/proxy/sendEmail'),
          method: 'post',
          data: {
            'id': id
          }
        }).then(({data}) => {
          if (data && data.code === 200) {
            this.$message({
              message: '发送成功',
              type: 'success'
            })
          } else {
            this.$message.error(data.msg)
          }
        })
      }).catch(() => {
      })
    },
    numFilter (num) {

      return (Math.round(num*10)/10).toFixed(1)
    },
    updateNotify(row){
      this.$confirm(`确定要更新上线通知状态吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: this.$http.adornUrl('/proxy/update'),
          method: 'post',
          data: {
            'id': row.id,
            'notify': row.notify
          }
        }).then(({data}) => {
          if (data && data.code === 200) {
            this.$message({
              message: '上线通知状态更新成功',
              type: 'success'
            })
          } else {
            this.$message.error(data.msg)
          }
        })
      }).catch(() => {
      })
    },
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
    toDetails (id) {
      this.$router.push({name: 'xianyu-proxy-details', params: {proxyId: id}})
    },
    reset () {
      this.dataForm.name = ''
      this.dataForm.id = ''
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
        url: this.$http.adornUrl('/proxy/getPage'),
        method: 'post',
        data: this.$http.adornData({
          'name': this.dataForm.name,
          'id': this.dataForm.id,
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
          url: this.$http.adornUrl('/proxy/delete'),
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
<style rel="stylesheet/scss" lang="scss">
.demo-table-expand {
  font-size: 0;
}
.demo-table-expand label {
  width: 150px;
  color: #99a9bf;
}
.demo-table-expand .el-form-item {
  margin-right: 0;
  margin-bottom: 0;
  width: 50%;
}
</style>
