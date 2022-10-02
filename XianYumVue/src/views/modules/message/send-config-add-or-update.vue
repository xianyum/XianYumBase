<template>
  <div>
    <el-card style="width:100%; margin-top:10px; clear:both;">
      <div slot="header" class="clearfix">
        <span>{{ sendConfigTitle }}</span>
        <el-button style="float: right;" size="mini" type="primary" @click="saveOrUpdateSendConfig">
          {{ saveOrUpdateTitle }}
        </el-button>
      </div>
      <div>
        <el-form :inline="true" :model="dataForm" :rules="dataFormRule" ref="dataForm" label-width="80px">
          <el-form-item label="消息类型" prop="messageCode" style="width: 30%">
            <el-select v-model="dataForm.messageCode" placeholder="请选择消息类型配置" filterable>
              <el-option
                v-for="item in messageTypeList"
                :key="item.messageCode"
                :label="`${item.messageCode}---${item.description}`"
                :value="item.messageCode">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="消息标题" prop="messageTitle" style="width: 30%">
            <el-input v-model="dataForm.messageTitle" placeholder="消息标题" style="width: 300px"></el-input>
          </el-form-item>
          <el-form-item label="启用状态" prop="status">
            <el-switch
              v-model="dataForm.status"
              active-color="#13ce66"
              inactive-color="#ff4949"
              active-text="启用"
              inactive-text="禁用"
              :active-value=0
              :inactive-value=1>
            </el-switch>
          </el-form-item>
          <el-form-item label="限制时间" prop="time">
            <el-time-picker
              is-range
              v-model="dataForm.time"
              range-separator="至"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              placeholder="选择时间范围">
            </el-time-picker>
          </el-form-item>
        </el-form>
      </div>
      <div>
        <el-divider>账户配置详情</el-divider>
        <el-form :inline="true" :model="messageRelationForm" ref="messageRelationForm"
                 @keyup.enter.native="getMessageRelationList()">
          <el-form-item prop="messageAccountType">
            <el-select v-model="messageRelationForm.messageAccountType" placeholder="请选择消息账户类型" filterable clearable>
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
            <el-button plain @click="getMessageRelationList()" round>查询</el-button>
            <el-button type="success" @click="addOrUpdateMR()" round>新增</el-button>
          </el-form-item>
        </el-form>

        <el-table
          :data="dataList"
          border
          stripe
          v-loading="dataListLoading"
          style="width: 100%">
          <el-table-column
            label="序号"
            type="index"
            align="center"
            width="50">
          </el-table-column>
          <el-table-column
            prop="messageConfigDescription"
            header-align="center"
            align="center"
            label="账户配置描述">
          </el-table-column>
          <el-table-column
            prop="messageAccountType"
            header-align="center"
            align="center"
            label="消息账户类型"
            :formatter="formatMessageAccountType">
          </el-table-column>
          <el-table-column
            prop="toUser"
            header-align="center"
            align="center"
            label="发送用户">
          </el-table-column>
          <el-table-column
            fixed="right"
            header-align="center"
            align="center"
            width="160"
            label="操作">
            <template slot-scope="scope">
              <el-button type="text" size="small" @click="querymessageConfig(scope.row)">查看账户</el-button>
              <el-button type="text" size="small" @click="addOrUpdateMR(scope.row.id)">修改</el-button>
              <el-button type="text" size="small" @click="deleteMessageRelation(scope.row.id)">删除</el-button>
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
    </el-card>

    <el-dialog
      :title="messageConfigDialog"
      :close-on-click-modal="false"
      :visible.sync="messageConfigVisible"
      width="30%"
      center>
      <div v-show="messageConfigDialogForm.messageAccountType === 'wechat'">
        <el-divider>微信配置详情</el-divider>
        <span>企业id: {{ messageConfigDialogForm.corpId }}</span>
        <el-divider></el-divider>
        <span>应用秘钥: {{ messageConfigDialogForm.corpSecret }}</span>
        <el-divider></el-divider>
        <span>应用id: {{ messageConfigDialogForm.agentId }}</span>
        <el-divider></el-divider>
        <span>描述: {{ messageConfigDialogForm.description }}</span>
      </div>
      <div v-show="messageConfigDialogForm.messageAccountType === 'email'">
        <el-divider>邮箱配置详情</el-divider>
        <span>smtp地址: {{ messageConfigDialogForm.emailSmtp }}</span>
        <el-divider></el-divider>
        <span>邮箱账号: {{ messageConfigDialogForm.emailUserName }}</span>
        <el-divider></el-divider>
        <span>邮箱密码: {{ messageConfigDialogForm.emailUserPassword }}</span>
        <el-divider></el-divider>
        <span>描述: {{ messageConfigDialogForm.description }}</span>
      </div>
      <div v-show="messageConfigDialogForm.messageAccountType.includes('webhook')">
        <el-divider>webhook配置详情</el-divider>
        <span>webHook地址: {{ messageConfigDialogForm.webHookUrl }}</span>
        <el-divider></el-divider>
        <span>秘钥: {{ messageConfigDialogForm.webHookSecret }}</span>
        <el-divider></el-divider>
        <span>消息类型: {{ messageConfigDialogForm.messageAccountType }}</span>
        <el-divider></el-divider>
        <span>描述: {{ messageConfigDialogForm.description }}</span>
      </div>
    </el-dialog>

    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate"
                   @refreshDataList="getMessageRelationList"></add-or-update>
  </div>
