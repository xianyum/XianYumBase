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
            {{ parseTime(dataForm.createTime) }}
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
import JsonViewer from 'vue-json-viewer';
import {getMessageDetailInfo} from '@/api/message/message';

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
  components: { JsonViewer },
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
      this.getPublicSystemConstant('message_account_type').then(res => {
        this.messageAccountTypeList = JSON.parse(res.data.constantValue)
      });
    },
    getDataList() {
      this.reqeustForm = {
        'id': this.dataForm.messageId
      }
      getMessageDetailInfo(this.reqeustForm).then(res => {
        this.dataForm = res.data
        this.messageContent = JSON.parse(res.data.messageContent)
        try {
          this.messageResponse = JSON.parse(res.data.messageResponse)
        }catch(e) {
          this.messageResponse = data.data.messageResponse
        }
      })
    }
  }
}
</script>

<style scoped>

</style>
