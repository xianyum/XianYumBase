<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="85px">
      <el-form-item label="名称" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入名称"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="ApiKey" prop="apiKey">
        <el-input
          v-model="queryParams.apiKey"
          placeholder="请输入 ApiKey"
          clearable
          style="width: 220px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="isValid">
        <el-select v-model="queryParams.isValid" placeholder="全部" clearable style="width: 120px">
          <el-option label="生效" :value="true" />
          <el-option label="禁用" :value="false" />
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
          v-hasPermi="['system:apiKey:save']"
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
          v-hasPermi="['system:apiKey:update']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:apiKey:delete']"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="apiKeyList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" align="center" width="50" />
      <el-table-column label="名称" align="center" prop="title" :show-overflow-tooltip="true" />
      <el-table-column label="ApiKey" align="center" prop="apiKey" width="240">
        <template v-slot="scope">
          <div class="api-key-cell">
            <el-tooltip :content="scope.row.apiKey" placement="top">
              <span class="api-key-text">{{ scope.row.apiKey }}</span>
            </el-tooltip>
            <el-button
              type="text"
              size="mini"
              icon="el-icon-document-copy"
              title="复制"
              v-clipboard:copy="scope.row.apiKey"
              v-clipboard:success="handleCopySuccess"
            />
          </div>
        </template>
      </el-table-column>
      <el-table-column label="介绍" align="center" prop="intro" :show-overflow-tooltip="true" />
      <el-table-column label="归属账号" align="center" prop="loginId">
        <template v-slot="scope">
          <span>{{ userMap[scope.row.loginId] || scope.row.loginId }}</span>
        </template>
      </el-table-column>
      <el-table-column label="授权范围" align="center" prop="scopes">
        <template v-slot="scope">
          <el-tag
            v-for="item in scope.row.scopes"
            :key="item"
            size="mini"
            style="margin-right: 4px"
          >{{ item }}</el-tag>
          <span v-if="!scope.row.scopes || scope.row.scopes.length === 0">-</span>
        </template>
      </el-table-column>
      <el-table-column label="到期时间" align="center" prop="expiresTime" width="160">
        <template v-slot="scope">
          <el-tag v-if="isNeverExpires(scope.row.expiresTime)" size="mini" type="warning">永不过期</el-tag>
          <span v-else>{{ parseTime(scope.row.expiresTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="isValid" width="80">
        <template v-slot="scope">
          <el-tag v-if="scope.row.isValid" size="small" type="success">生效</el-tag>
          <el-tag v-else size="small" type="danger">禁用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="150">
        <template v-slot="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:apiKey:update']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:apiKey:delete']"
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

    <!-- 添加或修改 API Key 对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="ApiKey" prop="apiKey">
          <el-input
            v-model="form.apiKey"
            :disabled="form.id != null"
            placeholder="留空则由系统自动生成"
          />
        </el-form-item>
        <el-form-item label="名称" prop="title">
          <el-input v-model="form.title" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="归属账号" prop="loginId">
          <el-select
            v-model="form.loginId"
            :disabled="form.id != null"
            placeholder="请选择归属账号"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="item in userOptions"
              :key="item.id"
              :label="formatUserLabel(item)"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="介绍" prop="intro">
          <el-input v-model="form.intro" type="textarea" :rows="3" placeholder="请输入用途描述" />
        </el-form-item>
        <el-form-item label="授权范围" prop="scopes">
          <el-select
            v-model="form.scopes"
            multiple
            clearable
            filterable
            allow-create
            default-first-option
            placeholder="可不选，输入后回车创建，如 userinfo、chat"
            style="width: 100%"
          >
            <el-option v-for="item in scopeOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="到期时间" prop="expiresTime">
          <el-radio-group v-model="form.expiresType" @change="handleExpiresTypeChange">
            <el-radio label="never">永不过期</el-radio>
            <el-radio label="custom">指定时间</el-radio>
          </el-radio-group>
          <el-date-picker
            v-if="form.expiresType === 'custom'"
            v-model="form.expiresTime"
            type="datetime"
            value-format="timestamp"
            :picker-options="expiresTimePickerOptions"
            placeholder="请选择到期时间"
            style="width: 200px; margin-left: 10px"
          />
        </el-form-item>
        <el-form-item label="状态" prop="isValid">
          <el-switch v-model="form.isValid" active-text="生效" inactive-text="禁用" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listApiKey, getApiKey, addApiKey, updateApiKey, delApiKey, getScopeList } from '@/api/system/apiKey'
import { listUser } from '@/api/system/user'

export default {
  name: 'ApiKey',
  data() {
    return {
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
      // API Key 表格数据
      apiKeyList: [],
      // 用户下拉数据
      userOptions: [],
      // 用户ID与账号映射
      userMap: {},
      // 授权范围可选项
      scopeOptions: [],
      // 到期时间禁止选择今天之前的日期
      expiresTimePickerOptions: {
        disabledDate(time) {
          return time.getTime() < new Date().setHours(0, 0, 0, 0)
        }
      },
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: undefined,
        apiKey: undefined,
        isValid: undefined
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        title: [
          { required: true, message: '名称不能为空', trigger: 'blur' }
        ],
        loginId: [
          { required: true, message: '请选择归属账号', trigger: 'change' }
        ],
        expiresTime: [
          { validator: this.validateExpiresTime, trigger: 'change' }
        ]
      }
    }
  },
  created() {
    this.getList()
    this.getUserOptions()
    this.getScopeOptions()
  },
  methods: {
    /** 查询 API Key 列表 */
    getList() {
      this.loading = true
      listApiKey(this.queryParams).then(response => {
        this.apiKeyList = response.data || []
        this.total = response.total
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    /** 获取用户下拉数据 */
    getUserOptions() {
      listUser({ pageNum: 1, pageSize: 10000 }).then(response => {
        this.userOptions = response.data || []
        const userMap = {}
        this.userOptions.forEach(item => {
          userMap[item.id] = item.username
        })
        this.userMap = userMap
      })
    },
    /** 获取授权范围下拉数据 */
    getScopeOptions() {
      getScopeList().then(response => {
        this.scopeOptions = response.data || []
      })
    },
    /** 是否永不过期 */
    isNeverExpires(value) {
      if (value === null || value === undefined || value === '') {
        return true
      }
      return Number(value) === -1
    },
    formatUserLabel(user) {
      return user.nickName ? `${user.username}（${user.nickName}）` : user.username
    },
    /** 到期时间校验 */
    validateExpiresTime(rule, value, callback) {
      if (this.form.expiresType === 'custom' && !value) {
        callback(new Error('请选择到期时间'))
      } else if (this.form.expiresType === 'custom' && Number(value) <= Date.now()) {
        callback(new Error('到期时间必须晚于当前时间'))
      } else {
        callback()
      }
    },
    /** 切换到期类型 */
    handleExpiresTypeChange(value) {
      if (value === 'never') {
        this.form.expiresTime = undefined
      }
    },
    /** 复制成功 */
    handleCopySuccess() {
      this.$modal.msgSuccess('复制成功')
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
        apiKey: undefined,
        title: undefined,
        intro: undefined,
        loginId: undefined,
        scopes: [],
        expiresType: 'never',
        expiresTime: undefined,
        isValid: true
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
      this.title = '添加ApiKey'
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids[0]
      getApiKey(id).then(response => {
        const data = response.data
        const neverExpires = this.isNeverExpires(data.expiresTime)
        this.form = {
          id: data.id,
          apiKey: data.apiKey,
          title: data.title,
          intro: data.intro,
          loginId: data.loginId,
          scopes: data.scopes || [],
          expiresType: neverExpires ? 'never' : 'custom',
          expiresTime: neverExpires ? undefined : data.expiresTime,
          isValid: data.isValid !== false
        }
        this.open = true
        this.title = '修改ApiKey'
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (!valid) {
          return
        }
        const payload = {
          title: this.form.title,
          intro: this.form.intro,
          scopes: this.form.scopes,
          expiresTime: this.form.expiresType === 'never' ? -1 : this.form.expiresTime,
          isValid: this.form.isValid
        }
        if (this.form.id != null) {
          payload.id = this.form.id
          updateApiKey(payload).then(() => {
            this.$modal.msgSuccess('修改成功')
            this.open = false
            this.getList()
          })
        } else {
          payload.apiKey = this.form.apiKey
          payload.loginId = this.form.loginId
          addApiKey(payload).then(() => {
            this.$modal.msgSuccess('新增成功')
            this.open = false
            this.getList()
          })
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id ? [row.id] : this.ids
      this.$modal.confirm('是否确认删除选中的ApiKey？').then(() => {
        return delApiKey(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.api-key-cell {
  display: flex;
  align-items: center;
}

.api-key-text {
  flex: 1;
  min-width: 0;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  text-align: left;
}
</style>
