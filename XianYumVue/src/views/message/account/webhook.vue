<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="配置描述" prop="description">
        <el-input
          v-model="queryParams.description"
          placeholder="请输入配置描述"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="账户类型" prop="messageAccountType">
        <el-select v-model="queryParams.messageAccountType" placeholder="请选择账户类型" filterable clearable>
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
          v-hasPermi="['message:webhook:save']"
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
          v-hasPermi="['message:webhook:update']"
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
          v-hasPermi="['message:webhook:delete']"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="proxyList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" align="center" width="50" />
      <el-table-column label="配置描述" align="center" prop="description" />
      <el-table-column label="账户类型" align="center" prop="messageAccountType" :formatter="formatMessageAccountType"/>
      <el-table-column label="WebHook地址" align="center" prop="webHookUrl" :show-overflow-tooltip="true"/>
      <el-table-column label="秘钥" align="center" prop="webHookSecret" :show-overflow-tooltip="true"/>
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
            icon="el-icon-s-flag"
            @click="openSendWebhookDrawer(scope.row)"
            v-hasPermi="['message:webhook:test-send']"
          >测试</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['message:webhook:update']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['message:webhook:delete']"
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

    <!-- 添加或修改客户端管理对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="115px">
        <el-form-item label="配置描述" prop="description">
          <el-input v-model="form.description" placeholder="请输入配置描述" />
        </el-form-item>
        <el-form-item label="WebHook地址" prop="webHookUrl">
          <el-input v-model="form.webHookUrl" placeholder="请输入webHook地址" />
        </el-form-item>
        <el-form-item label="秘钥" prop="webHookSecret">
          <el-input v-model="form.webHookSecret" placeholder="请输入秘钥" />
        </el-form-item>
        <el-form-item label="账户类型" prop="messageAccountType">
          <el-select v-model="form.messageAccountType" placeholder="请选择账户类型" filterable clearable>
            <el-option
              v-for="item in messageAccountTypeList"
              :key="item.code"
              :label="item.name"
              :value="item.code"
            >
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

<!--    webhook账户发信测试-->
    <el-drawer
      title="webhook账户发信测试"
      :visible.sync="drawer"
      :direction="direction">
      <el-form :model="webhookForm" :rules="webhookFormRule" ref="webhookForm"
               @keyup.enter.native="sendWebhook()" label-width="120px" style="width: 85%">
        <el-form-item label="消息编码" prop="messageCode">
          <el-input v-model="webhookForm.messageCode" placeholder="消息编码"></el-input>
        </el-form-item>
        <el-form-item label="发送标题" prop="title">
          <el-input v-model="webhookForm.title" placeholder="发送标题"></el-input>
        </el-form-item>
        <el-form-item label="发送内容" prop="content">
          <el-input type="textarea" :rows="5" v-model="webhookForm.content" placeholder="发送内容"></el-input>
        </el-form-item>
      </el-form>

      <div style="position: absolute;bottom: 0;width: 100%;height: 100px;clear:both;">
        <el-divider></el-divider>
        <div style="float: right;padding-right:20px">
          <el-button type="primary" :loading= 'sendWebhookLoading' @click="sendWebhook()">测试</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { getMessageConfigWebhookPage,
  getMessageConfigWebhookById, addMessageConfigWebhook,
  updateMessageConfigWebhook, delMessageConfigWebhook, sendWebhook} from '@/api/message/webhook'


export default {
  name: "Wechat",
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
      // 客户端管理表格数据
      proxyList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      drawer: false,
      sendWebhookLoading: false,
      direction: 'rtl',
      webhookForm: {
        title: '',
        content: '',
        messageCode: 'SYSTEM_TEST_NOTIFY'
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        messageAccountType: undefined,
        description: undefined
      },
      // 账户类型
      messageAccountTypeList: [],
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        webHookUrl: [
          {required: true, message: 'webHook地址不能为空', trigger: 'blur'}
        ],
        webHookSecret: [
          {required: true, message: '秘钥不能为空', trigger: 'blur'}
        ],
        messageAccountType: [
          {required: true, message: '账户类型不能为空', trigger: 'blur'}
        ],
        description: [
          {required: true, message: '配置描述不能为空', trigger: 'blur'}
        ]
      },
      webhookFormRule: {
        title: [
          {required: true, message: '发送标题不能为空', trigger: 'blur'}
        ],
        content: [
          {required: true, message: '发送内容不能为空', trigger: 'blur'}
        ],
        messageCode: [
          {required: true, message: '消息编码不能为空', trigger: 'blur'}
        ]
      }
    };
  },
  created() {
    this.getMessageAccountTypeList()
    this.getList();
  },
  methods: {
    sendWebhook(){
      this.$refs["webhookForm"].validate(valid => {
        if(valid){
          this.sendWebhookLoading = true
          sendWebhook(this.webhookForm).then(res => {
            this.$modal.msgSuccess("执行成功，请看收信");
            this.sendWebhookLoading = false
          })
        }
      });
    },
    openSendWebhookDrawer(row){
      this.resetForm("webhookForm");
      this.webhookForm.messageCode = 'SYSTEM_TEST_NOTIFY'
      this.drawer = true
      this.sendWebhookLoading = false
      this.webhookForm.messageConfigId = row.id
      this.webhookForm.messageAccountType = row.messageAccountType
    },
    getMessageAccountTypeList() {
      this.getPublicSystemConstant('message_account_type').then(res => {
        let dataList = JSON.parse(res.data.constantValue)
        this.messageAccountTypeList = dataList.filter(item => item.type === 'webhook');
      });
    },
    /** 查询客户端管理列表 */
    getList() {
      this.loading = true;
      getMessageConfigWebhookPage(this.queryParams).then(response => {
        this.proxyList = response.data;
        this.total = response.total;
        this.loading = false;
      });
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
        corpId: undefined,
        corpSecret: undefined,
        agentId: undefined,
        description: undefined
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
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加消息类型配置";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getMessageConfigWebhookById(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改消息类型配置";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateMessageConfigWebhook(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addMessageConfigWebhook(this.form).then(response => {
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
      const ids = row.id ?[row.id] : this.ids;
      this.$modal.confirm('是否确认删除Webhook配置数据？').then(function() {
        return delMessageConfigWebhook(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    formatMessageAccountType(row, column, cell){
      if (!cell) {
        return ''
      }
      let name = cell;
      this.messageAccountTypeList.filter(function (item){
        if(item.code === cell){
          name = item.name
        }
      })
      return name
    }
  }
}
</script>
