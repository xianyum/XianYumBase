<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="系统常量键" prop="constantKey">
        <el-input
          v-model="queryParams.constantKey"
          placeholder="请输入系统常量键"
          clearable
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="系统常量描述" prop="constantDescribe">
        <el-input
          v-model="queryParams.constantDescribe"
          placeholder="请输入系统常量描述"
          clearable
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-refresh"
          size="mini"
          @click="handleRefreshCache"
        >刷新缓存</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="configList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" align="center" width="50" />
      <el-table-column label="系统常量键" align="center" prop="constantKey" :show-overflow-tooltip="true"/>
      <el-table-column label="系统常量值" align="center" prop="constantValue" :show-overflow-tooltip="true" />
      <el-table-column label="系统常量描述" align="center" prop="constantDescribe" :show-overflow-tooltip="true" />
      <el-table-column label="可见性" align="center" prop="constantVisible">
        <template v-slot="scope">
          <dict-tag :options="dict.type.sys_constant_visable" :value="scope.row.constantVisible"/>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-search"
            @click="getValueByKey(scope.row.constantKey)"
          >查看缓存</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
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

    <!-- 添加或修改参数配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="系统常量键" prop="constantKey">
          <el-input v-model="form.constantKey" placeholder="请输入系统常量键" />
        </el-form-item>
        <el-form-item label="系统常量值" prop="constantValue">
          <el-input type="textarea" :rows="4" v-model="form.constantValue" placeholder="请输入系统常量值" />
        </el-form-item>
        <el-form-item label="可见性" prop="constantVisible">
          <el-radio-group v-model="form.constantVisible">
            <el-radio
              v-for="dict in dict.type.sys_constant_visable"
              :key="dict.value"
              :label="dict.value"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="系统常量描述" prop="constantDescribe">
          <el-input v-model="form.constantDescribe" placeholder="请输入系统常量描述" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>



    <!-- redis缓存数据查看 -->
    <el-dialog
      :title="'缓存数据'"
      :close-on-click-modal="false"
      :visible.sync="redisVisible"
      :before-close="cancelRedisForm"
      width="30%">
      <json-viewer
        :value="redisData" :expand-depth=5
        copyable
        boxed
        sort></json-viewer>
      <span slot="footer" class="dialog-footer">
      <el-button @click="cancelRedisForm" plain>取消</el-button>
      <el-button type="primary" plain @click="dataFormRedisByKey(redisData.constantKey)">删除缓存</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>
import {
  listConfig,
  getConfig,
  delConfig,
  addConfig,
  updateConfig,
  refreshCache,
  getConfigByCache,
  deleteConfigByCache
} from '@/api/system/config'
import { Message } from 'element-ui'
import JsonViewer from 'vue-json-viewer';

export default {
  name: "Config",
  dicts: ['sys_constant_visable'],
  data() {
    return {
      redisData: {},
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      redisVisible: false,
      // 总条数
      total: 0,
      // 参数表格数据
      configList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 日期范围
      dateRange: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        constantKey: undefined,
        constantDescribe: undefined
      },
      // 表单参数
      form: {
        constantVisible: "0"
      },
      // 表单校验
      rules: {
        configName: [
          { required: true, message: "参数名称不能为空", trigger: "blur" }
        ],
        configKey: [
          { required: true, message: "参数键名不能为空", trigger: "blur" }
        ],
        configValue: [
          { required: true, message: "参数键值不能为空", trigger: "blur" }
        ]
      }
    };
  },
  components: { JsonViewer },
  created() {
    this.getList();
  },
  methods: {
    dataFormRedisByKey(key){
      deleteConfigByCache({"key":key}).then(res=>{
        this.$modal.msgSuccess("删除缓存成功");
        this.cancelRedisForm();
      })
    },
    getValueByKey(key){
      getConfigByCache({"key":key}).then(res=>{
        if(!res.data){
          this.$modal.msgWarning('暂时没有缓存数据')
        }else{
          this.showRedisData(res.data)
        }
      })
    },
    cancelRedisForm() {
      this.redisData = {}
      this.redisVisible = false
    },
    showRedisData(data) {
      this.redisVisible = true
      this.redisData = data
    },
    /** 查询参数列表 */
    getList() {
      this.loading = true;
      listConfig(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
          this.configList = response.data.records
          this.total = response.data.total
          this.loading = false;
        }
      );
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: undefined,
        constantKey: undefined,
        constantValue: undefined,
        constantDescribe: undefined,
        constantVisible: "0"
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加系统常量";
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!=1
      this.multiple = !selection.length
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const configId = row.id || this.ids
      getConfig(configId).then(response => {
        this.form = response.data;
        this.form.constantVisible = this.form.constantVisible.toString()
        this.open = true;
        this.title = "修改系统常量";
      });
    },
    /** 提交按钮 */
    submitForm: function() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != undefined) {
            updateConfig(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addConfig(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const configIds = row.constantKey;
      this.$modal.confirm('是否确认删除系统常量键为"' + row.constantKey + '"的数据项？').then(function() {
          return delConfig(configIds);
        }).then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        }).catch(() => {});
    },
    /** 刷新缓存按钮操作 */
    handleRefreshCache() {
      refreshCache().then(() => {
        this.$modal.msgSuccess("刷新成功");
      });
    }
  }
};
</script>
