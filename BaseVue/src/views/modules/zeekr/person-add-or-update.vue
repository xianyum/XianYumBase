<template>
  <el-dialog
    :title="!dataForm.id ? 'zeekr-person-add' : 'zeekr-person-update'"
    :close-on-click-modal="false"
    :visible.sync="visible"
    :before-close="cancel"
    width="30%"
    center>
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()"
             label-width="100px">

      <el-form-item label="账号" prop="loginName">
        <el-input v-model="dataForm.loginName" placeholder="账号" :disabled="dataForm.id?true:false"></el-input>
      </el-form-item>
      <el-form-item label="项目列表" prop="projectId" v-if="dataForm.id?true:false">
        <el-select v-model="dataForm.projectId" placeholder="请选择项目列表" clearable>
          <el-option
            v-for="item in projectList"
            :key="item.id"
            :label="item.projectName"
            :value="item.id"
            @click.native="projectChange(item)">
          </el-option>
        </el-select>
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
      projectList: [],
      dataForm: {
        loginName: '',
        id: '',
        projectId: '',
        projectName: '',
        projectManagerId: '',
        projectManagerName: '',
        employeeName: '',
        employeeNum: ''
      },
      dataRule: {
        loginName: [
          {required: true, message: '账号不能为空', trigger: 'blur'}
        ],
        projectId: [
          {required: true, message: '项目不能为空', trigger: 'blur'}
        ]
      }
    }
  },
  created () {
  },
  methods: {
    projectChange(item){
      this.dataForm.projectId = item.id
      this.dataForm.projectName = item.projectName
      this.dataForm.projectManagerId = item.projectManagerUserid
      this.dataForm.projectManagerName = item.projectManagerName
    },
    cancel () {
      this.visible = false
      this.$refs['dataForm'].resetFields()
    },
    getProjectList(){
      this.$http({
        url: this.$http.adornUrl('/v1/zeekrPerson/getProjectList'),
        method: 'post',
        data: this.$http.adornData({
          'loginName': this.dataForm.loginName
        })
      }).then(({data}) => {
        if (data && data.code === 200) {
          this.projectList = data.data.result.content
        }
      })
    },
    getDataList () {
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
        if (this.dataForm.id) {
          this.$http({
            url: this.$http.adornUrl('/v1/zeekrPerson/getById'),
            method: 'post',
            data: this.$http.adornData({
              'id': this.dataForm.id
            })
          }).then(({data}) => {
            if (data && data.code === 200) {
              this.dataForm = data.data
              this.getProjectList()
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
            url: this.$http.adornUrl(`/v1/zeekrPerson/${!this.dataForm.id ? 'save' : 'update'}`),
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
