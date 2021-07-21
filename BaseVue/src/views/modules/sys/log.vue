<template>
  <div class="mod-log">
    <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">
      <el-form-item>
        <el-input v-model="dataForm.nameOrDesc" placeholder="用户名／用户操作" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-date-picker
          v-model="dataForm.time"
          type="datetimerange"
          :picker-options="pickerOptions"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          align="center">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button round @click="queryData()">查询</el-button>
        <el-button round type="primary" @click="changeType()">切换图表</el-button>
        <el-button type="danger"  @click="deleteHandle()" :disabled="dataListSelections.length <= 0">批量删除</el-button>
      </el-form-item>
    </el-form>

    <div>
      <ve-chart :data="chartData" :settings="chartSettings1" :data-empty="dataEmpty" :colors="echartsColors" v-loading="dataListLoading1" element-loading-text="正在汇总数据..."
               element-loading-spinner="el-icon-loading"></ve-chart>
    </div>

    <el-table
      :data="dataList"
      border
      stripe
      @selection-change="selectionChangeHandle"
      v-loading="dataListLoading"
      style="width: 100%">
      <el-table-column
        type="selection"
        header-align="center"
        align="center"
        width="50">
      </el-table-column>
      <el-table-column
        label="序号"
        type="index"
        width="50">
      </el-table-column>
      <el-table-column
        prop="username"
        header-align="center"
        align="center"
        width="150"
        label="用户名">
      </el-table-column>
      <el-table-column
        prop="operation"
        header-align="center"
        align="center"
        width="200"
        label="用户操作">
      </el-table-column>
      <el-table-column
        prop="method"
        header-align="center"
        align="center"
        width="150"
        :show-overflow-tooltip="true"
        label="请求方法">
      </el-table-column>
      <el-table-column
        prop="params"
        header-align="center"
        align="center"
        :show-overflow-tooltip="true"
        label="请求参数">
      </el-table-column>
      <el-table-column
        prop="timeMs"
        header-align="center"
        align="center"
        width="130"
        label="执行时长">
      </el-table-column>
      <el-table-column
        prop="ip"
        header-align="center"
        align="center"
        width="150"
        label="IP地址">
      </el-table-column>
      <el-table-column
        prop="ipInfo"
        header-align="center"
        align="center"
        label="IP地点">
      </el-table-column>
      <el-table-column
        prop="createTime"
        header-align="center"
        align="center"
        width="180"
        label="创建时间"
        :formatter="formateTime">
      </el-table-column>
      <el-table-column
        header-align="center"
        fixed="right"
        align="center"
        width="150"
        label="操作">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="getRequestParams(scope.row)">查看请求参数</el-button>
          <el-button type="text" size="small"  @click="deleteHandle(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      @size-change="sizeChangeHandle"
      @current-change="currentChangeHandle"
      :current-page="pageIndex"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="pageSize"
      :total="totalPage"
      layout="total, sizes, prev, pager, next, jumper">
    </el-pagination>
    <el-dialog
      :title="'请求参数'"
      :close-on-click-modal="false"
      :visible.sync="requestVisible"
      :before-close="cancleRequestForm"
      width="25%">
      <json-viewer
        :value="requestParam" :expand-depth=5
        copyable
        boxed
        sort></json-viewer>
    </el-dialog>
  </div>
</template>

