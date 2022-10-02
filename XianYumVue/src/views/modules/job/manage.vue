<template>
  <div class="mod-user">
    <el-form :inline="true" :model="dataForm" ref="dataForm" @keyup.enter.native="getDataList()">
      <el-form-item prop="jobName">
        <el-input v-model="dataForm.jobName" placeholder="任务名称" clearable></el-input>
      </el-form-item>
      <el-form-item prop="jobHandler">
        <el-input v-model="dataForm.jobHandler" placeholder="JobHandler" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-button plain @click="getDataList()" round>查询</el-button>
        <el-button type="success" @click="addOrUpdateHandle()" round>新增</el-button>
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
        type="selection"
        header-align="center"
        align="center"
        width="50">
      </el-table-column>
      <el-table-column
        prop="jobId"
        header-align="center"
        align="center"
        width="80"
        label="任务编号">
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
        prop="cronExpression"
        header-align="center"
        align="center"
        label="Cron">
      </el-table-column>
      <el-table-column header-align="center" align="center" label="状态" prop="status" width="110px">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.status"
            active-value='0'
            inactive-value='1'
            @change="handleStatusChange(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
<!--      <el-table-column-->
<!--        prop="remark"-->
<!--        header-align="center"-->
<!--        align="center"-->
<!--        show-overflow-tooltip-->
<!--        label="任务描述">-->
<!--      </el-table-column>-->
      <el-table-column
        prop="createTime"
        header-align="center"
        align="center"
        :formatter="formatTime"
        width="160px"
        label="创建日期">
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="addOrUpdateHandle(scope.row.jobId)">修改
          </el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="deleteHandle(scope.row.jobId)">删除</el-button>
          <el-dropdown size="mini" @command="(command) => handleCommand(command, scope.row)">
            <span class="el-dropdown-link">
              <i class="el-icon-d-arrow-right el-icon--right"></i>更多
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="handleRun" icon="el-icon-caret-right">执行一次</el-dropdown-item>
              <el-dropdown-item command="handleView" icon="el-icon-view">任务详细</el-dropdown-item>
              <el-dropdown-item command="handleJobLog" icon="el-icon-s-operation">调度日志</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
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

    <!-- 任务日志详细 -->
    <el-dialog title="任务详细" :visible.sync="openView" width="700px" append-to-body>
      <el-form ref="form" :model="form" label-width="120px" size="mini">
        <el-row>
          <el-col :span="12">
            <el-form-item label="任务编号：">{{ form.jobId }}</el-form-item>
            <el-form-item label="任务名称：">{{ form.jobName }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="告警消息编码：">{{ form.messageCode }}</el-form-item>
            <el-form-item label="创建时间：">{{ parseTime(form.createTime) }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="cron表达式：">{{ form.cronExpression }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="下次执行时间：">{{ parseTime(form.nextValidTime) }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="JobHandler：">{{ form.jobHandler }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="任务状态：">
              <div v-if="form.status == 0">正常</div>
              <div v-else-if="form.status == 1">暂停</div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否并发：">
              <div v-if="form.concurrent == 0">允许</div>
              <div v-else-if="form.concurrent == 1">禁止</div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="执行策略：">
              <div v-if="form.misfirePolicy == 0">默认策略</div>
              <div v-else-if="form.misfirePolicy == 1">立即执行</div>
              <div v-else-if="form.misfirePolicy == 2">执行一次</div>
              <div v-else-if="form.misfirePolicy == 3">放弃执行</div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="任务参数：">{{ form.jobParams }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="任务备注：">{{ form.remark }}</el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="openView = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import AddOrUpdate from './manage-add-or-update'

export default {
  data () {
    return {
      openView: false,
      drawer: false,
      dataForm: {
        jobName: '',
        jobHandler: ''
      },
      // 表单参数
      form: {},
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
    reset () {
      this.$refs['dataForm'].resetFields()
      this.pageIndex = 1
      this.pageSize = 10
      this.getDataList()
    },
    // 更多操作触发
    handleCommand (command, row) {
      switch (command) {
        case 'handleRun':
          this.handleRun(row)
          break
        case 'handleView':
          this.handleView(row)
          break
        case 'handleJobLog':
          this.handleJobLog(row)
          break
        default:
          break
      }
    },
    /** 任务详细信息 */
    handleView (row) {
      let jobId = row.jobId
      this.$http({
        url: this.$http.adornUrl('/v1/job/getById'),
        method: 'post',
        data: {'jobId': jobId}
      }).then(({data}) => {
        if (data && data.code === 200) {
          this.form = data.data
          this.openView = true
        } else {
          this.$message.error(data.msg)
        }
      })
    },
    /** 任务日志列表查询 */
    handleJobLog (row) {
      const jobId = row.jobId || 0
      this.$router.push({name: 'job-log', params: {jobId: jobId}})
    },
    /** 执行一次 */
    handleRun (row) {
      let jobId = row.jobId
      this.$http({
        url: this.$http.adornUrl('/v1/job/runOnce'),
        method: 'post',
        data: {'jobId': jobId}
      }).then(({data}) => {
        if (data && data.code === 200) {
          this.$message({
            message: '执行任务成功',
            type: 'success'
          })
        } else {
          this.$message.error(data.msg)
        }
      })
    },
    handleStatusChange (row) {
      let text = row.status === '0' ? '启用' : '停用'
      let jobId = row.jobId
      let status = row.status
      let message = '确认要' + text + '【' + row.jobName + '】任务吗？'
      this.$confirm(message, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: this.$http.adornUrl('/v1/job/changeStatus'),
          method: 'post',
          data: {'jobId': jobId, 'status': status}
        }).then(({data}) => {
          if (data && data.code === 200) {
            this.$message({
              message: text + '任务成功',
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
        row.status = row.status === '0' ? '1' : '0'
      })
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
        url: this.$http.adornUrl('/v1/job/getPage'),
        method: 'post',
        data: this.$http.adornData({
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
    // 日期格式化
    parseTime (time, pattern) {
      if (arguments.length === 0 || !time) {
        return null
      }
      const format = pattern || '{y}-{m}-{d} {h}:{i}:{s}'
      let date
      if (typeof time === 'object') {
        date = time
      } else {
        if ((typeof time === 'string') && (/^[0-9]+$/.test(time))) {
          time = parseInt(time)
        } else if (typeof time === 'string') {
          time = time.replace(new RegExp(/-/gm), '/').replace('T', ' ').replace(new RegExp(/\.[\d]{3}/gm), '')
        }
        if ((typeof time === 'number') && (time.toString().length === 10)) {
          time = time * 1000
        }
        date = new Date(time)
      }
      const formatObj = {
        y: date.getFullYear(),
        m: date.getMonth() + 1,
        d: date.getDate(),
        h: date.getHours(),
        i: date.getMinutes(),
        s: date.getSeconds(),
        a: date.getDay()
      }
      const time_str = format.replace(/{(y|m|d|h|i|s|a)+}/g, (result, key) => {
        let value = formatObj[key]
        // Note: getDay() returns 0 on Sunday
        if (key === 'a') {
          return ['日', '一', '二', '三', '四', '五', '六'][value]
        }
        if (result.length > 0 && value < 10) {
          value = '0' + value
        }
        return value || 0
      })
      return time_str
    },
    // 删除
    deleteHandle (id) {
      var ids = id ? [id] : this.dataListSelections.map(item => {
        return item.id
      })
      this.$confirm(`请谨慎做删除操作，确定要删除吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: this.$http.adornUrl('/v1/job/delete'),
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
