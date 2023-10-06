<template>
  <div class="app-container">
    <el-card style="width:100%; margin-top:10px; clear:both;">
      <div slot="header" class="clearfix">
        <span>{{ sendConfigTitle }}</span>
        <el-button style="float: right;" icon="el-icon-document-checked" size="mini" type="primary" @click="saveOrUpdateSendConfig">
          {{ saveOrUpdateTitle }}
        </el-button>
      </div>
      <div>
        <el-form :inline="true" :model="dataForm" :rules="dataFormRule" ref="dataForm" label-width="80px">
          <el-form-item label="消息类型" prop="messageCode" style="width: 30%">
            <el-select v-model="dataForm.messageCode" placeholder="请选择消息类型配置" filterable :disabled="dataForm.id">
              <el-option
                v-for="item in messageTypeList"
                :key="item.messageCode"
                :label="`${item.messageCode}---${item.description}`"
                :value="item.messageCode"
              >
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
              :inactive-value=1
            >
            </el-switch>
          </el-form-item>
          <el-form-item label="限制时间" prop="time">
            <el-time-picker
              is-range
              v-model="dataForm.time"
              range-separator="至"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              placeholder="选择时间范围"
            >
            </el-time-picker>
          </el-form-item>
        </el-form>
      </div>
      <div>
        <el-divider>账户配置详情</el-divider>
        <el-form :inline="true" :model="messageRelationForm" ref="messageRelationForm"
                 @keyup.enter.native="getMessageRelationList"
        >
          <el-form-item prop="messageAccountType">
            <el-select v-model="messageRelationForm.messageAccountType" placeholder="请选择消息账户类型" filterable
                       clearable
            >
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
            <el-button type="primary" icon="el-icon-search" size="mini"  @click="getMessageRelationList" >查询</el-button>
            <el-button type="success" icon="el-icon-plus" size="mini"  @click="addOrUpdateMR(undefined)" >新增</el-button>
          </el-form-item>
        </el-form>

        <el-table
          :data="dataList"
          border
          stripe
          v-loading="dataListLoading"
          style="width: 100%"
        >
          <el-table-column
            label="序号"
            type="index"
            align="center"
            width="50"
          >
          </el-table-column>
          <el-table-column
            prop="messageConfigDescription"
            header-align="center"
            align="center"
            label="账户配置描述"
          >
          </el-table-column>
          <el-table-column
            prop="messageAccountType"
            header-align="center"
            align="center"
            label="消息账户类型"
            :formatter="formatMessageAccountType"
          >
          </el-table-column>
          <el-table-column
            prop="toUser"
            header-align="center"
            align="center"
            label="发送用户"
          >
          </el-table-column>
          <el-table-column
            fixed="right"
            header-align="center"
            align="center"
            width="200"
            label="操作"
          >
            <template v-slot="scope">
              <el-button icon="el-icon-view" type="text" size="mini" @click="queryMessageConfig(scope.row)">查看账户</el-button>
              <el-button icon="el-icon-edit" type="text" size="mini" @click="addOrUpdateMR(scope.row.id)">修改</el-button>
              <el-button icon="el-icon-delete" type="text" size="mini" @click="deleteMessageRelation(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <pagination
          v-show="totalPage>0"
          :total="totalPage"
          :page.sync="pageIndex"
          :limit.sync="pageSize"
          @pagination="getMessageRelationList"
        />
      </div>
    </el-card>


    <el-dialog
      :title="messageConfigDialog"
      :close-on-click-modal="false"
      :visible.sync="messageConfigVisible"
      width="30%"
      center
    >
      <div v-show="messageConfigDialogForm.messageAccountType === 'wechat'">
        <el-divider>微信配置详情</el-divider>
        <span>企业id: {{ messageConfigDialogForm.corpId }}</span>
        <el-divider></el-divider>
        <span>应用秘钥: {{ messageConfigDialogForm.corpSecret }}</span>
        <el-divider></el-divider>
        <span>应用id: {{ messageConfigDialogForm.agentId }}</span>
        <el-divider></el-divider>
        <span>配置描述: {{ messageConfigDialogForm.description }}</span>
      </div>
      <div v-show="messageConfigDialogForm.messageAccountType === 'email'">
        <el-divider>邮箱配置详情</el-divider>
        <span>smtp地址: {{ messageConfigDialogForm.emailSmtp }}</span>
        <el-divider></el-divider>
        <span>邮箱账号: {{ messageConfigDialogForm.emailUserName }}</span>
        <el-divider></el-divider>
        <span>邮箱密码: {{ messageConfigDialogForm.emailUserPassword }}</span>
        <el-divider></el-divider>
        <span>配置描述: {{ messageConfigDialogForm.description }}</span>
      </div>
      <div v-show="messageConfigDialogForm.messageAccountType.includes('webhook')">
        <el-divider>webhook配置详情</el-divider>
        <span>webHook地址: {{ messageConfigDialogForm.webHookUrl }}</span>
        <el-divider></el-divider>
        <span>秘钥: {{ messageConfigDialogForm.webHookSecret }}</span>
        <el-divider></el-divider>
        <span>消息类型: {{ messageConfigDialogForm.messageAccountType }}</span>
        <el-divider></el-divider>
        <span>配置描述: {{ messageConfigDialogForm.description }}</span>
      </div>
    </el-dialog>





    <!-- 添加或修改客户端管理对话框 -->
    <el-dialog
      :title="!messageRelationData.id ? '账户配置详情新增' : '账户配置详情修改'"
      :visible.sync="open" width="500px" append-to-body>
      <el-form ref="messageRelationData" :model="messageRelationData" :rules="messageRelationDataRules" label-width="95px">
        <el-form-item label="账户类型" prop="messageAccountType" v-show="!this.messageRelationData.id">
          <el-select @change="messageAccountChange(messageRelationData.messageAccountType)" v-model="messageRelationData.messageAccountType" placeholder="请选择账户类型" filterable clearable>
            <el-option
              v-for="item in messageAccountTypeList"
              :key="item.code"
              :label="item.name"
              :value="item.code"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="发送账户" prop="messageConfigId" v-show="!this.messageRelationData.id">
          <el-select v-model="messageRelationData.messageConfigId" placeholder="请选择发送账户" filterable clearable>
            <el-option
              v-for="item in messageConfigList"
              :key="item.id"
              :label="item.description"
              :value="item.id"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="消息发送人" prop="toUser">
          <el-input  type="textarea" :rows="2" v-model="messageRelationData.toUser" placeholder="消息发送人"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitMessageRelationData">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>

