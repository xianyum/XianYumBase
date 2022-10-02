<template>
  <div class="mod-user">
    <el-form :inline="true" :model="dataForm" ref="dataForm" @keyup.enter.native="getDataList()">
      <el-form-item prop="messageCode">
        <el-input v-model="dataForm.messageCode" placeholder="消息编码" clearable></el-input>
      </el-form-item>
      <el-form-item prop="messageTitle">
        <el-input v-model="dataForm.messageTitle" placeholder="消息标题" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-button plain @click="getDataList()" round>查询</el-button>
        <el-button type="success" @click="addOrUpdateHandle()" round>新增发送配置</el-button>
        <el-button type="danger" @click="confirmDeleteMessage()" :disabled="dataListSelections.length <= 0" round>批量删除
        </el-button>
        <el-button round type="info" @click="reset()">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table
      :data="dataList"
      border
      stripe
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
        align="center"
        width="50">
      </el-table-column>
      <el-table-column
        prop="messageTitle"
        header-align="center"
        align="center"
        label="消息标题">
      </el-table-column>
      <el-table-column
        prop="messageCode"
        header-align="center"
        align="center"
        label="消息编码">
      </el-table-column>
      <el-table-column
        prop="messageTypeDescription"
        header-align="center"
        align="center"
        label="消息类型描述">
      </el-table-column>
      <el-table-column
        prop="status"
        header-align="center"
        align="center"
        label="状态">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === 1" size="small" type="danger">禁用</el-tag>
          <el-tag v-else type="success" size="small">启用</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="createTime"
        header-align="center"
        align="center"
        :formatter="formatTime"
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
          <el-button type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">修改发送配置</el-button>
          <el-button type="text" size="small" @click="confirmDeleteMessage(scope.row.id)">删除</el-button>
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

  </div>
</template>

<script>
export default {
  data() {
    return {
      dataForm: {
        messageCode: '',
        messageTitle: ''
      },
      reset() {
        this.$refs['dataForm'].resetFields()
        this.pageIndex = 1
        this.pageSize = 10
        this.getDataList()
      },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false
    }
  },
  activated() {
    this.getDataList()
  },
  methods: {
    addOrUpdateHandle(id) {
      this.$router.push({name: 'message-send-config-add-or-update', params: {id: id}})
    },
    formatTime(row, column, cell) {
      if (!cell) {
        return ''
      }
      return new Date(cell).Format('yyyy-MM-dd hh:mm:ss')
    },
    // 获取数据列表
    getDataList() {
      this.dataListLoading = true
      this.$http({
        url: this.$http.adornUrl('/v1/messageSendConfig/getPage'),
        method: 'post',
        data: this.$http.adornData({
          'messageCode': this.dataForm.messageCode,
          'messageTitle': this.dataForm.messageTitle,
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
    },
    confirmDeleteMessage(id) {
      let _this = this
      this.$prompt('请在下方输入框填入【我同意删除】进行删除数据', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(({value}) => {
        if(value === '我同意删除'){
          _this.deleteHandle(id)
        }else{
          _this.$message({
            type: 'warning',
            message: '自动取消删除操作'
          });
        }
      }).catch(() => {
        _this.$message({
          type: 'info',
          message: '用户取消删除操作'
        });
      });
    },
    // 删除
    deleteHandle(id) {
      var ids = id ? [id] : this.dataListSelections.map(item => {
        return item.id
      })
      this.$http({
        url: this.$http.adornUrl('/v1/messageSendConfig/delete'),
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
    }
  }
}
</script>
