<template>
  <el-dialog
    :title="!dataForm.id ? 'webhook配置新增' : 'webhook配置修改'"
    :close-on-click-modal="false"
    :visible.sync="visible"
    :before-close="cancel"
    width="30%"
    center>
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="120px">
      <el-form-item label="webHook地址" prop="webHookUrl">
        <el-input v-model="dataForm.webHookUrl" placeholder="webHook地址"></el-input>
      </el-form-item>
      <el-form-item label="秘钥" prop="webHookSecret">
        <el-input v-model="dataForm.webHookSecret" placeholder="秘钥"></el-input>
      </el-form-item>
      <el-form-item label="账户类型" prop="messageAccountType">
        <el-select v-model="dataForm.messageAccountType" placeholder="请选择账户类型" filterable clearable>
          <el-option
            v-for="item in messageAccountTypeList"
            :key="item.code"
            :label="item.name"
            :value="item.code"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input v-model="dataForm.description" placeholder="描述"></el-input>
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
      dataForm: {},
      messageAccountTypeList: [],
      dataRule: {
        webHookUrl: [
          {required: true, message: 'webHook地址不能为空', trigger: 'blur'}
        ],
        webHookSecret: [
          {required: true, message: '秘钥不能为空', trigger: 'blur'}
        ],
        messageAccountType: [
          {required: true, message: '账户类型不能为空', trigger: 'blur'}
        ],
        description: [
          {required: true, message: '描述不能为空', trigger: 'blur'}
        ]
      }
    }
  },
  created () {
  },
  methods: {
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
        let returnList = []
        list.forEach(function (item,index){
          if(item.type === 'webhook'){
            returnList.push(item)
          }
        })
        this.messageAccountTypeList = returnList
      })
    },
    getDataList () {
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
        if (this.dataForm.id) {
          this.$http({
            url: this.$http.adornUrl('/v1/messageConfigWebhook/getById'),
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
    init: function (id) {
      this.dataForm.id = id || 0
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
            url: this.$http.adornUrl(`/v1/messageConfigWebhook/${!this.dataForm.id ? 'save' : 'update'}`),
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
