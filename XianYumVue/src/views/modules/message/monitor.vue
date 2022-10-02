<template>
  <div class="mod-user">
    <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">
      <el-form-item>
        <el-input v-model="dataForm.id" placeholder="MID" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-select v-model="dataForm.messageType" placeholder="请选择账户类型" filterable clearable>
          <el-option
            v-for="item in messageAccountTypeList"
            :key="item.code"
            :label="item.name"
            :value="item.code"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-select v-model="dataForm.messageCode" placeholder="请选择消息类型" filterable clearable>
          <el-option
            v-for="item in messageTypeList"
            :key="item.messageCode"
            :label="`${item.messageCode}---${item.description}`"
            :value="item.messageCode">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-input v-model="dataForm.messageTitle" placeholder="消息标题" clearable></el-input>
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
        <el-button round @click="getDataList()">查询</el-button>
        <el-button round type="info" @click="reset()">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table
      :data="dataList"
      border
      stripe
      v-loading="dataListLoading"
      @selection-change="selectionChangeHandle"
      style="width: 100%;">
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
        prop="id"
        header-align="center"
        align="center"
        width="280"
        label="MID">
      </el-table-column>
      <el-table-column
        prop="messageTitle"
        header-align="center"
        align="center"
        width="280"
        label="消息标题">
      </el-table-column>
      <el-table-column
        prop="messageContent"
        header-align="center"
        align="center"
        show-overflow-tooltip
        label="消息内容">
      </el-table-column>
      <el-table-column
        prop="messageType"
        header-align="center"
        align="center"
        label="消息类型"
        :formatter="formatMessageAccountType">
      </el-table-column>
      <el-table-column
        prop="messageResponse"
        header-align="center"
        align="center"
        show-overflow-tooltip
        label="推送结果">
      </el-table-column>
      <el-table-column
        prop="toUser"
        header-align="center"
        align="center"
        show-overflow-tooltip
        label="发送人">
      </el-table-column>
      <el-table-column
        prop="createTime"
        header-align="center"
        align="center"
        label="推送时间"
        width="160px"
        :formatter="formateTime">
      </el-table-column>
      <el-table-column
        header-align="center"
        fixed="right"
        align="center"
        width="150"
        label="操作">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="showContent(scope.row.messageContent)">查看内容</el-button>
          <el-button type="text" size="small" @click="showContent(scope.row.messageResponse)">查看结果</el-button>
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
      :title="'消息内容'"
      :close-on-click-modal="false"
      :visible.sync="contentVisible"
      :before-close="cancleContentForm"
      width="25%">
      <json-viewer
        :value="messageContent" :expand-depth=5
        copyable
        boxed
        sort></json-viewer>
    </el-dialog>

  </div>
</template>

<script>
export default {
  data() {
    return {
      messageTypeList: [],
      messageAccountTypeList: [],
      contentVisible: false,
      dataForm: {
        messageCode: '',
        id: '',
        title: '',
        content: '',
        time: []
        // time: [new Date(new Date().getFullYear(),new Date().getMonth(),new Date().getDate(),0,0,0),new Date(new Date().getFullYear(),new Date().getMonth(),new Date().getDate(),23,59,59)]
      },
      messageContent: '',
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      pickerOptions: {
        shortcuts: [
          {
            text: '今天',
            onClick(picker) {
              const start = new Date();
              const start1 = new Date(start.getFullYear(), start.getMonth(), start.getDate(), 0, 0, 0);
              const end1 = new Date(start.getFullYear(), start.getMonth(), start.getDate(), 23, 59, 59);
              picker.$emit('pick', [start1, end1]);
            }
          }, {
            text: '昨天',
            onClick(picker) {
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24);
              const start1 = new Date(start.getFullYear(), start.getMonth(), start.getDate(), 0, 0, 0);
              const end1 = new Date(start.getFullYear(), start.getMonth(), start.getDate(), 23, 59, 59);
              picker.$emit('pick', [start1, end1]);
            }
          }, {
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
      }
    }
  },
  activated() {
    this.getMessageAccountTypeList()
    this.getMessageTypeList()
    this.getDataList()
  },
  methods: {
    getMessageTypeList() {
      this.$http({
        url: this.$http.adornUrl('/v1/messageTypeConfig/getList'),
        method: 'post',
        data: this.$http.adornData({})
      }).then(({data}) => {
        if (data && data.code === 200) {
          this.messageTypeList = data.data
        }
      })
    },
    formatMessageAccountType(row, column, cell) {
      if (!cell) {
        return ''
      }
      let name = cell;
      this.messageAccountTypeList.filter(function (item) {
        if (item.code === cell) {
          name = item.name
        }
      })
      return name
    },
    getMessageAccountTypeList() {
      this.$http({
        url: this.$http.adornUrl('/systemConstant/getPrivateConstant'),
        method: 'post',
        data: this.$http.adornData({
          'constantKey': 'message_account_type'
        })
      }).then(({data}) => {
        this.messageAccountTypeList = JSON.parse(data.data.constantValue)
      })
    },
    cancleContentForm() {
      this.messageContent = ''
      this.contentVisible = false
    },
    showContent(text) {
      try {
        this.messageContent = JSON.parse(text)
      } catch (e) {
        this.messageContent = text
      }
      this.contentVisible = true
    },
    formateTime(row, column, cell) {
      if (!cell) {
        return ''
      }
      return new Date(cell).Format('yyyy-MM-dd hh:mm:ss');
    },
    reset() {
      // this.dataForm.time = [new Date(new Date().getFullYear(),new Date().getMonth(),new Date().getDate(),0,0,0),new Date(new Date().getFullYear(),new Date().getMonth(),new Date().getDate(),23,59,59)],
      this.dataForm.time = []
      this.dataForm.messageTitle = ''
      this.dataForm.id = ''
      this.dataForm.messageCode = ''
      this.dataForm.messageContent = ''
      this.dataForm.messageType = ''
      this.pageIndex = 1
      this.pageSize = 10
      this.getDataList()
    },
    // 获取数据列表
    getDataList() {
      this.dataListLoading = true
      let startTime = null
      let endTime = null
      if (this.dataForm.time) {
        startTime = this.dataForm.time[0]
        endTime = this.dataForm.time[1]
      }
      this.$http({
        url: this.$http.adornUrl('/v1/messageMonitor/getPage'),
        method: 'post',
        data: this.$http.adornData({
          'id': this.dataForm.id,
          'startTime': startTime,
          'endTime': endTime,
          'messageTitle': this.dataForm.messageTitle,
          'messageContent': this.dataForm.messageContent,
          'messageType': this.dataForm.messageType,
          'pageNum': this.pageIndex,
          'pageSize': this.pageSize,
          'messageCode': this.dataForm.messageCode
        })
      }).then(({data}) => {
        if (data && data.code === 200) {
          this.dataList = data.data.records
          this.totalPage = data.data.total
        } else {
          this.dataList = []
          this.totalPage = 0
        }
        this.dataListLoading = false
      })
    },
    // 每页数
    sizeChangeHandle(val) {
      this.pageSize = val
      this.pageIndex = 1
      this.getDataList()
    },
    // 当前页
    currentChangeHandle(val) {
      this.pageIndex = val
      this.getDataList()
    },
    // 多选
    selectionChangeHandle(val) {
      this.dataListSelections = val
    }
  }
}
</script>
