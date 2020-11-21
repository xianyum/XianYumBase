<template>
  <el-dialog
    :title="!dataForm.id ? '程序订单新增' : '程序订单修改'"
    :close-on-click-modal="false"
    :visible.sync="visible"
    :before-close="cancle"
    center>
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">

      <el-row>
        <el-col :span="12">
          <el-form-item label="程序题目" prop="programTitle">
            <el-input v-model="dataForm.programTitle"  placeholder="程序题目"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="订单类型" prop="type" label-width="110px">
            <el-select v-model="dataForm.type" placeholder="订单类型">
              <el-option
                v-for="item in typeList"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="联系人" prop="contactName">
            <el-input v-model="dataForm.contactName"  placeholder="联系人"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
        <el-form-item label="买家类型" prop="tmallStatus" label-width="110px">
          <el-select v-model="dataForm.tmallStatus" placeholder="买家类型">
            <el-option
              v-for="item in tmallStatusList"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="联系方式" prop="contactPhone" >
            <el-input v-model="dataForm.contactPhone"  placeholder="联系方式"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="预计完成时间" prop="expectTime"  label-width="110px">
            <el-date-picker
              v-model="dataForm.expectTime"
              type="date"
              placeholder="请选择预计完成时间">
            </el-date-picker>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="订单金额" prop="money" >
            <el-input oninput="value=value.replace(/[^\d.]/g,'')" v-model="dataForm.money"  placeholder="订单金额"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12" v-if="isAuth('admin')">
          <el-form-item label="订单备注" prop="remark" >
            <el-input v-model="dataForm.remark"  placeholder="订单备注"  type="textarea" :rows="2"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-if="(this.dataForm.id && isAuth('admin')) || (!this.dataForm.id )">
        <el-col :span="12">
          <el-form-item label="程序要求" prop="filePath">
            <el-upload
              class="upload-demo"
              drag
              show-file-list="false"
              limit= 1
              :action="uploadUrl"
              :before-upload="beforeUploadHandle"
              :on-success="successHandle"
              ref="upload"
            >
              <i class="el-icon-upload"></i>
              <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
              <div class="el-upload__tip" slot="tip">注意：请下载程序模板填写完成后，在上传</div>
            </el-upload>
          </el-form-item>
        </el-col>
        <el-col :span="5">
        </el-col>
      </el-row>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="cancle">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
      <el-button type="info" @click="getHelp()">查看帮助</el-button>
    </span>
  </el-dialog>
</template>

<script>
export default {
  data () {
    var validateMobile = (rule, value, callback) => {
      if (!isMobile(value)) {
        callback(new Error('手机号格式错误'))
      } else {
        callback()
      }
    }
    return {
      tmallStatusList :[{
        value: 1,
        label: '散客买家'
      }, {
        value: 0,
        label: '淘宝买家'
      }],
      typeList: [{
        value: 0,
        label: '系统'
      }, {
        value: 2,
        label: '论文'
      }, {
        value: 1,
        label: '系统+论文'
      }],
      visible: false,
      dataForm: {
        programTitle: '',
        programRequirements: '',
        contactPhone: '',
        expectTime: '',
        tmallStatus: '',
        type: '',
        contactName: '',
        money: '',
        remark: ''
      },
      uploadUrl: '',
      dataRule: {
        programTitle: [
          { required: true, message: '程序题目不能为空', trigger: 'blur' }
        ],
        contactName: [
          { required: true, message: '联系人不能为空', trigger: 'blur' }
        ],
        contactPhone: [
          { required: true, message: '联系方式不能为空', trigger: 'blur' }
        ],
        tmallStatus: [
          { required: true, message: '买家类型不能为空', trigger: 'blur' }
        ],
        type: [
          { required: true, message: '订单类型不能为空', trigger: 'blur' }
        ],
        expectTime: [
          { required: true, message: '预计完成时间不能为空', trigger: 'blur' }
        ],
        money: [
          { required: true, message: '订单金额不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.uploadUrl = this.$http.adornUrl(`/oss/upload?token=${this.$cookie.get('token')}`);
  },
  methods: {
    getHelp(){
      this.$http({
        url: this.$http.adornUrl('/systemConstant/getPrivateConstant'),
        method: 'post',
        data: this.$http.adornData({
          'constantKey': 'program_help'
        })
      }).then(({data}) => {
        window.open(data.data.constantValue, '_blank')
      })
    },
    beforeUploadHandle(file) {
      // if (file.type !== 'application/pdf' && file.type !== 'application/msword'
      //   && file.type !== 'application/vnd.openxmlformats-officedocument.wordprocessingml.document') {
      //   this.$message.error('只支持doc、docx、pdf格式的图片！')
      //   return false
      // }
    },
    successHandle (response, file, fileList) {
      if (response && response.code === 200) {
        this.dataForm.programRequirements = response.data.url
        console.log(response.data.url)
      } else {
        this.dataForm.programRequirements = ''
      }
    },
    cancle () {
      this.visible = false
      this.$refs['dataForm'].resetFields()
      this.$refs.upload.clearFiles()
    },
    getDataList () {
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
        this.$refs.upload.clearFiles()
        if (this.dataForm.id) {
          this.$http({
            url: this.$http.adornUrl('/program/selectById'),
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
      if(!this.dataForm.id && ! this.dataForm.programRequirements){
        this.$message.error("请上传程序具体要求")
        return
      }
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.$http({
            url: this.$http.adornUrl(`/program/${!this.dataForm.id ? 'save' : 'update'}`),
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
              this.$refs.upload.clearFiles()
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
