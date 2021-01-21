<template>
  <div>
    <div>
      <ve-gauge :data="chartData" :settings="chartSettings" style="float:left; width:33.33%;"></ve-gauge>
      <ve-gauge :data="memChartData" :settings="memChartSettings" style="float:left; width:33.33%;"></ve-gauge>
      <ve-gauge :data="jvmChartData" :settings="jvmChartSettings" style="float:left; width:33.33%;"></ve-gauge>
    </div>
    <div>
      <el-card style="width:100%; margin-top:80px; clear:both;">
        <div slot="header" class="clearfix">
          <span>服务器信息</span>
          <el-button style="float: right; padding: 3px 0" type="text" @click="fresh">刷新</el-button>
        </div>
        <el-row :gutter="24" align="middle" :data="tableData">
          <el-col :span="24">
            <div class="grid-content bg-purple threeCard">
              <span>服务器名称</span>
              <span prop="computerName">{{sys.computerName}}</span>
              <span>操作系统</span>
              <span>{{sys.osName}}</span>
            </div>
          </el-col>
          <el-col :span="24">
            <div class="grid-content bg-purple threeCard">
              <span>服务器IP</span>
              <span prop="used">{{sys.computerIp}}</span>
              <span>系统架构</span>
              <span>{{sys.osArch}}</span>
            </div>
          </el-col>
        </el-row>
      </el-card>
      <el-card style="width:100%; margin-top:20px; clear:both;">
        <div slot="header" class="clearfix">
          <span>Java虚拟机信息</span>
          <el-button style="float: right; padding: 3px 0" type="text" @click="fresh">刷新</el-button>
        </div>
        <el-row :gutter="24" align="middle" :data="tableData">
          <el-col :span="24">
            <div class="grid-content bg-purple threeCard">
              <span>Java名称</span>
              <span prop="used">{{jvm.name}}</span>
              <span>Java版本</span>
              <span>{{jvm.version}}</span>
            </div>
          </el-col>
          <el-col :span="24">
            <div class="grid-content bg-purple threeCard">
              <span>启动时间</span>
              <span prop="used">{{jvm.startTime}}</span>
              <span>运行时长</span>
              <span>{{jvm.runTime}}</span>
            </div>
          </el-col>
          <el-col :span="24">
            <div class="grid-content bg-purple threeCard">
              <span>安装路径</span>
              <span prop="used">{{jvm.home}}</span>
            </div>
          </el-col>
          <el-col :span="24">
            <div class="grid-content bg-purple threeCard">
              <span>项目路径</span>
              <span prop="used">{{sys.userDir}}</span>
            </div>
          </el-col>
        </el-row>
      </el-card>
      <el-card style="width:100%; margin-top:20px; clear:both;">
        <div slot="header" class="clearfix">
          <span>磁盘状态</span>
          <el-button style="float: right; padding: 3px 0" type="text" @click="fresh">刷新</el-button>
        </div>
        <el-table :data="sysFilesData" style="width: 100%">
          <el-table-column prop="dirName" width="180" label="盘符路径"></el-table-column>
          <el-table-column prop="sysTypeName" label="文件系统" width="180"></el-table-column>
          <el-table-column prop="typeName" label="盘符类型"></el-table-column>
          <el-table-column prop="total" label="总大小"></el-table-column>
          <el-table-column prop="free" label="可用大小"></el-table-column>
          <el-table-column prop="used" label="已用大小"></el-table-column>
          <el-table-column prop="usage" label="已用百分比" :formatter="formateUseage"></el-table-column>
        </el-table>
      </el-card>
    </div>
  </div>
</template>
<style>
  .clearfix:before,
  .clearfix:after {
    display: table;
    content: "";
  }

  .clearfix:after {
    clear: both
  }

  /*.box-card {*/
  /*  width: 48%;*/
  /*  float: left;*/
  /*  margin-bottom: 20px;*/
  /*}*/

  .el-col {
    border-radius: 4px;
  }

  .bg-purple-dark {
    background: #99a9bf;
  }

  .bg-purple span {
    width: 50%;
    float: left;
    height: 36px;
    line-height: 36px;
  }

  .threeCard span {
    width: 25%;
    float: left;
    height: 36px;
    line-height: 36px;
  }

  .twoCard span {
    width: 33.33333%;
    height: 36px;
    float: left;
    line-height: 36px;
  }

  .bg-purple-light {
    background: #e5e9f2;
  }

  .grid-content {
    min-height: 36px;
    border-bottom: 1px solid #eee;
  }

  .row-bg {
    padding: 10px 0;
    background-color: #f9fafc;
  }
