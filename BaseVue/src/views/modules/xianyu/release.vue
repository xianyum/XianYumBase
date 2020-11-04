<template>
  <el-form ref="form" :rules="dataRule" :model="form" label-width="150px">
    <el-form-item label="客户端标识" prop="constantKey" style="width: 50%">
      <el-input v-model="form.constantKey"  maxlength="100" placeholder="客户端通过标识符获取客户端信息" :disabled="true"></el-input>
    </el-form-item>
    <el-form-item label="客户端版本号" prop="versionNo" style="width: 50%">
      <el-input v-model="form.versionNo"  maxlength="100" type="number" placeholder="填入最新版本号客户端自动更新"></el-input>
    </el-form-item>
    <el-form-item label="客户端版本标签" prop="title" style="width: 50%">
      <el-input v-model="form.title"  maxlength="100" placeholder="客户端版本标签"></el-input>
    </el-form-item>
    <el-form-item label="客户端版本标题" prop="labelTitle" style="width: 50%">
      <el-input v-model="form.labelTitle"  maxlength="100" placeholder="客户端版本正文标题"></el-input>
    </el-form-item>
    <el-form-item label="客户端公告" prop="notice" style="width: 50%">
      <el-input v-model="form.notice"  type="textarea" maxlength="100" placeholder="用于展示最新客户端公告"></el-input>
    </el-form-item>
    <el-form-item label="客户端更新地址" prop="downloadUrl" style="width: 50%">
      <el-input v-model="form.downloadUrl"  maxlength="100" placeholder="客户端最新更新地址"></el-input>
    </el-form-item>
    <el-form-item label="系统常用参数描述" prop="constantDescribe" style="width: 50%">
      <el-input v-model="form.constantDescribe"  type="textarea" maxlength="100" placeholder="系统常用参数描述"></el-input>
    </el-form-item>
    <el-form-item label="上次发布时间" prop="updateTime" style="width: 50%">
      <el-input v-model="form.updateTime"  maxlength="100"  :disabled="true" placeholder="客户端版本上次发布时间"></el-input>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="sendInfo">发布</el-button>
      <el-button type="info" @click="reset">刷新</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
export default {
  data () {
    return {
      form: {
        id: '',
        constantKey: '',
        constantValue: '',
        versionNo: 0,
        title: '',
        downloadUrl: '',
        notice: '',
        labelTitle: '',
        constantDescribe: '',
        updateTime: ''
      },
      dataRule: {
        constantKey: [
          { required: true, message: '客户端标识不能为空', trigger: 'blur' }
        ],
        versionNo: [
          { required: true, message: '客户端版本号不能为空', trigger: 'blur' }
        ],
        title: [
          { required: true, message: '客户端版本标签不能为空', trigger: 'blur' }
        ],
        downloadUrl: [
          { required: true, message: '客户端更新地址不能为空', trigger: 'blur' }
        ],
        notice: [
          { required: true, message: '客户端公告不能为空', trigger: 'blur' }
        ],
        labelTitle: [
          { required: true, message: '客户端版本标题不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  created () {
    this.getConstantParams()
  },
  methods: {
    // 获取数据列表
    getConstantParams () {
      this.$http({
        url: this.$http.adornUrl('/systemConstant/getPrivateConstant'),
        method: 'post',
        data: this.$http.adornData({
          'constantKey': 'xianyu_client'
        })
      }).then(({data}) => {
        let dataValue = JSON.parse(data.data.constantValue)
        this.form = data.data
        this.form.updateTime = new Date(data.data.updateTime).Format('yyyy-MM-dd hh:mm:ss')
        this.form = {...this.form,...dataValue}
      })
    },
    reset () {
      this.disabled = false
      this.getConstantParams()
      // this.$refs['form'].resetFields()
    },
    sendInfo () {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          let params = {
            'versionNo': this.form.versionNo,
            'title': this.form.title,
            'downloadUrl': this.form.downloadUrl,
            'notice': this.form.notice,
            'labelTitle': this.form.labelTitle
          }
          this.form.constantValue = JSON.stringify(params)
          this.$http({
            url: this.$http.adornUrl('/systemConstant/update'),
            method: 'post',
            data: this.form
          }).then(({data}) => {
            if (data && data.code === 200) {
              this.reset()
              this.$message({
                message: '发布成功',
                type: 'success',
                duration: 1500
              })
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

<style scoped>

</style>
