<template>
  <div :class="className" :style="{height:height,width:width}" />
</template>

<script>
import * as echarts from 'echarts';
require('echarts/theme/macarons') // echarts theme
import resize from './mixins/resize'

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
      default: '500px'
    },
    autoResize: {
      type: Boolean,
      default: true
    },
    chartData: {
      type: Array,
      required: true
    }
  },
  data() {
    return {
      chart: null
    }
  },
  watch: {
    chartData: {
      deep: true,
      handler(val) {
        this.setOptions(val)
      }
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initChart()
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
    initChart() {
      this.chart = echarts.init(this.$el, 'macarons')
      this.setOptions(this.chartData)
    },
    setOptions(dataArray= []) {
      let showDtaZoom = false
      if (dataArray && dataArray[0] && dataArray[0].length > 38) {
        showDtaZoom = true;
      }
      const colors = ['#5470C6', '#91CC75', '#EE6666'];
      this.chart.setOption({
        color: colors,
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross'
          }
        },
        grid: {
          right: '20%'
        },
        toolbox: {
          show: true,//是否显示工具栏组件。
          orient: 'vertical',//工具栏 icon 的布局朝向。
          left: 'right',//工具栏组件离容器左侧的距离。
          top: 'center',//工具栏组件离容器上侧的距离。
          feature: {
            magicType: { show: true, type: ['line', 'bar', 'stack'], title: { line: '切换为折线图', bar: '切换为柱状图', stack: '切换为堆叠' } },
            dataView: { show: true, readOnly: false },
            saveAsImage: { show: true }
          }
        },
        legend: {
          data: ['行驶公里数', '消耗电量', '平均电耗']
        },
        dataZoom: [
          {
            type: 'slider',//滑动条型数据区域缩放组件
            show: showDtaZoom,//是否显示 组件。
            start: 0,//数据窗口范围的起始百分比。范围是：0 ~ 100。表示 0% ~ 100%。
            end: 80,//数据窗口范围的结束百分比。范围是：0 ~ 100。
            handleSize: 5,//控制手柄的尺寸，可以是像素大小，也可以是相对于 dataZoom 组件宽度的百分比，默认跟 dataZoom 宽度相同。
          },
        ],
        xAxis: [
          {
            type: 'category',
            axisTick: {
              alignWithLabel: true
            },
            data: dataArray[0]
          }
        ],
        yAxis: [
          {
            type: 'value',
            name: '行驶公里数',
            position: 'right',
            alignTicks: true,
            axisLine: {
              show: true,
              lineStyle: {
                color: colors[0]
              }
            },
            axisLabel: {
              formatter: '{value} km'
            }
          },
          {
            type: 'value',
            name: '消耗电量',
            position: 'right',
            alignTicks: true,
            offset: 80,
            axisLine: {
              show: true,
              lineStyle: {
                color: colors[1]
              }
            },
            axisLabel: {
              formatter: '{value} kwh'
            }
          },
          {
            type: 'value',
            name: '平均电耗',
            position: 'left',
            alignTicks: true,
            axisLine: {
              show: true,
              lineStyle: {
                color: colors[2]
              }
            },
            axisLabel: {
              formatter: '{value} kWh/km'
            }
          }
        ],
        series: [
          {
            name: '行驶公里数',
            type: 'line',
            data: dataArray[2]
          },
          {
            name: '消耗电量',
            type: 'line',
            yAxisIndex: 1,
            data: dataArray[1]
          },
          {
            name: '平均电耗',
            type: 'line',
            yAxisIndex: 2,
            data: dataArray[3]
          }
        ]
      })
    }
  }
}
</script>
