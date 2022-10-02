<template>
  <el-dialog
    :title="!dataForm.id ? '微信企业配置新增' : '微信企业配置修改'"
    :close-on-click-modal="false"
    :visible.sync="visible"
    :before-close="cancel"
    width="30%"
    center>
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="120px">
      <el-form-item label="企业id" prop="corpId">
        <el-input v-model="dataForm.corpId" placeholder="企业id"></el-input>
      </el-form-item>
      <el-form-item label="应用秘钥" prop="corpSecret">
        <el-input v-model="dataForm.corpSecret" placeholder="应用秘钥"></el-input>
      </el-form-item>
      <el-form-item label="应用id" prop="agentId">
        <el-input v-model="dataForm.agentId" placeholder="应用id"></el-input>
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
        corpId: [
          {required: true, message: '企业id不能为空', trigger: 'blur'}
        ],
        corpSecret: [
          {required: true, message: '应用秘钥不能为空', trigger: 'blur'}
        ],
        agentId: [
          {required: true, message: '应用id不能为空', trigger: 'blur'}
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
            url: this.$http.adornUrl('/v1/messageConfigWechat/getById'),
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
            url: this.$http.adornUrl(`/v1/messageConfigWechat/${!this.dataForm.id ? 'save' : 'update'}`),
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
