<template>
  <div class="mod-user">
    <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">
      <el-form-item>
        <el-input v-model="dataForm.programTitle" placeholder="程序题目" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-input v-model="dataForm.contactName" placeholder="联系人" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-select v-model="dataForm.status" placeholder="订单状态" clearable>
          <el-option
            v-for="item in statusList"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-date-picker
          v-model="dataForm.time"
          type="daterange"
          range-separator="至"
          start-placeholder="订单开始日期"
          end-placeholder="订单结束日期"
          value-format="yyyy-MM-dd">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button @click="getDataList()" round >查询</el-button>
        <el-button type="success"  @click="addOrUpdateHandle()" round>新增</el-button>
        <el-button type="primary"  @click="download()" round>下载程序填写模板</el-button>
        <el-button type="warning"  @click="exportExcel()" round>导出</el-button>
        <el-button type="danger"  @click="deleteHandle()" :disabled="dataListSelections.length <= 0" round>批量删除</el-button>
      </el-form-item>
    </el-form>
    <el-table
      :data="dataList"
      border
      v-loading="dataListLoading"
      @selection-change="selectionChangeHandle"
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
        prop="programTitle"
        header-align="center"
        align="center"
        width="180px"
        label="程序题目">
      </el-table-column>
      <el-table-column
        prop="programRequirements"
        header-align="center"
        align="center"
        label="程序要求">
        <template slot-scope="scope">
          <el-link :href="scope.row.programRequirements" type="success" target="_blank">点击下载</el-link>
        </template>
      </el-table-column>
      <el-table-column
        prop="status"
        header-align="center"
        align="center"
        label="订单状态">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === 0" size="small" type="success">已完成</el-tag>
          <el-tag v-else-if="scope.row.status === 2" size="small">开发中</el-tag>
          <el-tag v-else size="small" type="danger">审核中</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="type"
        header-align="center"
        align="center"
        width="90px"
        label="订单类型">
        <template slot-scope="scope">
          <div v-if="scope.row.type === 0"  >系统</div>
          <div v-else-if="scope.row.type === 1"  >系统+论文</div>
          <div v-else-if="scope.row.type === 2" >论文</div>
          <div v-else ></div>
        </template>
      </el-table-column>
      <el-table-column
        prop="money"
        header-align="center"
        align="center"
        label="订单价格">
      </el-table-column>
      <el-table-column
        prop="contactName"
        header-align="center"
        align="center"
        label="联系人">
      </el-table-column>
      <el-table-column
        prop="contactPhone"
        header-align="center"
        align="center"
        width="130px"
        label="联系方式">
      </el-table-column>
      <el-table-column
        prop="tmallStatus"
        header-align="center"
        align="center"
        width="110px"
        label="是否淘系买家">
        <template slot-scope="scope">
          <div v-if="scope.row.tmallStatus === 1"  >散客买家</div>
          <div v-else size="small">淘系买家</div>
        </template>
      </el-table-column>
      <el-table-column
        prop="expectTime"
        header-align="center"
        align="center"
        :formatter="formateTime"
        width="160px"
        label="预计完成时间">
      </el-table-column>
      <el-table-column
        prop="completionTime"
        header-align="center"
        align="center"
        :formatter="formateTime"
        width="160px"
        label="最终完成时间">
      </el-table-column>
      <el-table-column
        prop="createUserName"
        header-align="center"
        align="center"
        width="100px"
        label="创建人名称">
      </el-table-column>
      <el-table-column
        prop="createTime"
        header-align="center"
        align="center"
        :formatter="formateTime"
        width="160px"
        label="创建日期">
      </el-table-column>
      <el-table-column
        fixed="right"
        header-align="center"
        align="center"
        width="150"
        label="操作">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="openProgramStatusSteps(scope.row.id)">进度</el-button>
          <el-popover
            :ref="`popover-${scope.$index}`"
            placement="left"
            width="300"
            v-model="visible">
            <p>要确定操作订单状态吗？请谨慎操作！</p>
            <div style="text-align: right; margin: 0">
              <el-button size="mini" type="text" round @click="handlePopoverClose(scope.$index)">取消</el-button>
              <el-button type="primary" size="mini" round @click="complete(scope.$index,scope.row.id,'develop')">开发</el-button>
              <el-button type="success" size="mini" round @click="complete(scope.$index,scope.row.id,'success')">完成</el-button>
            </div>
            <el-button type="text" size="small" slot="reference" >状态</el-button>
          </el-popover>
          <el-button type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">修改</el-button>
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
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @refreshDataList="getDataList"></add-or-update>


    <el-dialog
      title="订单当前进度条（实时更新）"
      :visible.sync="programStatusStepsVisible"
      width="30%"
      center>
      <div style="overflow-x:scroll;height: 500px">

        <hzqing-vue-timeline
          timelineColor="#5bbcd5"
          timeContentColor="#fff"
          :dataList="data"
        ></hzqing-vue-timeline>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import AddOrUpdate from './manage-add-or-update'

