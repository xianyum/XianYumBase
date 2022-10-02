<template>
  <el-dialog
    :title="!dataForm.id ? '代理配置新增' : '代理配置修改'"
    :close-on-click-modal="false"
    :visible.sync="visible"
    :before-close="cancel"
    width="30%"
    center>
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()"
             label-width="100px">

      <el-form-item label="客户端名称" prop="proxyId">
        <el-select v-model="dataForm.proxyId" placeholder="请选择客户端" filterable :disabled="dataForm.id">
          <el-option
            v-for="item in proxyList"
            :key="item.id"
            :label="item.name"
            :value="item.id">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="代理名称" prop="name">
        <el-input v-model="dataForm.name" placeholder="代理名称"></el-input>
      </el-form-item>
      <el-form-item label="公网端口" prop="inetPort">
        <el-input v-model="dataForm.inetPort" placeholder="公网端口(不填默认生成)" onkeyup="value=value.replace(/[^\d]/g,'')"
                  @blur="inetPortChange"></el-input>
      </el-form-item>
      <el-form-item label="代理地址" prop="lan">
        <el-input v-model="dataForm.lan" placeholder="代理地址，格式ip:port"></el-input>
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
        inetPort: '',
        lan: '127.0.0.1:3389',
        name: '',
        proxyId: ''
      },
      proxyList: [],
      dataRule: {
        name: [
          {required: true, message: '代理名称不能为空', trigger: 'blur'}
        ],
        lan: [
          {required: true, message: '代理地址不能为空', trigger: 'blur'}
        ],
        proxyId: [
          {required: true, message: '请选择客户端', trigger: 'blur'}
        ]
      }
    }
  },
  created () {
  },
  methods: {
    inetPortChange (e) {
      this.dataForm.inetPort = e.target.value
    },
    getProvList () {
      this.$http({
        url: this.$http.adornUrl('/proxy/getList'),
        method: 'post',
        data: {
          'id': 111111
        }
      }).then(({data}) => {
        if (data && data.code === 200) {
          this.proxyList = data.data
        } else {
          this.$message.error(data.msg)
        }
      })
    },
    cancel () {
      this.visible = false
      this.$refs['dataForm'].resetFields()
    },
    getDataList () {
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
        if (this.dataForm.id) {
          this.$http({
            url: this.$http.adornUrl('/proxyDetails/getById'),
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
      this.getProvList()
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
            url: this.$http.adornUrl(`/proxyDetails/${!this.dataForm.id ? 'save' : 'update'}`),
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
