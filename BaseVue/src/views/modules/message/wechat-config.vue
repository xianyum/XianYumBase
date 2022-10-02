<template>
  <div class="mod-user">
    <el-form :inline="true" :model="dataForm" ref="dataForm" @keyup.enter.native="getDataList()">
      <el-form-item prop="description">
        <el-input v-model="dataForm.description" placeholder="描述" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-button plain @click="getDataList()" round>查询</el-button>
        <el-button type="success" @click="addOrUpdateHandle()" round>新增</el-button>
        <el-button type="danger"  @click="deleteHandle()" :disabled="dataListSelections.length <= 0" round>批量删除</el-button>
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
        prop="description"
        header-align="center"
        align="center"
        label="描述">
      </el-table-column>
      <el-table-column
        prop="corpId"
        header-align="center"
        align="center"
        label="企业id">
      </el-table-column>
      <el-table-column
        prop="corpSecret"
        header-align="center"
        align="center"
        label="应用秘钥">
      </el-table-column>
      <el-table-column
        prop="agentId"
        header-align="center"
        align="center"
        label="应用id">
      </el-table-column>
      <el-table-column
        prop="createTime"
        header-align="center"
        align="center"
        :formatter="formatTime"
        width="160px"
        label="创建日期">
      </el-table-column>
      <el-table-column
        fixed="right"
        header-align="center"
        align="center"
        width="150"
        label="操作">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="testSendWechat(scope.row)">测试</el-button>
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

    <el-drawer
      title="微信账户配置测试"
      :visible.sync="drawer"
      :direction="direction">
      <el-form :model="wechatForm" :rules="wechatFormRule" ref="wechatForm"
               @keyup.enter.native="sendWechat()" label-width="120px" style="width: 85%">
        <el-form-item label="消息编码" prop="messageCode">
          <el-input v-model="wechatForm.messageCode" placeholder="消息编码"></el-input>
        </el-form-item>
        <el-form-item label="发送标题" prop="title">
          <el-input v-model="wechatForm.title" placeholder="发送标题"></el-input>
        </el-form-item>
        <el-form-item label="发送内容" prop="content">
          <el-input type="textarea" :rows="5" v-model="wechatForm.content" placeholder="发送内容"></el-input>
        </el-form-item>
        <el-form-item label="发送用户" prop="wechatToUser">
          <el-input v-model="wechatForm.wechatToUser" placeholder="发送用户"></el-input>
        </el-form-item>
      </el-form>

      <div style="position: absolute;bottom: 0;width: 100%;height: 100px;clear:both;">
        <el-divider></el-divider>
        <div style="float: right;padding-right:20px">
          <el-button type="primary" plain @click="sendWechat()">测试</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import AddOrUpdate from './wechat-config-add-or-update'

export default {
  data () {
    return {
      drawer: false,
      direction: 'rtl',
      dataForm: {
        name: ''
      },
      wechatForm: {
        wechatToUser: '',
        title: '',
        content: '',
        messageConfigId: '',
        messageCode: 'SYSTEM_TEST_NOTIFY'
      },
      reset () {
        this.$refs['dataForm'].resetFields()
        this.pageIndex = 1
        this.pageSize = 10
        this.getDataList()
      },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false,
      wechatFormRule: {
        title: [
          {required: true, message: '发送标题不能为空', trigger: 'blur'}
        ],
        content: [
          {required: true, message: '发送内容不能为空', trigger: 'blur'}
        ],
        wechatToUser: [
          {required: true, message: '发送用户不能为空', trigger: 'blur'}
        ],
        messageCode: [
          {required: true, message: '消息编码不能为空', trigger: 'blur'}
        ]
      }
    }
  },
  activated () {
    this.getDataList()
  },
  components: {
    AddOrUpdate
  },
  methods: {
    sendWechat () {
      let _this = this
      this.$refs['wechatForm'].validate((valid) => {
        if (valid) {
          this.$http({
            url: this.$http.adornUrl(`/v1/messageConfigWechat/sendWechat`),
            method: 'post',
            data: _this.wechatForm
          }).then(({data}) => {
            if (data && data.code === 200) {
              this.$message({
                message: '执行成功，请看收信',
                type: 'success',
                duration: 1500
              })
            } else {
              this.$message.error(data.msg)
            }
          })
        }
      })
    },
    testSendWechat(row){
      this.$nextTick(() => {
        this.$refs['wechatForm'].resetFields()
        this.$refs['wechatForm'].clearValidate()
      })
      this.wechatForm.messageCode = 'SYSTEM_TEST_NOTIFY'
      this.drawer = true
      this.wechatForm.messageConfigId = row.id
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
        url: this.$http.adornUrl('/v1/messageConfigWechat/getPage'),
        method: 'post',
        data: this.$http.adornData({
          'description': this.dataForm.description,
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
      this.$confirm(`请谨慎做删除操作，确定要删除吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: this.$http.adornUrl('/v1/messageConfigWechat/delete'),
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
