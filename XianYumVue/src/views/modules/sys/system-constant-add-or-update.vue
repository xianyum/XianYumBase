<template>
  <el-dialog
    :title="!dataForm.id ? '系统常量新增' : '系统常量修改'"
    :close-on-click-modal="false"
    :visible.sync="visible"
    :before-close="cancel"
    width="35%"
    center>
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
      <el-form-item label="系统常量键" prop="constantKey">
        <el-input v-model="dataForm.constantKey" placeholder="系统常量键" :disabled=dataForm.id></el-input>
      </el-form-item>
      <el-form-item label="系统常量值" prop="constantValue">
        <el-input type="textarea" :rows="4" v-model="dataForm.constantValue" placeholder="系统常量值"></el-input>
      </el-form-item>
      <el-form-item label="可见性" prop="constantVisible">
        <el-switch
          v-model="dataForm.constantVisible"
          active-color="#13ce66"
          inactive-color="#FFCC33"
          :active-value=0
          active-text="公开"
          inactive-text="私有"
          :inactive-value=1>
        </el-switch>
      </el-form-item>
      <el-form-item label="系统常量描述" prop="constantDescribe">
        <el-input type="text" v-model="dataForm.constantDescribe" placeholder="系统常量描述"></el-input>
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
  data() {
    return {
      visible: false,
      dataForm: {},
      dataRule: {
        constantKey: [
          {required: true, message: '系统常量键不能为空', trigger: 'blur'}
        ],
        constantDescribe: [
          {required: true, message: '系统常量描述不能为空', trigger: 'blur'}
        ],
        constantVisible: [
          {required: true, message: '系统常量可见性不能为空', trigger: 'blur'}
        ],
        constantValue: [
          {required: true, message: '系统常量值不能为空', trigger: 'blur'}
        ]
      }
    }
  },
  created() {
  },
  methods: {
    cancel() {
      this.visible = false
      this.$refs['dataForm'].resetFields()
    },
    getDataList() {
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
        if (this.dataForm.id) {
          this.$http({
            url: this.$http.adornUrl('/systemConstant/getById'),
            method: 'post',
            data: this.$http.adornData({
              'id': this.dataForm.id
            })
          }).then(({data}) => {
            if (data && data.code === 200) {
              this.dataForm = data.data
              // try {
              //   this.dataForm.constantValue = JSON.parse(data.data.constantValue)
              // } catch (e) {
              //   this.dataForm.constantValue = data.data.constantValue
              // }
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
    dataFormSubmit() {
      if (!this.dataForm.constantValue) {
        console.log(this.dataForm)
        this.$message.error('系统常量值不能为空')
        return
      }
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.$http({
            url: this.$http.adornUrl(`/systemConstant/${!this.dataForm.id ? 'save' : 'update'}`),
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
