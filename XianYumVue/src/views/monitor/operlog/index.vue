<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="85px">
      <el-form-item label="账号" prop="userName">
        <el-input
          v-model="queryParams.userName"
          placeholder="请输入账号"
          clearable
          style="width: 200px;"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="模块名" prop="module">
        <el-input
          v-model="queryParams.module"
          placeholder="请输入模块名"
          clearable
          style="width: 200px;"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="操作类型" prop="operTypeList">
        <el-select
          v-model="queryParams.operTypeList"
          placeholder="请选择操作类型"
          clearable
          multiple
          collapse-tags
          style="width: 200px"
        >
          <el-option
            v-for="dict in dict.type.user_oper_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="操作状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="请选择操作状态"
          clearable
          style="width: 200px"
        >
          <el-option label="成功" :value="1" />
          <el-option label="失败" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item label="操作时间" prop="dateRange">
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
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['monitor:operlog:delete']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          @click="handleClean"
          v-hasPermi="['monitor:operlog:delete']"
        >清空</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table ref="tables" v-loading="loading" :data="list" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="日志编号" align="center" prop="id" width="80"/>
      <el-table-column label="账号" align="center" prop="userName" width="100"/>
      <el-table-column label="模块" align="center" prop="module" width="120" :show-overflow-tooltip="true"/>
      <el-table-column label="操作类型" align="center" prop="operType" width="100">
        <template v-slot="scope">
          <dict-tag :options="dict.type.user_oper_type" :value="scope.row.operType"/>
        </template>
      </el-table-column>
      <el-table-column label="操作描述" align="center" prop="operDesc" :show-overflow-tooltip="true"/>
      <el-table-column label="请求方式" align="center" prop="requestMethod" width="90"/>
      <el-table-column label="请求URL" align="center" prop="requestUrl" :show-overflow-tooltip="true"/>
      <el-table-column label="IP地址" align="center" prop="ipAddress" width="130"/>
      <el-table-column label="IP归属地" align="center" prop="location" width="160" :show-overflow-tooltip="true"/>
      <el-table-column label="操作状态" align="center" prop="status" width="90">
        <template v-slot="scope">
          <el-tag v-if="scope.row.status === 1" size="small" type="success">成功</el-tag>
          <el-tag v-else size="small" type="danger">失败</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="耗时" align="center" prop="costTime" width="90">
        <template v-slot="scope">
          <span>{{ scope.row.costTime }}ms</span>
        </template>
      </el-table-column>
      <el-table-column label="操作时间" align="center" prop="createTime" width="170">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="150" fixed="right">
        <template v-slot="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
          >详细</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['monitor:operlog:delete']"
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

    <el-dialog title="操作日志详细" :visible.sync="open" width="760px" append-to-body>
      <el-form :model="form" label-width="100px" size="mini">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="日志编号">{{ form.id }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="账号">{{ form.userName }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="模块">{{ form.module }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="操作类型">
              <dict-tag :options="dict.type.user_oper_type" :value="form.operType"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="操作状态">
              <el-tag v-if="form.status === 1" size="small" type="success">成功</el-tag>
              <el-tag v-else size="small" type="danger">失败</el-tag>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="执行耗时">{{ form.costTime }}ms</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="IP地址">{{ form.ipAddress }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="IP归属地" :show-overflow-tooltip="true">{{ form.location }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="请求方式">{{ form.requestMethod }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="操作时间">{{ parseTime(form.createTime) }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="请求URL" :show-overflow-tooltip="true">{{ form.requestUrl }}</el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="操作描述" :show-overflow-tooltip="true">{{ form.operDesc }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="类名" :show-overflow-tooltip="true">{{ form.className }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="方法名" :show-overflow-tooltip="true">{{ form.methodName }}</el-form-item>
          </el-col>
          <el-col :span="24" v-if="form.errorMsg">
            <el-form-item label="错误信息">
              <span style="color: #F56C6C;">{{ form.errorMsg }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="form.requestParams">
            <el-form-item label="请求参数" label-position="top" class="json-form-item">
              <json-viewer
                :value="parseJson(form.requestParams)"
                :expand-depth=3
                copyable
                boxed
                sort></json-viewer>
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="form.responseResult">
            <el-form-item label="响应结果" label-position="top" class="json-form-item">
              <json-viewer
                :value="parseJson(form.responseResult)"
                :expand-depth=3
                copyable
                boxed
                sort></json-viewer>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="open = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { list, delOperLog, cleanOperLog } from "@/api/monitor/operlog";
import JsonViewer from 'vue-json-viewer';

export default {
  name: "Operlog",
  dicts: ['user_oper_type'],
  components: { JsonViewer },
  data() {
    return {
      loading: true,
      ids: [],
      multiple: true,
      showSearch: true,
      total: 0,
      list: [],
      open: false,
      form: {},
      dateRange: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userName: undefined,
        module: undefined,
        operTypeList: [],
        status: undefined
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    getList() {
      this.loading = true;
      const params = this.addDateRange(this.queryParams, this.dateRange);
      list(params).then(response => {
        this.list = response.data;
        this.total = response.total;
        this.loading = false;
      });
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.queryParams.pageNum = 1;
      this.handleQuery();
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id);
      this.multiple = !selection.length;
    },
    handleView(row) {
      this.open = true;
      this.form = row;
    },
    parseJson(str) {
      if (!str) return {};
      if (typeof str === 'object') return str;
      try {
        return JSON.parse(str);
      } catch (e) {
        return str;
      }
    },
    handleDelete(row) {
      const operIds = row.id ? [row.id] : this.ids;
      this.$modal.confirm('是否确认删除日志编号为"' + operIds + '"的数据项？').then(function() {
        return delOperLog(operIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    handleClean() {
      this.$modal.confirm('是否确认清空所有操作日志数据项？').then(function() {
        return cleanOperLog();
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("清空成功");
      }).catch(() => {});
    }
  }
};
</script>

<style scoped>
.json-form-item >>> .el-form-item__content {
  max-height: 280px;
  overflow: auto;
  padding: 8px 10px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}
</style>