export default {
  data () {
    return {
      reverse: true,
      data: [
        {
          time: '2018-03-30 14:36:35',
          img: 'static/img/favicon.ico',
          content: '这是一个简单的vue时间轴插件，使用非常的方便'
        },
        {
          time: 1522372393000,
          img: 'static/img/favicon.ico',
          content: '当你发现你的才华撑不起野心时，就请安静下来学习吧~~~'
        },
        {
          time: 1522372393000,
          title: '努力奋斗',
          img: 'static/img/favicon.ico',
          content: '当你发现你的才华撑不起野心时，就请安静下来学习吧~~~'
        },
        {
          time: 1522372393000,
          title: '努力奋斗',
          img: 'static/img/favicon.ico',
          content: '当你发现你的才华撑不起野心时，就请安静下来学习吧~~~'
        },
        {
          time: 1522372393000,
          title: '努力奋斗',
          img: 'static/img/favicon.ico',
          content: '当你发现你的才华撑不起野心时，就请安静下来学习吧~~~'
        },
        {
          time: 1522372393000,
          title: '努力奋斗',
          img: 'static/img/favicon.ico',
          content: '当你发现你的才华撑不起野心时，就请安静下来学习吧~~~'
        }
      ],
      statusList: [{
        value: 0,
        label: '已完成'
      }, {
        value: 2,
        label: '开发中'
      }, {
        value: 1,
        label: '审核中'
      }],
      houseList: [],
      dataForm: {
        contactName: '',
        time: '',
        programTitle: '',
        status: ''
      },
      programTemplateUrl: '',
      programStatusStepsVisible: false,
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false
    }
  },
  components: {
    AddOrUpdate
  },
  activated () {
    this.getProgramTemplate()
    this.getDataList()
  },
  methods: {
    openProgramStatusSteps (id) {

      this.$http({
        url: this.$http.adornUrl('/program/schedule'),
        method: 'post',
        data: this.$http.adornData({
          'id': id
        })
      }).then(({data}) => {
        if (data && data.code === 200) {
          if(data.data && data.data.length >0){
            this.programStatusStepsVisible = true
            this.data = data.data
          }else{
            this.$message.warning("该订单暂时还没有进度")
          }
        } else {
          this.data = []
          this.$message.error(data.msg)
        }
      })
    },
    exportExcel () {
      let beginTime
      let endTime
      if (this.dataForm.time) {
        beginTime = this.dataForm.time[0] + ' 00:00:00'
        endTime = this.dataForm.time[1] + ' 23:59:59'
      }
      this.$http({
        url: this.$http.adornUrl('/program/export'),
        method: 'post',
        data: this.$http.adornData({
          'beginTime': beginTime,
          'endTime': endTime,
          'programTitle': this.dataForm.programTitle,
          'contactName': this.dataForm.contactName,
          'status': this.dataForm.status,
          'pageNum': 1,
          'pageSize': 100000
        }),
        responseType: 'arraybuffer'
      }).then(({data, headers}) => {
        const link = document.createElement('a'); // 生成一个a标签。
        let blob = new Blob([data], {type: "application/vnd.ms-excel"});
        let objectUrl = URL.createObjectURL(blob);
        link.href = objectUrl
        let title = headers['filename']
        link.download = decodeURI(title) // 自定义文件名
        link.click() // 下载文件
        URL.revokeObjectURL(objectUrl); // 释放内存
      })
    },
    complete (index,id,tag) {
      this.$http({
        url: this.$http.adornUrl('/program/complete'),
        method: 'post',
        data: this.$http.adornData({
          'id': id,
          'tag': tag
        })
      }).then(({data}) => {
        if (data && data.code === 200) {
          this.$message({
            message: '订单完成成功',
            type: 'success',
            duration: 1500,
            onClose: () => {
              this.$refs[`popover-${index}`].doClose()
              this.getDataList()
            }
          })
        } else {
          this.$message.error(data.msg)
        }
      })
    },
    handlePopoverClose (index){
      this.$refs[`popover-${index}`].doClose()
    },
    getProgramTemplate () {
      this.$http({
        url: this.$http.adornUrl('/systemConstant/getPrivateConstant'),
        method: 'post',
        data: this.$http.adornData({
          'constantKey': 'program_template'
        })
      }).then(({data}) => {
        this.programTemplateUrl = data.data.constantValue;
      })
    },
    download (){
      window.open(this.programTemplateUrl, '_blank','width=400,height=500,menubar=no,toolbar=no')
    },
    review (row) {
      window.open(row.programRequirements, '_blank')
    },
    formateTime (row, column,cell) {
      if(!cell){
        return ''
      }
      return new Date(cell).Format('yyyy-MM-dd hh:mm:ss');
    },
    // 获取数据列表
    getDataList () {
      this.dataListLoading = true
      let beginTime
      let endTime
      if (this.dataForm.time) {
        beginTime = this.dataForm.time[0] + ' 00:00:00'
        endTime = this.dataForm.time[1] + ' 23:59:59'
      }
      this.$http({
        url: this.$http.adornUrl('/program/list'),
        method: 'post',
        data: this.$http.adornData({
          'beginTime': beginTime,
          'endTime': endTime,
          'programTitle': this.dataForm.programTitle,
          'contactName': this.dataForm.contactName,
          'status': this.dataForm.status,
          'pageNum': this.pageIndex,
          'pageSize': this.pageSize
        })
      }).then(({data}) => {
        if (data && data.code === 200) {
          this.dataList = data.data.records
          this.totalPage = data.data.total
        } else {
          this.$message.error(data.msg)
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
    },
    // 多选
    selectionChangeHandle (val) {
      this.dataListSelections = val
    },
    // 新增 / 修改
    addOrUpdateHandle (userid) {
      this.addOrUpdateVisible = true
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(userid)
      })
    },
    // 删除
    deleteHandle (id) {
      var ids = id ? [id] : this.dataListSelections.map(item => {
        return item.id
      })
      this.$confirm(`确定要进行删除操作吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: this.$http.adornUrl('/program/delete'),
          method: 'post',
          data: this.$http.adornData(ids, false)
        }).then(({data}) => {
          if (data && data.code === 200) {
            this.$message({
              message: '删除成功',
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
    }
  }
}
</script>
