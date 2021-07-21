<template>
  <el-dialog
    :title="!dataForm.id ? '客户端新增' : '客户端修改'"
    :close-on-click-modal="false"
    :visible.sync="visible"
    :before-close="cancel"
    width="30%"
    center>
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()"
             label-width="100px">

      <el-form-item label="客户端名称" prop="name">
        <el-input v-model="dataForm.name" placeholder="客户端名称"></el-input>
      </el-form-item>
      <el-form-item label="上线通知" prop="notify">
        <el-switch
          v-model="dataForm.notify"
          active-color="#13ce66"
          inactive-color="#ff4949"
          :active-value=1
          :inactive-value=0>
        </el-switch>
      </el-form-item>
      <el-form-item label="通知邮箱" prop="notifyEmail">
        <el-input v-model="dataForm.notifyEmail" placeholder="通知邮箱，填写了会给此邮件推送上线提醒"></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="cancel">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
export default {
  data () {
    return {
      visible: false,
      dataForm: {
        notify: 1,
        notifyEmail: '',
        name: ''
      },
      dataRule: {
        name: [
          {required: true, message: '客户端名称不能为空', trigger: 'blur'}
        ],
        notify: [
          {required: true, message: '上线通知不能为空', trigger: 'blur'}
        ],
        notifyEmail: [
          {required: true, message: '通知邮箱不能为空', trigger: 'blur'}
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
            url: this.$http.adornUrl('/proxy/getById'),
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
            url: this.$http.adornUrl(`/proxy/${!this.dataForm.id ? 'save' : 'update'}`),
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
