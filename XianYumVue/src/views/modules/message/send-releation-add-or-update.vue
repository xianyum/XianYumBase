<template>
  <el-dialog
    :title="!dataForm.id ? '账户配置详情新增' : '账户配置详情修改'"
    :close-on-click-modal="false"
    :visible.sync="visible"
    :before-close="cancel"
    width="30%"
    center>
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="120px">
      <el-form-item label="账户类型" prop="messageAccountType" v-show="!this.dataForm.id">
        <el-select @change="messageAccountChange(dataForm.messageAccountType)" v-model="dataForm.messageAccountType" placeholder="请选择账户类型" filterable clearable>
          <el-option
            v-for="item in messageAccountTypeList"
            :key="item.code"
            :label="item.name"
            :value="item.code"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="发送账户" prop="messageConfigId" v-show="!this.dataForm.id">
        <el-select v-model="dataForm.messageConfigId" placeholder="请选择发送账户" filterable clearable>
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
        <el-input  type="textarea" :rows="2" v-model="dataForm.toUser" placeholder="消息发送人"></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="cancel" plain>取消</el-button>
      <el-button type="primary" plain @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
export default {
  data () {
    return {
      visible: false,
      dataForm: {
        messageConfigId: '',
        messageSendId: '',
        toUser: '',
        messageAccountType: ''
      },
      messageConfigList: [],
      messageAccountTypeList: [],
      dataRule: {
        messageConfigId: [
          {required: true, message: '发送账户不能为空', trigger: 'blur'}
        ],
        messageAccountType: [
          {required: true, message: '账户类型不能为空', trigger: 'blur'}
        ]
      }
    }
  },
  created () {
  },
  methods: {
    messageAccountChange(messageAccountType){
      this.$http({
        url: this.$http.adornUrl('/v1/messageSendRelation/getMessageConfigByAccountType'),
        method: 'post',
        data: this.$http.adornData({
          'messageAccountType': messageAccountType
        })
      }).then(({data}) => {
        this.messageConfigList = data.data
      })
    },
    cancel () {
      this.visible = false
      this.$refs['dataForm'].resetFields()
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
        this.messageAccountTypeList = list
      })
    },
    getDataList () {
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
        if (this.dataForm.id) {
          this.$http({
            url: this.$http.adornUrl('/v1/messageSendRelation/getById'),
            method: 'post',
            data: this.$http.adornData({
              'id': this.dataForm.id
            })
          }).then(({data}) => {
            if (data && data.code === 200) {
              this.dataForm = data.data
            }
          })
        }
      })
    },
    init: function (id,messageSendId) {
      this.dataForm.id = id || 0
      this.dataForm.messageSendId = messageSendId
      this.visible = true
      this.getMessageAccountTypeList()
      if (id) {
        this.getDataList()
      }
    },
    // 表单提交
    dataFormSubmit () {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.$http({
            url: this.$http.adornUrl(`/v1/messageSendRelation/${!this.dataForm.id ? 'save' : 'update'}`),
            method: 'post',
            data: this.dataForm
          }).then(({data}) => {
            if (data && data.code === 200) {
              this.$message({
                message: '操作成功',
                type: 'success',
                duration: 1500,
                onClose: () => {
                  this.visible = false
                  this.$emit('refreshDataList')
                }
              })
              this.$refs['dataForm'].resetFields()
            } else {
              this.$message.error(data.msg)
            }
          })
        }
      })
    }
  }
}
</script>
