<template>
  <div class="block">
    <el-timeline>
      <el-timeline-item timestamp="2019/9/27" placement="top">
        <el-card>
          <h4>不忘初心，方得始终</h4>
          <p> 系统会持续做下去，会实时集成市面上先进的技术，以及系统有什么BUG会及时优化</p>
        </el-card>
      </el-timeline-item>

      <el-timeline-item timestamp="2019/9/27" placement="top">
        <el-card>
          <h4>v1.4 爬虫监控，界面图标美化</h4>
          <p>2019-09-27 集成Gecco爬虫框架，把爬取的数据写到数据库里，并且把每天更新内容推送到微信上，以及界面图标美化</p>
        </el-card>
      </el-timeline-item>
      <el-timeline-item timestamp="2019/6/28" placement="top">
        <el-card>
          <h4>v1.3 注册功能</h4>
          <p>2019-06-28 增加用户注册功能，以及用户重复性校验优化</p>
        </el-card>
      </el-timeline-item>
      <el-timeline-item timestamp="2019/6/20" placement="top">
        <el-card>
          <h4>v1.2 优化登录模块，增加日志注解</h4>
          <p>2019-06-20 优化登录模块，增加日志注解，使用redis存储token，验证码，ip地址等</p>
        </el-card>
      </el-timeline-item>
      <el-timeline-item timestamp="2019/6/10" placement="top">
        <el-card>
          <h4>v1.1 版本新增一些常用的工具类,以及集成mybatis-plus多数据源</h4>
          <p>2019-06-10 版本新增一些常用的工具类,以及集成mybatis-plus多数据源</p>
        </el-card>
      </el-timeline-item>
      <el-timeline-item timestamp="2019/5/1" placement="top">
        <el-card>
          <h4>v1.0 开始开发模板</h4>
          <p>2019-05-01 准备做一套前后端分离版本脚手架，方便接一些项目</p>
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
