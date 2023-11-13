<template>
  <div class="app-container">
    <el-card style="width:100%; clear:both;">
      <div slot="header" class="clearfix">
        <span>基本信息</span>
      </div>
      <el-form :model="proxyData" label-width="110px">
        <el-row>
          <el-col :span="3">
            <el-form-item label="客户端名称：" prop="name">
              {{proxyData.name}}
            </el-form-item>
          </el-col>
          <el-col :span="3">
            <el-form-item label="在线状态：" prop="status">
              <template>
                <el-tag v-if="proxyData.status == 1" effect="plain"type="success">在线</el-tag>
                <el-tag v-else type="danger" effect="plain">离线</el-tag>
              </template>
            </el-form-item>
          </el-col>
          <el-col :span="3">
            <el-form-item label="登录次数：" prop="loginCount">
              {{proxyData.loginCount}}次
            </el-form-item>
          </el-col>
          <el-col :span="3">
            <el-form-item label="绑定账号：" prop="bindUserName">
              {{proxyData.bindUserName}}
            </el-form-item>
          </el-col>
          <el-col :span="3">
            <el-form-item label="绑定邮箱：" prop="bindEmail">
              {{proxyData.bindEmail}}
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <el-card style="width:100%;margin-top: 6px; clear:both;">
      <div slot="header" class="clearfix">
        <span>客户端映射</span>
      </div>
      <el-table :data="proxyDetailsList" stripe>
        <el-table-column label="代理名称" align="center" prop="name" />
        <el-table-column label="公网端口" align="center" prop="inetPort" />
        <el-table-column label="本地代理信息" align="center" prop="lan" />
        <el-table-column label="写入量" align="center" prop="writeBytesStr" />
        <el-table-column label="读取量" align="center" prop="readBytesStr" />
        <el-table-column label="当前连接数" align="center" prop="connectCount" />
        <el-table-column label="创建时间" align="center" prop="createTime" width="180">
          <template v-slot="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>


    <el-card style="width:100%;margin-top: 6px; clear:both;">
      <div slot="header" class="clearfix">
        <span>最近使用记录</span>
      </div>
      <el-table :data="proxyLogList" stripe>
        <el-table-column label="序号" type="index" align="center" width="50"/>
        <el-table-column label="主机ip" align="center" prop="hostIp" width="130"/>
        <el-table-column label="操作系统" align="center" prop="osName" width="145"/>
        <el-table-column label="当前版本" align="center" prop="clientVersion" width="80">
          <template v-slot="scope">
            <span v-if="scope.row.clientVersion">V {{ scope.row.clientVersion }}</span>
          </template>
        </el-table-column>
        <el-table-column label="内存信息" align="center" prop="memoryInfo" :show-overflow-tooltip="true" :formatter='formatMemoryInfo'/>
        <el-table-column label="cpu" align="center" prop="cpuUseAge" width="80">
          <template v-slot="scope">
            <span v-if="scope.row.cpuUseAge">{{ scope.row.cpuUseAge }}%</span>
          </template>
        </el-table-column>
        <el-table-column label="运行路径" align="center" prop="userDir" :show-overflow-tooltip="true"/>
        <el-table-column label="计算机用户" align="center" prop="computerUserName" />
        <el-table-column label="mac地址" align="center" prop="macAddress" :show-overflow-tooltip="true"/>
        <el-table-column label="使用时长" align="center" prop="onlineTime" :show-overflow-tooltip="true"/>
        <el-table-column label="cpu信息" align="center" prop="cpuModel" :show-overflow-tooltip="true"/>
        <el-table-column label="登录时间" align="center" prop="createTime" width="150">
          <template v-slot="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
      </el-table>

    </el-card>
  </div>
</template>

<script>
import {getCurrentProxy} from "@/api/xianyu/proxy";
import {getLastProxyLog} from "@/api/xianyu/proxyLog";
import {getCurrentProxyDetails} from "@/api/xianyu/proxyDetails";

export default {
  name: "index",
  data() {
    return {
      proxyData: {},
      proxyLogList: [],
      proxyDetailsList: []
    }
  },
  created() {
    this.getCurrentProxy()
    if(this.proxyData){
      this.getLastProxyLog()
      this.getCurrentProxyDetails()
      this.getXianYuInfo()
    }
  },
  methods: {
    getCurrentProxy(){
      getCurrentProxy().then(res => {
        this.proxyData = res.data
      });
    },
    getLastProxyLog(){
      getLastProxyLog().then(res => {
        this.proxyLogList = res.data
      });
    },
    getCurrentProxyDetails(){
      getCurrentProxyDetails().then(res => {
        this.proxyDetailsList = res.data
      });
    },
    getXianYuInfo(){
      this.getPublicSystemConstant("xianyu_client").then(res =>{
          let constantValue = JSON.parse(res.data.constantValue);
          this.$notify({
            title: '客户端公告',
            iconClass: 'el-icon-message-solid',
            message: constantValue.notice,
            position: 'bottom-right',
            duration: 0
          });
      })
    }
  }
}
</script>
