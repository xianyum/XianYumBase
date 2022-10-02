<template>
  <div v-loading="loading" element-loading-text="正在加密/解密中...">
    <el-card style="width:100%; margin-top:10px; clear:both;">
      <div slot="header" class="clearfix">
        <span>AES加密/解密</span>
      </div>
      <div>
        <el-form :model="dataForm">
          <el-form-item>
            <el-input type="textarea" :rows="4" v-model="dataForm.content" placeholder="请输入加密/解密内容" clearable></el-input>
          </el-form-item>
          <el-form-item>
            <el-button  plain type="primary"  icon="el-icon-lock"  @click="aesEncryptOrDecrypt('encrypt')">加密</el-button>
            <el-button  plain type="primary"  icon="el-icon-unlock" @click="aesEncryptOrDecrypt('decrypt')">解密</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card style="width:100%; margin-top:30px; clear:both;">
      <div slot="header" class="clearfix">
        <span>加密/解密结果</span>
      </div>
      <div style="font-size: 20px;word-break:break-all">
        {{response}}
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  data () {
    return {
      dataForm: {
        content: ''
      },
      response: '',
      waybillNo: '',
      loading: false
    }
  },
  activated () {
  },
  methods: {
    aesEncryptOrDecrypt (type) {
      let path = ''
      if (type === 'encrypt'){
        path = '/p1/encryption/aes/encrypt'
      }else {
        path = '/p1/encryption/aes/decrypt'
      }
      this.loading = true
      this.$http({
        url: this.$http.adornUrl(path),
        method: 'get',
        params: this.dataForm
      }).then(({data}) => {
        this.loading = false
        if (data && data.code === 200) {
          this.response = data.data
        } else {
          this.response = data.msg
          this.$message.error(data.msg)
        }
      })
    }
  }
}
</script>