</template>

<script>
import addOrUpdate from './send-releation-add-or-update'

export default {
  data() {
    return {
      messageConfigDialogForm: {
        messageAccountType: ''
      },
      messageConfigVisible: false,
      addOrUpdateVisible: false,
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataList: [],
      messageConfigDialog: '',
      dataListLoading: false,
      messageAccountTypeList: [],
      messageTypeList: [],
      sendConfigTitle: '',
      messageConfigTitle: '',
      saveOrUpdateTitle: '',
      messageRelationForm: {
        messageAccountType: '',
        messageSendId: '',
        messageConfigId: '',
        toUser: ''
      },
      dataForm: {
        id: '',
        status: 0,
        messageCode: '',
        messageTitle: '',
        time: [],
        limitSendStartTime: '',
        limitSendEndTime: ''
      },
      dataFormRule: {
        messageCode: [
          {required: true, message: '消息编码不能为空', trigger: 'change'}
        ],
        messageTitle: [
          {required: true, message: '消息标题不能为空', trigger: 'change'}
        ],
        status: [
          {required: true, message: '启用状态不能为空', trigger: 'blur'}
        ]
      }
    }
  },
  activated() {
    this.getMessageTypeList()
    this.getMessageAccountTypeList()
    let id = this.$route.params.id
    this.dataForm.id = id
    if (id) {
      this.sendConfigTitle = '发送配置更新'
      this.saveOrUpdateTitle = '更新数据'
      this.getSendConfigDetail(id)
      this.getMessageRelationList()
    } else {
      this.sendConfigTitle = '发送配置保存'
      this.saveOrUpdateTitle = '保存数据'
    }
  },
  components: {
    addOrUpdate
  },
  methods: {
    addOrUpdateMR(id) {
      let messageSendId = this.dataForm.id
      if (!messageSendId) {
        this.$message.error('需要先配置消息发送配置')
        return;
      }
      this.addOrUpdateVisible = true
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id, messageSendId)
      })
    },
    getMessageAccountTypeList() {
      this.$http({
        url: this.$http.adornUrl('/systemConstant/getPrivateConstant'),
        method: 'post',
        data: this.$http.adornData({
          'constantKey': 'message_account_type'
        })
      }).then(({data}) => {
        this.messageAccountTypeList = JSON.parse(data.data.constantValue)
      })
    },
    formatMessageAccountType(row, column, cell) {
      if (!cell) {
        return ''
      }
      let name = cell;
      this.messageAccountTypeList.filter(function (item) {
        if (item.code === cell) {
          name = item.name
        }
      })
      return name
    },
    // 每页数
    sizeChangeHandle(val) {
      this.pageSize = val
      this.pageIndex = 1
      this.getMessageRelationList()
    },
    // 当前页
    currentChangeHandle(val) {
      this.pageIndex = val
      this.getMessageRelationList()
    },
    getMessageTypeList() {
      this.$http({
        url: this.$http.adornUrl('/v1/messageTypeConfig/getList'),
        method: 'post',
        data: this.$http.adornData({})
      }).then(({data}) => {
        if (data && data.code === 200) {
          this.messageTypeList = data.data
        }
      })
    },
    getSendConfigDetail(id) {
      let _this = this
      _this.$http({
        url: _this.$http.adornUrl('/v1/messageSendConfig/getById'),
        method: 'post',
        data: _this.$http.adornData({
          'id': id
        })
      }).then(({data}) => {
        if (data && data.code === 200) {
          let respData = data.data
          _this.dataForm = respData
          if(respData.limitSendStartTime || respData.limitSendEndTime){
            let bindTime = [new Date(respData.limitSendStartTime),new Date(respData.limitSendEndTime)]
            this.$set(_this.dataForm, "time" ,bindTime)
          }
        }
      })
    },
    saveOrUpdateSendConfig() {
      let _this = this
      console.log(_this.dataForm.time)
      if(_this.dataForm.time){
        _this.dataForm.limitSendStartTime = this.dataForm.time[0]
        _this.dataForm.limitSendEndTime = this.dataForm.time[1]
      }else{
        _this.dataForm.limitSendStartTime = null
        _this.dataForm.limitSendEndTime = null
      }

      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.$http({
            url: _this.$http.adornUrl(`/v1/messageSendConfig/saveOrUpdate`),
            method: 'post',
            data: _this.dataForm
          }).then(({data}) => {
            if (data && data.code === 200) {
              _this.$message({
                message: '操作成功',
                type: 'success',
                duration: 1500
              })
              _this.dataForm = data.data
              if(data.data.limitSendStartTime || data.data.limitSendEndTime){
                let bindTime = [new Date(data.data.limitSendStartTime),new Date(data.data.limitSendEndTime)]
                _this.$set(_this.dataForm, "time" ,bindTime)
              }else{
                _this.$set(_this.dataForm, "time" ,null)
              }
            } else {
              _this.$message.error(data.msg)
            }
          })
        }
      })
    },
    getMessageRelationList() {
      this.dataListLoading = true
      this.$http({
        url: this.$http.adornUrl('/v1/messageSendRelation/getPage'),
        method: 'post',
        data: this.$http.adornData({
          'messageSendId': this.dataForm.id,
          'messageAccountType': this.messageRelationForm.messageAccountType,
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
    deleteMessageRelation(id) {
      this.$confirm(`请谨慎做删除操作，确定要删除吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: this.$http.adornUrl('/v1/messageSendRelation/delete'),
          method: 'post',
          data: this.$http.adornData({
            'id': id
          })
        }).then(({data}) => {
          if (data && data.code === 200) {
            this.$message({
              message: '删除成功',
              type: 'success',
              duration: 1500,
              onClose: () => {
                this.getMessageRelationList()
              }
            })
          } else {
            this.$message.error(data.msg)
          }
        })
      }).catch(() => {
      })
    },
    querymessageConfig(row) {
      this.messageConfigDialog = row.messageConfigDescription
      let _this = this
      this.$http({
        url: this.$http.adornUrl('/v1/messageSendRelation/getMessageConfigByAccountType'),
        method: 'post',
        data: this.$http.adornData({
          'messageConfigId': row.messageConfigId,
          'messageAccountType': row.messageAccountType
        })
      }).then(({data}) => {
        if (data && data.code === 200) {
          _this.messageConfigDialogForm = data.data[0]
          _this.messageConfigDialogForm.messageAccountType = row.messageAccountType
          _this.messageConfigVisible = true
        } else {
          this.$message.error('未查询出配置')
        }
      })
    }
  }
}
</script>
