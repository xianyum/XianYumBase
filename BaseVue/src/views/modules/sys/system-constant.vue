<template>
  <div class="mod-user">
    <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">
      <el-form-item>
        <el-input v-model="dataForm.constantKey" placeholder="系统常量键" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-input v-model="dataForm.constantDescribe" placeholder="系统常量描述" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-button @click="getDataList()" round>查询</el-button>
        <el-button type="success" @click="addOrUpdateHandle()" round>新增</el-button>
        <el-button round type="info" @click="reset()">重置</el-button>
      </el-form-item>
    </el-form>
    <el-table
      :data="dataList"
      border
      stripe
      v-loading="dataListLoading"
      style="width: 100%">
      <el-table-column
        label="序号"
        type="index"
        align="center"
        width="50">
      </el-table-column>
      <el-table-column
        prop="constantKey"
        header-align="center"
        align="center"
        width="230"
        show-overflow-tooltip
        label="系统常量键">
      </el-table-column>
      <el-table-column
        prop="constantValue"
        header-align="center"
        align="center"
        width="300"
        show-overflow-tooltip
        label="系统常量值">
      </el-table-column>
      <el-table-column
        prop="constantDescribe"
        header-align="center"
        align="center"
        show-overflow-tooltip
        label="系统常量描述">
      </el-table-column>
      <el-table-column
        prop="constantVisible"
        header-align="center"
        align="center"
        label="可见性"
        width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.constantVisible === 0" size="small" type="success">公开</el-tag>
          <el-tag v-else size="small" type="warning">私有</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="createTime"
        header-align="center"
        align="center"
        :formatter="formatTime"
        width="160px"
        label="创建时间"
        sortable>
      </el-table-column>
      <el-table-column
        prop="updateTime"
        header-align="center"
        align="center"
        :formatter="formatTime"
        width="160px"
        label="更新时间"
        sortable>
      </el-table-column>
      <el-table-column
        fixed="right"
        header-align="center"
        align="center"
        width="230"
        label="操作">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="getValueByKey(scope.row.constantKey)">查看缓存</el-button>
          <el-button type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">修改</el-button>
          <el-button type="text" size="small" @click="deleteHandle(scope.row.constantKey)">删除</el-button>
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

    <!-- redis缓存数据查看 -->
    <el-dialog
      :title="'缓存数据'"
      :close-on-click-modal="false"
      :visible.sync="redisVisible"
      :before-close="cancleRedisForm"
      width="30%">
      <json-viewer
        :value="redisData" :expand-depth=5
        copyable
        boxed
        sort></json-viewer>
      <span slot="footer" class="dialog-footer">
      <el-button @click="cancleRedisForm" plain>取消</el-button>
      <el-button type="primary" plain @click="dataFormRedisByKey(redisData.constantKey)">删除缓存</el-button>
      </span>
    </el-dialog>

    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @refreshDataList="getDataList"></add-or-update>
  </div>
</template>

<script>
import AddOrUpdate from './system-constant-add-or-update'

export default {
  data() {
    return {
      dataForm: {
        constantKey: '',
        constantDescribe: ''
      },
      redisData: {},
      redisVisible: false,
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
  components: {AddOrUpdate},
  methods: {
    dataFormRedisByKey(key) {
      this.$http({
        url: this.$http.adornUrl('/systemConstant/deleteRedisCache'),
        method: 'get',
        params: {
          'key': key
        }
      }).then(({data}) => {
        if (data && data.code === 200) {
          this.$message({
            message: '删除缓存成功',
            type: 'success',
            duration: 1500,
            onClose: () => {
              this.cancleRedisForm()
            }
          })
        } else {
          this.$message.error(data.msg)
        }
      })
    },
    cancleRedisForm() {
      this.redisData = {}
      this.redisVisible = false
    },
    showRedisData(data) {
      this.redisVisible = true
      this.redisData = data
    },
    getValueByKey(key) {
      this.$http({
        url: this.$http.adornUrl('/systemConstant/getRedisCache'),
        method: 'get',
        params: {
          'key': key
        }
      }).then(({data}) => {
        if (data && data.code === 200) {
          if (data.data) {
            this.showRedisData(data.data)
          } else {
            this.$message.error('暂时没有缓存数据')
          }
        } else {
          this.$message.error(data.msg)
        }
      })
    },
    updateNotify(row) {
      this.$confirm(`确定要更新上线通知状态吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: this.$http.adornUrl('/proxy/update'),
          method: 'post',
          data: {
            'id': row.id,
            'notify': row.notify
          }
        }).then(({data}) => {
          if (data && data.code === 200) {
            this.$message({
              message: '上线通知状态更新成功',
              type: 'success'
            })
          } else {
            this.$message.error(data.msg)
          }
        })
      }).catch(() => {
      })
    },
    reset() {
      this.dataForm.constantKey = ''
      this.dataForm.constantDescribe = ''
      this.pageIndex = 1
      this.pageSize = 10
      this.getDataList()
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
        url: this.$http.adornUrl('/systemConstant/getPage'),
        method: 'post',
        data: this.$http.adornData({
          'constantKey': this.dataForm.constantKey,
          'constantDescribe': this.dataForm.constantDescribe,
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
    // 新增 / 修改
    addOrUpdateHandle(id) {
      this.addOrUpdateVisible = true
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id)
      })
    },
    // 删除
    deleteHandle(key) {
      this.$confirm(`请谨慎做删除操作，确定要删除吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: this.$http.adornUrl('/systemConstant/delete'),
          method: 'get',
          params: {
            'key': key
          }
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
        this.$message('取消删除操作')
      })
    }
  }
}
</script>
<style rel="stylesheet/scss" lang="scss">
.demo-table-expand {
  font-size: 0;
}

.demo-table-expand label {
  width: 150px;
  color: #99a9bf;
}

.demo-table-expand .el-form-item {
  margin-right: 0;
  margin-bottom: 0;
  width: 50%;
}
</style>
