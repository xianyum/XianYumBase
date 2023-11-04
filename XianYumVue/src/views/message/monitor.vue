<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="MID" prop="id">
        <el-input
          v-model="queryParams.id"
          placeholder="MID"
          clearable
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item label="账户类型" prop="messageType">
        <el-select v-model="form.messageType" placeholder="请选择账户类型" filterable clearable>
          <el-option
            v-for="item in messageAccountTypeList"
            :key="item.code"
            :label="item.name"
            :value="item.code"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="消息类型" prop="messageCode">
        <el-select v-model="form.messageCode" placeholder="请选择消息类型" filterable clearable>
          <el-option
            v-for="item in messageTypeList"
            :key="item.messageCode"
            :label="`${item.messageCode}---${item.description}`"
            :value="item.messageCode">
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="消息标题" prop="messageTitle">
        <el-input
          v-model="queryParams.messageTitle"
          placeholder="消息标题"
          clearable
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="操作时间" clearable>
        <el-date-picker
          v-model="dateRange"
          style="width: 240px"
          value-format="yyyy-MM-dd HH:mm:ss"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="['00:00:00', '23:59:59']"
        ></el-date-picker>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['message:monitor:delete']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          @click="handleClean"
          v-hasPermi="['message:monitor:delete']"
        >清空</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="jobLogList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" width="50" />
      <el-table-column label="MID" width="280" align="center" prop="id" />
      <el-table-column label="消息标题" align="center" prop="messageTitle" width="280" />
      <el-table-column label="消息内容" align="center" prop="messageContent" :show-overflow-tooltip="true" />
      <el-table-column label="消息类型"  align="center" prop="messageType" :formatter="formatMessageAccountType"/>
      <el-table-column label="推送结果" align="center" prop="messageResponse" :show-overflow-tooltip="true" />
      <el-table-column label="发送人" align="center" prop="toUser" :show-overflow-tooltip="true" />
      <el-table-column label="推送时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>

      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
          >查看推送</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['message:monitor:delete']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 消息监控日志详细 -->
    <el-dialog title="推送详情" :visible.sync="open" width="700px" append-to-body>
      <el-form :model="formDetails" label-width="80px" size="mini">
      <el-form-item label="推送内容">
        <json-viewer
          :value="formDetails.messageContent" :expand-depth=6
          copyable
          boxed
          sort />
      </el-form-item>
      <el-form-item label="推送结果">
        <json-viewer
          :value="formDetails.messageResponse" :expand-depth=6
          copyable
          boxed
          sort />
      </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="open = false">关 闭</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { getPageList , delMessageMonitor ,cleanMessageMonitor} from "@/api/message/monitor";
import JsonViewer from 'vue-json-viewer';
import { getMessageTypeList } from '@/api/message/messageTypeConfig'

export default {
  name: "MessageMonitor",
  data() {
    return {
      messageTypeList: [],
      messageAccountTypeList: [],
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 调度日志表格数据
      jobLogList: [],
      // 是否显示弹出层
      open: false,
      // 日期范围
      dateRange: [],
      // 表单参数
      form: {},
      formDetails: {},
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        messageCode: undefined,
        messageType: undefined,
        id: undefined,
        messageTitle: undefined
      }
    };
  },
  components: { JsonViewer },
  created() {
    this.getMessageAccountTypeList()
    this.getMessageTypeList()
    this.getList();
  },
  methods: {
    getMessageTypeList(){
      getMessageTypeList().then(response => {
        this.messageTypeList = response.data
      });
    },
    getMessageAccountTypeList() {
      this.getPublicSystemConstant('message_account_type').then(res => {
        this.messageAccountTypeList = JSON.parse(res.data.constantValue)
      });
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
    /** 查询调度日志列表 */
    getList() {
      this.loading = true;
      getPageList(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
          this.jobLogList = response.data;
          this.total = response.total;
          this.loading = false;
        }
      );
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.queryParams.jobId = undefined;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.queryParams.jobId = undefined
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id);
      this.multiple = !selection.length;
    },
    /** 详细按钮操作 */
    handleView(row) {
      this.open = true;
      try {
        this.formDetails.messageContent = JSON.parse(row.messageContent);
      } catch (e) {
        this.formDetails.messageContent = row.messageContent
      }
      try {
        this.formDetails.messageResponse = JSON.parse(row.messageResponse);
      } catch (e) {
        this.formDetails.messageResponse = row.messageResponse
      }
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const jobLogIds = row.id ? [row.id] : this.ids;
      this.$modal.confirm('是否确认删除消息监控数据？').then(function() {
        return delMessageMonitor(jobLogIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      });
    },
    /** 清空按钮操作 */
    handleClean() {
      this.$modal.confirm('是否确认清空所有消息监控数据项？').then(function() {
        return cleanMessageMonitor();
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("清空成功");
      }).catch(() => {
      });
    }
  }
}
</script>
