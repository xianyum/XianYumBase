<template>
  <div class="block">
    <el-timeline>
      <el-timeline-item timestamp="2019/6/27" placement="top">
        <el-card>
          <h4>待完成</h4>
          <p>待完成</p>
        </el-card>
      </el-timeline-item>
      <el-timeline-item timestamp="2019/6/20" placement="top">
        <el-card>
          <h4>v1.2 优化登录模块，增加日志注解</h4>
          <p>优化登录模块，增加日志注解，使用redis存储token，验证码，ip地址等</p>
        </el-card>
      </el-timeline-item>
      <el-timeline-item timestamp="2019/6/10" placement="top">
        <el-card>
          <h4>v1.1 版本新增一些常用的工具类,以及集成mybatis-plus多数据源</h4>
          <p>版本新增一些常用的工具类,以及集成mybatis-plus多数据源</p>
        </el-card>
      </el-timeline-item>
      <el-timeline-item timestamp="2019/5/1" placement="top">
        <el-card>
          <h4>v1.0 开始开发模板</h4>
          <p>准备做一套前后端分离版本脚手架，方便接一些项目</p>
        </el-card>
      </el-timeline-item>
    </el-timeline>
  </div>
</template>
<style>

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
            { type: 'cpu', value: 0 }
          ]
        },
        jvmChartData: {
          columns: ['type', 'value'],
          rows: [
            { type: 'jvm', value: 0 }
          ]
        },
        memChartData: {
          columns: ['type', 'value'],
          rows: [
            { type: 'mem', value: 0 }
          ]
        },
        sysFilesData: null
      }
    },
    methods: {
    }
  }
</script>
