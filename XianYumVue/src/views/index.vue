<template>

  <div class="dashboard-editor-container">

    <panel-group @handleSetLineChartData="handleSetLineChartData"/>

    <el-row style="background:#fff;padding:16px 16px 0;margin-bottom:32px;">
      <line-chart :chart-data="lineChartData"/>
    </el-row>

    <el-row :gutter="32">
      <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper">
          <raddar-chart/>
        </div>
      </el-col>
      <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper">
          <pie-chart/>
        </div>
      </el-col>
      <el-col :xs="24" :sm="24" :lg="8">
        <div class="chart-wrapper">
          <bar-chart/>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="8">
      <el-col :xs="{span: 24}" :sm="{span: 24}" :md="{span: 24}" :lg="{span: 12}" :xl="{span: 12}"
              style="padding-right:8px;margin-bottom:30px;">
        <transaction-table/>
      </el-col>
      <el-col :xs="{span: 24}" :sm="{span: 12}" :md="{span: 12}" :lg="{span: 6}" :xl="{span: 6}"
              style="margin-bottom:30px;">
        <todo-list/>
      </el-col>
      <el-col :xs="{span: 24}" :sm="{span: 12}" :md="{span: 12}" :lg="{span: 6}" :xl="{span: 6}"
              style="margin-bottom:30px;">
        <box-card/>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import PanelGroup from '@/views/dashboard/PanelGroup'
import LineChart from '@/views/dashboard/LineChart'
import RaddarChart from '@/views/dashboard/RaddarChart'
import PieChart from '@/views/dashboard/PieChart'
import BarChart from '@/views/dashboard/BarChart'
import {getLogVisitCountCharts} from "@/api/monitor/operlog";

export default {
  name: 'DashboardAdmin',
  components: {
    PanelGroup,
    LineChart,
    RaddarChart,
    PieChart,
    BarChart
  },
  data() {
    return {
      lineChartData: {
        "title": "",
        "xData": [],
        "yData": [],
      }
    }
  },
  created() {
    this.getLogVisitCountCharts();
  },
  methods: {
    getLogVisitCountCharts() {
      getLogVisitCountCharts().then(res => {
        this.lineChartData.title = "接口访问量";
        res.data.forEach((item,index) =>{
          this.lineChartData.xData.push(item.time)
          this.lineChartData.yData.push(item.visitCount)
        });
      });
    },
    handleSetLineChartData(type) {
      if(type === 'operLog'){
        this.$router.push({ path: "/monitor/operlog" });
      }else if(type === 'messages'){
        this.$router.push({ path: "/message/monitor" });
      }else if(type === 'job'){
        this.$router.push({ path: "/job/log" });
      }else if(type === 'proxy'){
        this.$router.push({ path: "/xianyu/proxy" });
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-editor-container {
  padding: 32px;
  background-color: rgb(240, 242, 245);
  position: relative;

  .chart-wrapper {
    background: #fff;
    padding: 16px 16px 0;
    margin-bottom: 32px;
  }
}

@media (max-width: 1024px) {
  .chart-wrapper {
    padding: 8px;
  }
}
</style>
