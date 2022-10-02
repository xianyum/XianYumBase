<template>
  <div class="mod-user">
    <el-form :inline="true" :model="dataForm" ref="dataForm" @keyup.enter.native="getDataList()">
      <el-form-item prop="jobName">
        <el-input v-model="dataForm.jobName" placeholder="任务名称" clearable></el-input>
      </el-form-item>
      <el-form-item prop="jobHandler">
        <el-input v-model="dataForm.jobHandler" placeholder="JobHandler" clearable></el-input>
      </el-form-item>
      <el-form-item prop="执行状态">
        <el-select v-model="dataForm.status" placeholder="任务执行状态" filterable clearable>
          <el-option
            v-for="item in logStatusList"
            :key="item.code"
            :label="item.name"
            :value="item.code"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button plain @click="getDataList()" round>查询</el-button>
        <el-button type="warning" @click="deleteLog()" round>清空日志</el-button>
        <el-button round type="info" @click="reset()">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table
      :data="dataList"
      border
      stripe
      v-loading="dataListLoading"
      style="width: 100%">
      <el-table-column
        prop="id"
        header-align="center"
        align="center"
        width="90px"
        label="日志id">
      </el-table-column>
      <el-table-column
        prop="jobName"
        header-align="center"
        align="center"
        label="任务名称">
      </el-table-column>
      <el-table-column
        prop="jobHandler"
        header-align="center"
        align="center"
        label="JobHandler">
      </el-table-column>
      <el-table-column
        prop="status"
        header-align="center"
        align="center"
        width="100px"
        label="状态">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === 1" size="small" type="danger">失败</el-tag>
          <el-tag v-else type="success" size="small">正常</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="jobRunTime"
        header-align="center"
        align="center"
        width="160px"
        label="执行时间">
        <template slot-scope="scope">
          {{scope.row.jobRunTime}}ms
        </template>
      </el-table-column>
      <el-table-column
        prop="startTime"
        header-align="center"
        align="center"
        width="160px"
        :formatter="formatTime"
        label="开始时间">
      </el-table-column>
      <el-table-column
        prop="stopTime"
        header-align="center"
        align="center"
        :formatter="formatTime"
        width="160px"
        label="结束时间">
      </el-table-column>
      <el-table-column
        width="220px"
        prop="exceptionInfo"
        header-align="center"
        align="center"
        show-overflow-tooltip
        label="异常信息">
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
  data() {
    return {
      logStatusList: [
        {
          'code': 0,
          'name': '正常'
        }, {
          'code': 1,
          'name': '失败'
        }
      ],
      dataForm: {
        jobName: '',
        jobHandler: '',
        jobId: null,
        status: null
      },
      reset() {
        this.$refs['dataForm'].resetFields()
        this.pageIndex = 1
        this.pageSize = 10
        this.dataForm.jobId = null
        this.getDataList()
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
  activated() {
    this.dataForm.jobId = this.$route.params.jobId
    this.getDataList()
  },
  methods: {
    deleteLog(){
      let message = '确认要清空日志吗？'
      this.$confirm(message, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: this.$http.adornUrl('/v1/jobLog/truncateLog'),
          method: 'post',
          data: {'jobId': 0}
        }).then(({data}) => {
          if (data && data.code === 200) {
            this.$message({
              message: '清空成功',
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
    addOrUpdateHandle(id) {
      this.$router.push({name: 'message-send-config-add-or-update', params: {id: id}})
    },
    formatTime(row, column, cell) {
      if (!cell) {
        return ''
      }
      return new Date(cell).Format('yyyy-MM-dd hh:mm:ss')
    },
    // 获取数据列表
    getDataList() {
      this.dataListLoading = true
      this.$http({
        url: this.$http.adornUrl('/v1/jobLog/getPage'),
        method: 'post',
        data: this.$http.adornData({
          'status': this.dataForm.status,
          'jobId': this.dataForm.jobId,
          'jobName': this.dataForm.jobName,
          'jobHandler': this.dataForm.jobHandler,
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
    sizeChangeHandle(val) {
      this.pageSize = val
      this.pageIndex = 1
      this.getDataList()
    },
    // 当前页
    currentChangeHandle(val) {
      this.pageIndex = val
      this.getDataList()
    },
    // 多选
    selectionChangeHandle(val) {
      this.dataListSelections = val
    },
    // 删除
    deleteHandle(id) {
      var ids = id ? [id] : this.dataListSelections.map(item => {
        return item.id
      })
      this.$http({
        url: this.$http.adornUrl('/v1/messageSendConfig/delete'),
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
    }
  }
}
</script>