</style>
<script>
  export default {
    data () {
      this.jvmChartSettings = {
        dataType: {
          'jvm': 'percent'
        },
        digit: 0,
        seriesMap: {
          'jvm': {
            min: 0,
            max: 1,
            show: false
          }
        },
        dataName: {
          'jvm': 'JVM占用率'
        }
      }
      this.memChartSettings = {
        dataType: {
          'mem': 'percent'
        },
        digit: 0,
        seriesMap: {
          'mem': {
            min: 0,
            max: 1
          }
        },
        dataName: {
          'mem': '内存占用率'
        }
      }
      this.chartSettings = {
        dataType: {
          'cpu': 'percent'
        },
        digit: 0,
        seriesMap: {
          'cpu': {
            min: 0,
            max: 1
          }
        },
        dataName: {
          'cpu': 'CPU占用率'
        }
      }
      return {
        chartData: {
          columns: ['type', 'value'],
          rows: [
            {type: 'cpu', value: 0}
          ]
        },
        jvmChartData: {
          columns: ['type', 'value'],
          rows: [
            {type: 'jvm', value: 0}
          ]
        },
        memChartData: {
          columns: ['type', 'value'],
          rows: [
            {type: 'mem', value: 0}
          ]
        },
        sysFilesData: null,
        tableData: null,
        cpuData: null,
        memData: null,
        cpu: {
          cpuNum: '',
          used: '',
          sys: '',
          free: ''
        },
        mem: {
          total: '',
          used: '',
          free: '',
          usage: ''
        },
        jvm: {
          total: '',
          used: '',
          free: '',
          usage: '',
          name: '',
          version: '',
          startTime: '',
          runTime: '',
          home: ''
        },
        sys: {
          computerName: '',
          osName: '',
          computerIp: '',
          osArch: '',
          userDir: ''
        }
      }
    },
    mounted () {
      this.getList()
      if (this.timer) {
        clearInterval(this.timer)
      } else {
        this.timer = setInterval(() => {
            this.getList()
          }, 8000
        )
      }
    },
    destroyed () {
      clearInterval(this.timer)
    },
    methods: {
      formateUseage (row, column) {
        let objD = row.usage
        return objD + '%'
      },
      fresh () {
        this.getList()
      },
      getList () {
        this.$http({
          url: this.$http.adornUrl('/server/get'),
          method: 'get',
          params: this.$http.adornParams({}),
          headers: {
            'contentType': 'application/x-www-form-urlencoded'
          }
        }).then(({data}) => {
          if (data && data.code === 200) {
            var cpu = data.data.cpu
            this.cpu.cpuNum = cpu.cpuNum
            this.cpu.sys = cpu.sys + '%'
            this.cpu.used = cpu.used + '%'
            this.cpu.free = cpu.free + '%'
            this.sysFilesData = data.data.sysFiles
            var mem = data.data.mem
            this.mem.total = mem.total + 'GB'
            this.mem.used = mem.used + 'GB'
            this.mem.free = mem.free + 'GB'
            this.mem.usage = mem.usage + '%'
            var jvm = data.data.jvm
            this.jvm.total = jvm.total + 'M'
            this.jvm.used = jvm.used + 'M'
            this.jvm.free = jvm.free + 'M'
            this.jvm.usage = jvm.usage + '%'
            this.jvm.name = jvm.name
            this.jvm.version = jvm.version
            this.jvm.startTime = jvm.startTime
            this.jvm.runTime = jvm.runTime
            this.jvm.home = jvm.home
            var sys = data.data.sys
            this.sys.computerName = sys.computerName
            this.sys.osName = sys.osName
            this.sys.computerIp = sys.computerIp
            this.sys.osArch = sys.osArch
            this.sys.userDir = sys.userDir
            this.chartData.rows[0].value = 1 - cpu.free / 100
            this.memChartData.rows[0].value = mem.usage / 100
            this.jvmChartData.rows[0].value = jvm.usage / 100
          } else {
            this.$message.error(data.msg)
          }
        })
      }
    }
  }
</script>
