<template>
  <div class="app-container">
    <el-form ref="form" :rules="dataRule" :model="form" label-width="150px">
      <el-row>
        <el-col :span="10">
          <el-form-item label="客户端标识" prop="constantKey">
            <el-input v-model="form.constantKey"  maxlength="100" placeholder="客户端通过标识符获取客户端信息" :disabled="true"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="10">
          <el-form-item label="上次发布时间" prop="updateTime" >
            <el-input v-model="form.updateTime"  maxlength="100"  :disabled="true" placeholder="客户端版本上次发布时间"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="10">
          <el-form-item label="服务端地址" prop="serverAddress">
            <el-input v-model="form.serverAddress"  maxlength="100" placeholder="服务端地址"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="10">
          <el-form-item label="服务端端口" prop="serverPort" >
            <el-input v-model="form.serverPort"  maxlength="100" placeholder="服务端端口"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="10">
          <el-form-item label="客户端版本标签" prop="title">
            <el-input v-model="form.title"  maxlength="100" placeholder="客户端版本标签"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="10">
          <el-form-item label="客户端版本标题" prop="labelTitle">
            <el-input v-model="form.labelTitle"  maxlength="100" placeholder="客户端版本正文标题"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="10">
          <el-form-item label="客户端API地址" prop="apiUrl">
            <el-input v-model="form.apiUrl"  maxlength="100" placeholder="客户端API地址"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="10">
          <el-form-item label="客户端更新地址" prop="downloadUrl">
            <el-input v-model="form.downloadUrl"  maxlength="100" placeholder="客户端最新更新地址"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="10">
          <el-form-item label="客户端版本号" prop="versionNo">
            <el-input v-model="form.versionNo"  maxlength="100" type="number" placeholder="输入最新版本号客户端自动更新"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="10">
          <el-form-item label="跳转详情地址" prop="redirectDetailUrl">
            <el-input v-model="form.redirectDetailUrl" placeholder="输入跳转详情地址"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="10">
          <el-form-item label="客户端公告" prop="notice">
            <el-input v-model="form.notice"  type="textarea" :rows="4" maxlength="100" placeholder="用于展示最新客户端公告"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item>
        <el-button v-hasPermi="['xianyu:release:pub']" type="primary" @click="sendInfo">发布</el-button>
        <el-button v-hasPermi="['xianyu:release:pub']" type="success" @click="refresh">刷新</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>


import { updateConfig } from '@/api/system/config'

export default {
  name: "XianYu_Release",

  data() {
    return {
      form: {},
      dataRule: {
        serverAddress: [
          { required: true, message: '服务端地址不能为空', trigger: 'blur' }
        ],
        serverPort: [
          { required: true, message: '服务端端口不能为空', trigger: 'blur' }
        ],
        constantKey: [
          { required: true, message: '客户端标识不能为空', trigger: 'blur' }
        ],
        versionNo: [
          { required: true, message: '客户端版本号不能为空', trigger: 'blur' }
        ],
        title: [
          { required: true, message: '客户端版本标签不能为空', trigger: 'blur' }
        ],
        apiUrl: [
          { required: true, message: '客户端API地址不能为空', trigger: 'blur' }
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
    };
  },
  created() {
    this.getData()
  },
  methods: {
    // 初始化數據
    getData(){
      this.getPublicSystemConstant('xianyu_client').then(response => {
        this.form = JSON.parse(response.data.constantValue)
        this.form.updateTime = this.parseTime(response.data.updateTime)
        this.form.constantKey = response.data.constantKey
        this.form.id = response.data.id
      })
    },
    refresh(){
      this.getData()
    },
    sendInfo(){
      let params = {
        'versionNo': this.form.versionNo,
        'title': this.form.title,
        'downloadUrl': this.form.downloadUrl,
        'notice': this.form.notice,
        'labelTitle': this.form.labelTitle,
        'apiUrl': this.form.apiUrl,
        'serverAddress': this.form.serverAddress,
        'serverPort': this.form.serverPort,
        'redirectDetailUrl': this.form.redirectDetailUrl
      }
      this.form.constantValue = JSON.stringify(params)
      this.$refs["form"].validate(valid => {
        if (valid) {
          updateConfig(this.form).then(response => {
            this.$modal.msgSuccess("咸鱼服务端发版成功");
            this.getData();
          });
        }
      });
    }
  }
};
</script>
