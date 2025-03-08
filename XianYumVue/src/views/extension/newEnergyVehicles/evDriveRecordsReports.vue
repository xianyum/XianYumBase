<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="日期类型" prop="dateType">
        <el-select v-model="queryParams.dateType" @change="dateTypeChange" placeholder="请选择主机" clearable>
          <el-option
            v-for="item in dateTypeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="日期范围" prop="timeRange" clearable>
        <el-date-picker
          v-model="queryParams.timeRange"
          :type="elDatePicker.dateType"
          range-separator="至"
          :start-placeholder="elDatePicker.startDesc"
          :end-placeholder="elDatePicker.endDesc"
          :value-format="elDatePicker.dateFormat">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <div style="margin-top: 50px">
      <ev-drive-records-line :chart-data="evDriveLineData"/>
    </div>
  </div>
</template>

<script>
import {getEvDriveRecordsReportLineData } from '@/api/extension/evDriveRecords'
import EvDriveRecordsLine from '@/views/dashboard/evDriveRecordsLine'

export default {
  name: 'evDriveRecordsReports',
  components: { EvDriveRecordsLine},
  data() {
    return {
      evDriveLineData: [],
      dateTypeOptions: [{
        value: 0,
        label: '按日'
      }, {
        value: 1,
        label: '按月'
      }],
      // 日期选择默认值
      elDatePicker:{
        dateType: 'daterange',
        startDesc: '请选择开始日期',
        endDesc: '请选择结束日期',
        dateFormat: 'yyyy-MM-dd'
      },
      // 显示搜索条件
      showSearch: true,
      dateValue: '',
      queryParams:{
        timeRange:[
          this.parseTime(new Date(new Date().getFullYear(), new Date().getMonth()-1, 1, 0, 0, 0),'{y}-{m}-{d}'),
          this.parseTime(new Date(),'{y}-{m}-{d}')
        ],
        dateType: 0
      }
    }
  },
  created() {
    this.handleQuery()
  },
  methods: {
    dateTypeChange(){
      if(this.queryParams.dateType == 0){
        this.elDatePicker ={
            dateType: 'daterange',
            startDesc: '请选择开始日期',
            endDesc: '请选择结束日期',
            dateFormat: 'yyyy-MM-dd'
        }
        this.timeRange = [
          this.parseTime(new Date(new Date().getFullYear(), new Date().getMonth()-1, 1, 0, 0, 0),'{y}-{m}-{d}'),
          this.parseTime(new Date(),'{y}-{m}-{d}')
        ]
      }else{
        this.queryParams.timeRange = undefined
        this.elDatePicker = {
          dateType: 'monthrange',
          startDesc: '请选择开始月份',
          endDesc: '请选择结束月份',
          dateFormat: 'yyyy-MM'
        }
      }
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm('queryForm')
      this.timeRange = [
        this.parseTime(new Date(new Date().getFullYear(), new Date().getMonth()-1, 1, 0, 0, 0),'{y}-{m}-{d}'),
        this.parseTime(new Date(),'{y}-{m}-{d}')
      ]
      this.handleQuery()
    },
    handleQuery(){
      getEvDriveRecordsReportLineData(this.queryParams).then(response => {
        let driveLineData = response.data
        const driveDateArray = driveLineData.map(item => item.driveDate);
        const totalElectricityConsumedArray = driveLineData.map(item => item.totalElectricityConsumed);
        const totalDistanceKmArray = driveLineData.map(item => item.totalDistanceKm);
        const electricityPerKmArray = driveLineData.map(item => item.electricityPerKm);
        let driveLineDataArray = [];
        driveLineDataArray.push(driveDateArray)
        driveLineDataArray.push(totalDistanceKmArray)
        driveLineDataArray.push(totalElectricityConsumedArray)
        driveLineDataArray.push(electricityPerKmArray)
        this.evDriveLineData = driveLineDataArray
      })
    }
  }
}
</script>
