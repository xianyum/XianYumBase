<template>
  <el-dialog
    :title="!dataForm.id ? '邮箱账户配置新增' : '邮箱账户配置修改'"
    :close-on-click-modal="false"
    :visible.sync="visible"
    :before-close="cancel"
    width="30%"
    center>
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="120px">
      <el-form-item label="smtp地址" prop="emailSmtp">
        <el-input v-model="dataForm.emailSmtp" placeholder="smtp地址"></el-input>
      </el-form-item>
      <el-form-item label="邮箱账号" prop="emailUserName">
        <el-input v-model="dataForm.emailUserName" placeholder="邮箱账号"></el-input>
      </el-form-item>
      <el-form-item label="邮箱密码" prop="emailUserPassword">
        <el-input v-model="dataForm.emailUserPassword" placeholder="邮箱密码"></el-input>
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
      dataRule: {
        emailSmtp: [
          {required: true, message: 'smtp地址不能为空', trigger: 'blur'}
        ],
        emailUserName: [
          {required: true, message: '邮箱账号不能为空', trigger: 'blur'}
        ],
        emailUserPassword: [
          {required: true, message: '邮箱密码不能为空', trigger: 'blur'}
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
    getDataList () {
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
        if (this.dataForm.id) {
          this.$http({
            url: this.$http.adornUrl('/v1/messageConfigEmail/getById'),
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
      if (id) {
        this.getDataList()
      }
    },
    // 表单提交
    dataFormSubmit () {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.$http({
            url: this.$http.adornUrl(`/v1/messageConfigEmail/${!this.dataForm.id ? 'save' : 'update'}`),
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
