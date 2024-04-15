<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="85px">
      <el-form-item label="主机名称" prop="serverName">
        <el-input
          v-model="queryParams.serverName"
          placeholder="请输入主机名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="类型" prop="tag">
        <el-select v-model="queryParams.tag" placeholder="请选择类型" clearable>
          <el-option
            v-for="dict in dict.type.sys_server_tag"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
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
          v-hasPermi="['server-config:save']"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['server-config:update']"
        >修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['server-config:delete']"
        >删除
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="serverConfigList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="序号" type="index" align="center" width="50"/>
      <el-table-column label="主机名称" align="center" prop="serverName"/>
      <el-table-column label="公网ip" align="center" prop="serverPublicIp">
        <template v-slot="{ row }">
          {{ row.serverPublicIp || '暂无' }}
        </template>
      </el-table-column>
      <el-table-column label="主机位置" align="center" prop="serverLocation">
        <template v-slot="{ row }">
          {{ row.serverLocation || '暂无' }}
        </template>
      </el-table-column>
      <el-table-column label="内网ip" align="center" prop="serverLanIp"/>
      <el-table-column label="类型" align="center" prop="tag">
        <template v-slot="scope">
          <dict-tag :options="dict.type.sys_server_tag" :value="scope.row.tag"/>
        </template>
      </el-table-column>
      <el-table-column label="cpu核心数" align="center" prop="cpuNum"/>
      <el-table-column label="内存总量" align="center" prop="memTotal"/>
      <el-table-column label="创建日期" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['server-config:update']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['server-config:delete']"
          >删除
          </el-button>
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

    <!-- 添加或修改客户端管理对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="95px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="主机名称" prop="serverName">
              <el-input v-model="form.serverName" placeholder="请输入主机名称"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="类型" prop="tag">
              <el-select v-model="form.tag" placeholder="请选择类型" clearable>
                <el-option
                  v-for="dict in dict.type.sys_server_tag"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="cpu核心数" prop="cpuNum">
              <el-input v-model="form.cpuNum" placeholder="请输入cpu核心数"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="内存总量" prop="memTotal">
              <el-input v-model="form.memTotal" placeholder="请输入内存总量"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="公网ip" prop="serverPublicIp">
              <el-input v-model="form.serverPublicIp" placeholder="请输入公网ip"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="内网ip" prop="serverLanIp">
              <el-input v-model="form.serverLanIp" placeholder="请输入内网ip"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="备注" prop="useRemark">
              <el-input v-model="form.useRemark" type="textarea" placeholder="请输入备注"/>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getServerConfigPage,
  getServerConfigById, addServerConfig,
  updateServerConfig, delServerConfig
} from '@/api/extension/serverConfig'

export default {
  name: 'serverConfig',
  dicts: ['sys_server_tag'],
  data() {
    return {
      sendWechatLoading: false,
      drawer: false,
      direction: 'rtl',
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
      // 总条数
      total: 0,
      // 客户端管理表格数据
      serverConfigList: [],
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        serverName: undefined,
        tag: undefined
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        serverName: [
          { required: true, message: '主机名称不能为空', trigger: 'blur' }
        ],
        tag: [
          { required: true, message: '类型不能为空', trigger: 'blur' }
        ],
        memTotal: [
          { required: true, message: '内存总量不能为空', trigger: 'blur' }
        ],
        cpuNum: [
          { required: true, message: 'cpu核心数不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询客户端管理列表 */
    getList() {
      this.loading = true
      getServerConfigPage(this.queryParams).then(response => {
        this.serverConfigList = response.data
        this.total = response.total
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        id: undefined,
        serverName: undefined,
        tag: undefined,
        cpuNum: undefined,
        memTotal: undefined,
        serverPublicIp: undefined,
        serverLanIp: undefined,
        useRemark: undefined
      }
      this.resetForm('form')
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '添加主机配置'
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getServerConfigById(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = '修改主机配置'
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateServerConfig(this.form).then(response => {
              this.$modal.msgSuccess('修改成功')
              this.open = false
              this.getList()
            })
          } else {
            addServerConfig(this.form).then(response => {
              this.$modal.msgSuccess('新增成功')
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id ? [row.id] : this.ids
      this.$modal.confirm('是否确认删除主机配置数据？').then(function() {
        return delServerConfig(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {
      })
    }
  }
}
</script>
