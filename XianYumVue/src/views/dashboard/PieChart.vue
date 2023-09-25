<template>
  <div :class="className" :style="{height:height,width:width}" />
</template>

<script>
import * as echarts from 'echarts';
require('echarts/theme/macarons') // echarts theme
import resize from './mixins/resize'
import {getMessageTypeList} from "@/api/message/messageTypeConfig";

export default {
  mixins: [resize],
  props: {
    className: {
      type: String,
      default: 'chart'
    },
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '300px'
    }
  },
  data() {
    return {
      chart: null,
      chartData:{
        chartTitle: [],
        chartData:[]
      }
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.getChartData()
    })
  },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
  },
  methods: {
    getChartData(){
      getMessageTypeList().then(res => {
        res.data.forEach((item,index)=>{
          this.chartData.chartTitle.push(item.description)
          let data = {
            value: item.sendCount,
            name: item.description,
          }
          this.chartData.chartData.push(data)
        });
        this.initChart();
      });
    },
    initChart() {
      this.chart = echarts.init(this.$el, 'macarons')

      this.chart.setOption({
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)'
        },
        legend: {
          left: 'center',
          bottom: '5',
          data: this.chartData.chartTitle
        },
        series: [
          {
            name: '消息发送量',
            type: 'pie',
            roseType: 'radius',
            radius: [15, 95],
            center: ['50%', '38%'],
            data: this.chartData.chartData,
            animationEasing: 'cubicInOut',
            animationDuration: 2600
          }
        ]
      })
    }
  }
}
</script>