import { getMessageTypeList } from '@/api/message/messageTypeConfig'
import {
  addMessageSendRelationConfig,
  deleteMessageRelationById,
  getMessageConfigByAccountType, getMessageRelationById,
  getMessageSendConfigById,
  getMessageSendRelationPage,
  saveOrUpdateSendConfig, updateMessageSendRelationConfig
} from '@/api/message/messageSendConfig'

export default {
  name: 'sendConfigAddOrUpdate',

  data() {
    return {
      open: false,
      messageRelationData: {},
      messageConfigDialogForm: {
        messageAccountType: ''
      },
      messageConfigVisible: false,
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
      messageConfigList: [],
      dataForm: {
        id: '',
        status: 0,
        messageCode: '',
        messageTitle: '',
        time: [],
        limitSendStartTime: '',
        limitSendEndTime: ''
      },
      messageRelationDataRules:{
        messageConfigId: [
          {required: true, message: '发送账户不能为空', trigger: 'blur'}
        ],
        messageAccountType: [
          {required: true, message: '账户类型不能为空', trigger: 'blur'}
        ]
      },
      dataFormRule: {
        messageCode: [
          { required: true, message: '消息编码不能为空', trigger: 'change' }
        ],
        messageTitle: [
          { required: true, message: '消息标题不能为空', trigger: 'change' }
        ],
        status: [
          { required: true, message: '启用状态不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getMessageTypeList()
    this.getMessageAccountTypeList()
    this.dataForm.id = this.$route.params.id
    if (this.dataForm.id) {
      this.sendConfigTitle = '发送配置更新'
      this.saveOrUpdateTitle = '更新数据'
      this.getSendConfigDetail(this.dataForm.id)
      this.getMessageRelationList()
    } else {
      this.sendConfigTitle = '发送配置保存'
      this.saveOrUpdateTitle = '保存数据'
    }
  },
  methods: {
    messageAccountChange(messageAccountType){
      let requestForm = {
        'messageAccountType': messageAccountType
      }
      getMessageConfigByAccountType(requestForm).then(res => {
        this.messageConfigList = res.data
      });
    },
    submitMessageRelationData(){
      this.$refs["messageRelationData"].validate(valid => {
        if (valid) {
          if (this.messageRelationData.id != null) {
            updateMessageSendRelationConfig(this.messageRelationData).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getMessageRelationList();
            });
          } else {
            addMessageSendRelationConfig(this.messageRelationData).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getMessageRelationList();
            });
          }
        }
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.resetMessageRelation();
    },
    // 表单重置
    resetMessageRelation() {
      this.messageRelationData = {
        id: undefined,
        messageAccountType: undefined,
        messageConfigId: undefined,
        toUser: undefined,
        messageSendId: undefined
      };
      this.resetForm("messageRelationData");
    },
    addOrUpdateMR(id) {
      let messageSendId = this.dataForm.id
      if (!messageSendId) {
        this.$modal.msgError('需要先配置消息发送配置')
        return
      }
      this.resetMessageRelation()
      if(id){
        getMessageRelationById(id).then(res => {
          this.messageRelationData = res.data
        });
      }
      this.messageRelationData.messageSendId = messageSendId
      this.open = true
    },
    saveOrUpdateSendConfig() {
      if (this.dataForm.time) {
        this.dataForm.limitSendStartTime = this.dataForm.time[0]
        this.dataForm.limitSendEndTime = this.dataForm.time[1]
      } else {
        this.dataForm.limitSendStartTime = null
        this.dataForm.limitSendEndTime = null
      }
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          saveOrUpdateSendConfig(this.dataForm).then(res => {
            this.dataForm = res.data
            if (res.data.limitSendStartTime || res.data.limitSendEndTime) {
              let bindTime = [new Date(res.data.limitSendStartTime), new Date(res.data.limitSendEndTime)]
              this.$set(this.dataForm, 'time', bindTime)
            } else {
              this.$set(this.dataForm, 'time', null)
            }
            this.$modal.msgSuccess('操作成功')
          })
        }
      })
    },
    formatMessageAccountType(row, column, cell) {
      if (!cell) {
        return ''
      }
      let name = cell
      this.messageAccountTypeList.filter(function(item) {
        if (item.code === cell) {
          name = item.name
        }
      })
      return name
    },
    getMessageTypeList() {
      getMessageTypeList().then(response => {
        this.messageTypeList = response.data
      })
    },
    getMessageAccountTypeList() {
      this.getPublicSystemConstant('message_account_type').then(res => {
        this.messageAccountTypeList = JSON.parse(res.data.constantValue)
      })
    },
    getSendConfigDetail(id) {
      getMessageSendConfigById(id).then(res => {
        this.dataForm = res.data
        if (this.dataForm.limitSendStartTime || this.dataForm.limitSendEndTime) {
          let bindTime = [new Date(this.dataForm.limitSendStartTime), new Date(this.dataForm.limitSendEndTime)]
          this.$set(this.dataForm, 'time', bindTime)
        }
      })
    },
    getMessageRelationList() {
      this.dataListLoading = true
      let requestForm = {
        'messageSendId': this.dataForm.id,
        'messageAccountType': this.messageRelationForm.messageAccountType,
        'pageNum': this.pageIndex,
        'pageSize': this.pageSize
      }
      getMessageSendRelationPage(requestForm).then(res => {
        this.dataList = res.data.records
        this.totalPage = res.data.total
        this.dataListLoading = false
      })
    },
    queryMessageConfig(row) {
      let requestForm = {
        'messageConfigId': row.messageConfigId,
        'messageAccountType': row.messageAccountType
      }
      this.messageConfigDialog = row.messageConfigDescription
      getMessageConfigByAccountType(requestForm).then(res => {
        this.messageConfigDialogForm = res.data[0]
        this.messageConfigDialogForm.messageAccountType = row.messageAccountType
        this.messageConfigVisible = true
      })
    },
    deleteMessageRelation(id) {
      this.$modal.confirm('是否确认删除账户配置关系数据？').then(function() {
        return deleteMessageRelationById(id)
      }).then(() => {
        this.getMessageRelationList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {
      })
    }
  }
}
</script>
