<template>
  <div class="mod-user">
    <el-form :inline="true" :model="dataForm" ref="dataForm" @keyup.enter.native="getDataList()">
      <el-form-item prop="description">
        <el-input v-model="dataForm.description" placeholder="描述" clearable></el-input>
      </el-form-item>
      <el-form-item prop="messageAccountType">
        <el-select v-model="dataForm.messageAccountType" placeholder="请选择消息账户类型" filterable clearable>
          <el-option
            v-for="item in messageAccountTypeList"
            :key="item.code"
            :label="item.name"
            :value="item.code"
          >
          </el-option>
        </el-select>
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
        prop="messageAccountType"
        header-align="center"
        align="center"
        label="账户类型"
        :formatter="formatMessageAccountType">
      </el-table-column>
      <el-table-column
        prop="webHookUrl"
        header-align="center"
        align="center"
        label="webHook地址">
      </el-table-column>
      <el-table-column
        prop="webHookSecret"
        header-align="center"
        align="center"
        label="秘钥">
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
          <el-button type="text" size="small" @click="testSendWbhook(scope.row)">测试</el-button>
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
      title="webhook账户配置测试"
      :visible.sync="drawer"
      :direction="direction">
      <el-form :model="webhookForm" :rules="webhookFormRule" ref="webhookForm"
               @keyup.enter.native="sendWebhook()" label-width="120px" style="width: 85%">
        <el-form-item label="消息编码" prop="messageCode">
          <el-input v-model="webhookForm.messageCode" placeholder="消息编码"></el-input>
        </el-form-item>
        <el-form-item label="发送标题" prop="title">
          <el-input v-model="webhookForm.title" placeholder="发送标题"></el-input>
        </el-form-item>
        <el-form-item label="发送内容" prop="content">
          <el-input type="textarea" :rows="5" v-model="webhookForm.content" placeholder="发送内容"></el-input>
        </el-form-item>
      </el-form>

      <div style="position: absolute;bottom: 0;width: 100%;height: 100px;clear:both;">
        <el-divider></el-divider>
        <div style="float: right;padding-right:20px">
          <el-button type="primary" plain @click="sendWebhook()">测试</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import AddOrUpdate from './webhook-config-add-or-update'

export default {
  data () {
    return {
      drawer: false,
      direction: 'rtl',
      webhookForm: {
        title: '',
        content: '',
        messageCode: 'SYSTEM_TEST_NOTIFY'
      },
      dataForm: {
        description: ''
      },
      reset () {
        this.$refs['dataForm'].resetFields()
        this.pageIndex = 1
        this.pageSize = 10
        this.getDataList()
      },
      dataList: [],
      pageIndex: 1,
      messageAccountTypeList: [],
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false,
      webhookFormRule: {
        title: [
          {required: true, message: '发送标题不能为空', trigger: 'blur'}
        ],
        content: [
          {required: true, message: '发送内容不能为空', trigger: 'blur'}
        ],
        messageCode: [
          {required: true, message: '消息编码不能为空', trigger: 'blur'}
        ]
      }
    }
  },
  activated () {
    this.getMessageAccountTypeList()
    this.getDataList()
  },
  components: {
    AddOrUpdate
  },
  methods: {
    sendWebhook () {
      this.$refs['webhookForm'].validate((valid) => {
        if (valid) {
          this.$http({
            url: this.$http.adornUrl(`/v1/messageConfigWebhook/sendWebhook`),
            method: 'post',
            data: this.webhookForm
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
    testSendWbhook(row){
      this.$nextTick(() => {
        this.$refs['webhookForm'].resetFields()
        this.$refs['webhookForm'].clearValidate()
      })
      this.webhookForm.messageCode = 'SYSTEM_TEST_NOTIFY'
      this.drawer = true
      this.webhookForm.messageConfigId = row.id
      this.webhookForm.messageAccountType = row.messageAccountType
    },
    getMessageAccountTypeList(){
      this.$http({
        url: this.$http.adornUrl('/systemConstant/getPrivateConstant'),
        method: 'post',
        data: this.$http.adornData({
          'constantKey': 'message_account_type'
        })
      }).then(({data}) => {
        let list = JSON.parse(data.data.constantValue)
        let returnList = []
        list.forEach(function (item,index){
          if(item.type === 'webhook'){
            returnList.push(item)
          }
        })
        this.messageAccountTypeList = returnList

      })
    },
    formatMessageAccountType(row, column, cell){
      if (!cell) {
        return ''
      }
      let name = cell;
      this.messageAccountTypeList.filter(function (item){
        if(item.code === cell){
          name = item.name
        }
      })
      return name
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
        url: this.$http.adornUrl('/v1/messageConfigWebhook/getPage'),
        method: 'post',
        data: this.$http.adornData({
          'messageAccountType': this.dataForm.messageAccountType,
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
          url: this.$http.adornUrl('/v1/messageConfigWebhook/delete'),
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
