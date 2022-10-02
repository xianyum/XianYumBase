<template>
  <div class="mod-user">
    <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">
      <el-form-item>
        <el-input v-model="dataForm.employeeName" placeholder="员工名称" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-input v-model="dataForm.loginName" placeholder="账号" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-button @click="getDataList()" round>查询</el-button>
        <el-button type="success" @click="addOrUpdateHandle()" round>新增</el-button>
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
        prop="loginName"
        header-align="center"
        align="center"
        label="账号"
        sortable>
      </el-table-column>
      <el-table-column
        prop="employeeNum"
        header-align="center"
        align="center"
        label="员工编码">
      </el-table-column>
      <el-table-column
        prop="employeeName"
        header-align="center"
        align="center"
        label="员工名称">
      </el-table-column>
      <el-table-column
        prop="projectName"
        header-align="center"
        align="center"
        label="项目名称">
      </el-table-column>
      <el-table-column
        prop="projectManagerName"
        header-align="center"
        align="center"
        label="项目经理">
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
          <el-button type="text" size="small" @click="view(scope.row.loginName)">查看</el-button>
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

    <el-dialog
      title="详情"
      :visible.sync="dialogVisible"
      width="60%"
      center>
        <el-calendar :first-day-of-week="7" v-model="month">
          <template slot="dateCell" slot-scope="{ data }">
            <p>{{ data.day.split("-").slice(2).join("-") }}<br/></p>
            <div
              v-for="(item, index) in calendarData"
              :key="index">
              <div v-if="data.day == item.dateTime">
                <div v-if="!item.approvalStatus && new Date().getTime() >= new Date(data.day).getTime()" style="color: #f73131" >缺勤</div>
                <div v-if="item.approvalStatus === 1" style="color: #d51598" >拒绝</div>
                <div v-if="item.approvalStatus === 2" style="color: #38d958" >通过</div>
                <div v-if="item.approvalStatus === 3" style="color: #2597e7" >已提交</div>
                <div v-if="item.approvalStatus === 4" style="color: rgba(14,163,197,0.34)" >节假日</div>
              </div>
            </div>
          </template>
        </el-calendar>
    </el-dialog>
  </div>
</template>

<script>
import AddOrUpdate from './person-add-or-update'

export default {
  data () {
    return {
      month: new Date(),
      calendarData: [],
      dialogVisible: false,
      dataForm: {
        employeeName: '',
        loginName: ''
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
    getZeekrByMonth(loginName){
      this.$http({
        url: this.$http.adornUrl('/v1/zeekrPerson/getZeekrByMonth'),
        method: 'post',
        data: this.$http.adornData({
          'loginName': loginName
        })
      }).then(({data}) => {
        if (data && data.code === 200) {
          this.calendarData = data.data
        }
      })
    },
    view(loginName){
      this.dialogVisible = true
      this.getZeekrByMonth(loginName)
    },
    reset () {
      this.dataForm.employeeName = ''
      this.dataForm.loginName = ''
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
        url: this.$http.adornUrl('/v1/zeekrPerson/getPage'),
        method: 'post',
        data: this.$http.adornData({
          'employeeName': this.dataForm.employeeName,
          'loginName': this.dataForm.loginName,
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
          url: this.$http.adornUrl('/v1/zeekrPerson/delete'),
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