<script>
  export default {
    data () {
      this.typeArr = ['line', 'bar','histogram']
      this.index = 0
      this.echartsColors = ['#836FFF']
      return {
        chartSettings1: {
          type: this.typeArr[this.index],
          labelMap: {
            visitCount: '日志写入量'
          },
          metrics: ['visitCount'],
          dimension: ['time']
        },
        chartData: {
          columns: ['time','visitCount'],
          rows: []
        },
        dataEmpty: true,
        pickerOptions: {
          shortcuts: [
            {
              text: '今天',
              onClick(picker) {
                const start = new Date();
                const start1 = new Date(start.getFullYear(),start.getMonth(),start.getDate(),0,0,0);
                const end1 = new Date(start.getFullYear(),start.getMonth(),start.getDate(),23,59,59);
                picker.$emit('pick', [start1, end1]);
              }
            },{
              text: '昨天',
              onClick(picker) {
                const start = new Date();
                start.setTime(start.getTime() - 3600 * 1000 * 24);
                const start1 = new Date(start.getFullYear(),start.getMonth(),start.getDate(),0,0,0);
                const end1 = new Date(start.getFullYear(),start.getMonth(),start.getDate(),23,59,59);
                picker.$emit('pick', [start1, end1]);
              }
            },{
            text: '最近一周',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
              picker.$emit('pick', [start, end]);
            }
          }, {
            text: '最近一个月',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
              picker.$emit('pick', [start, end]);
            }
          }, {
            text: '最近三个月',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
              picker.$emit('pick', [start, end]);
            }
          }]
        },
        dataForm: {
          startTime: '',
          endTime: '',
          nameOrDesc: '',
          time: [new Date(new Date().getFullYear(),new Date().getMonth(),new Date().getDate(),0,0,0),new Date(new Date().getFullYear(),new Date().getMonth(),new Date().getDate(),23,59,59)]
        },
        requestParam: {
        },
        dataListSelections: [],
        requestVisible: false,
        dataList: [],
        pageIndex: 1,
        pageSize: 10,
        totalPage: 0,
        dataListLoading: false,
        dataListLoading1: true,
        selectionDataList: []
      }
    },
    created () {
      this.queryData()
    },
    methods: {
      changeType () {
        this.index++
        if (this.index >= this.typeArr.length) { this.index = 0 }
        this.chartSettings1 = { type: this.typeArr[this.index],labelMap: {visitCount: '日志写入量'} }
      },
      queryData () {
        this.getDataList();
        this.getVisitCountCharts();
      },
      getVisitCountCharts (){
        this.dataListLoading1 = true
        let startTime = this.dataForm.time[0]
        let endTime = this.dataForm.time[1]
        this.$http({
          url: this.$http.adornUrl('/log/getVisitCountCharts'),
          method: 'post',
          data: this.$http.adornData({
            'nameOrDesc': this.dataForm.nameOrDesc,
            'startTime': new Date(startTime).Format('yyyy-MM-dd hh:mm:ss'),
            'endTime': new Date(endTime).Format('yyyy-MM-dd hh:mm:ss'),
            'time': this.dataForm.time,
            'pageNum': this.pageIndex,
            'pageSize': this.pageSize
          })
        }).then(({data}) => {
          if (data && data.code === 200) {
            this.chartData.rows = data.data
          }
          this.dataListLoading1 = false
        })
      },
      // 删除
      deleteHandle(id) {
        var logIdS = id ? [id] : this.dataListSelections.map(item => {
          return item.id
        })
        this.$confirm(`确定要进行删除操作吗?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl('/log/delete'),
            method: 'post',
            data: this.$http.adornData(logIdS, false)
          }).then(({data}) => {
            if (data && data.code === 200) {
              this.$message({
                message: '操作成功',
                type: 'success',
                duration: 1500,
                onClose: () => {
                  this.getDataList()
                }
              })
            } else {
              this.$message.error(data.msg)
            }
          })
        }).catch(() => {
        })
      },
      // 多选
      selectionChangeHandle(val) {
        this.dataListSelections = val
      },
      cancleRequestForm (){
        this.requestParam = ''
        this.requestVisible = false;
      },
      getRequestParams (row){
        let params = row.params
        this.requestParam = JSON.parse(params)
        this.requestVisible = true
      },
      formateTime (row, column,cell) {
        if(!cell){
          return ''
        }
        return new Date(cell).Format('yyyy-MM-dd hh:mm:ss');
      },
      dateFormat:function(date) {
        var year=date.getFullYear();
        /* 在日期格式中，月份是从0开始的，因此要加0
         * 使用三元表达式在小于10的前面加0，以达到格式统一  如 09:11:05
         * */
        var month= date.getMonth()+1<10 ? "0"+(date.getMonth()+1) : date.getMonth()+1;
        var day=date.getDate()<10 ? "0"+date.getDate() : date.getDate();
        var hours=date.getHours()<10 ? "0"+date.getHours() : date.getHours();
        var minutes=date.getMinutes()<10 ? "0"+date.getMinutes() : date.getMinutes();
        var seconds=date.getSeconds()<10 ? "0"+date.getSeconds() : date.getSeconds();
        // 拼接
        return year+"-"+month+"-"+day+" "+hours+":"+minutes+":"+seconds;
      },
      // 获取数据列表
      getDataList () {
        this.dataListLoading = true
        let startTime = this.dataForm.time[0]
        let endTime = this.dataForm.time[1]
        this.$http({
          url: this.$http.adornUrl('/log/list'),
          method: 'post',
          data: this.$http.adornData({
            'nameOrDesc': this.dataForm.nameOrDesc,
            'startTime': this.dateFormat(startTime),
            'endTime': this.dateFormat(endTime),
            'time': this.dataForm.time,
            'pageNum': this.pageIndex,
            'pageSize': this.pageSize
          })
        }).then(({data}) => {
          if (data && data.code === 200) {
            this.dataList = data.data.records
            this.dataList.forEach((item) => {
              item.timeMs = item.time + 'ms'
            })
            this.totalPage = data.data.total
          } else {
            this.dataList = []
            this.totalPage = 0
          }
          this.dataListLoading = false
        })
      },
      // 每页数
      sizeChangeHandle (val) {
        this.pageSize = val
        this.pageIndex = 1
        this.getDataList()
      },
      // 当前页
      currentChangeHandle (val) {
        this.pageIndex = val
        this.getDataList()
      }
    }
  }
</script>
