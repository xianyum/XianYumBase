<template>
  <div>
    <el-card style="width:100%; margin-top:10px; clear:both;">
      <div slot="header" class="clearfix">
        <span>消息详情</span>
      </div>
      <div>
        <el-form :model="dataForm">
          <el-form-item label="MID：">
            {{ dataForm.id }}
          </el-form-item>
          <el-form-item label="消息标题：">
            {{ dataForm.messageTitle }}
          </el-form-item>
          <el-form-item label="消息编码：">
            {{ dataForm.messageCode }}
          </el-form-item>
          <el-form-item label="消息类型：">
            {{ formatMessageType(dataForm.messageType) }}
          </el-form-item>
          <el-form-item label="发送时间：">
            {{ new Date(dataForm.createTime).Format('yyyy-MM-dd hh:mm:ss') }}
          </el-form-item>
          <el-form-item label="跳转URL：" v-show="messageContent.formUrl != '' && messageContent.formUrl != null">
            <el-link type="success" :underline="false" :href="messageContent.formUrl" target="_blank">{{messageContent.formUrl}}</el-link>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <el-card style="width:100%; margin-top:30px; clear:both;">
      <div slot="header" class="clearfix">
        <span>消息内容</span>
      </div>
      <div>
        <json-viewer
          :value="messageContent" :expand-depth=5
          copyable
          boxed
          sort></json-viewer>
      </div>
    </el-card>

    <el-card style="width:100%; margin-top:30px; clear:both;">
      <div slot="header" class="clearfix">
        <span>推送结果</span>
      </div>
      <div>
        <json-viewer
          :value="messageResponse" :expand-depth=5
          copyable
          boxed
          sort></json-viewer>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  data() {
    return {
      messageAccountTypeList: [],
      dataForm: {
        messageId: ''
      },
      messageContent: '',
      messageResponse: ''
    }
  },
  created() {
    this.dataForm.messageId = this.$route.query.messageId
    console.log(this.dataForm.messageId)
    this.getMessageAccountTypeList()
    this.getDataList()
  },
  methods: {
    formatMessageType(type) {
      if (!type) {
        return ''
      }
      let name = type
      this.messageAccountTypeList.filter(function (item) {
        if (item.code === type) {
          name = item.name
        }
      })
      return name
    },
    getMessageAccountTypeList() {
      this.$http({
        url: this.$http.adornUrl('/systemConstant/getPublicConstant'),
        method: 'post',
        data: this.$http.adornData({
          'constantKey': 'message_account_type'
        })
      }).then(({data}) => {
        this.messageAccountTypeList = JSON.parse(data.data.constantValue)
      })
    },
    getDataList() {
      this.$http({
        url: this.$http.adornUrl('/v1/messageMonitor/getById'),
        method: 'post',
        data: this.$http.adornData({
          'id': this.dataForm.messageId
        })
      }).then(({data}) => {
        if (data && data.code === 200) {
          this.dataForm = data.data
          this.messageContent = JSON.parse(data.data.messageContent)
          try {
            this.messageResponse = JSON.parse(data.data.messageResponse)
          }catch(e) {
            this.messageResponse = data.data.messageResponse
          }
        }
      })
    }
  }
}
</script>

<style scoped>

</style>
