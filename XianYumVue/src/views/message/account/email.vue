<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="邮箱账号" prop="emailUserName">
        <el-input
          v-model="queryParams.emailUserName"
          placeholder="请输入邮箱账号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="配置描述" prop="description">
        <el-input
          v-model="queryParams.description"
          placeholder="请输入配置描述"
          clearable
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
          v-hasPermi="['message:email:save']"
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
          v-hasPermi="['message:email:update']"
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
          v-hasPermi="['message:email:delete']"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="proxyList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" align="center" width="50" />
      <el-table-column label="配置描述" align="center" prop="description" />
      <el-table-column label="邮箱账号" align="center" prop="emailUserName" />
      <el-table-column label="邮箱密码" align="center" prop="emailUserPassword" />
      <el-table-column label="smtp地址" align="center" prop="emailSmtp"/>
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
            @click="openSendEmailDrawer(scope.row)"
            v-hasPermi="['message:email:test-send']"
          >测试</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['message:email:update']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['message:email:delete']"
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
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="95px">
        <el-form-item label="配置描述" prop="description">
          <el-input v-model="form.description" placeholder="请输入配置描述" />
        </el-form-item>
        <el-form-item label="邮箱账号" prop="emailUserName">
          <el-input v-model="form.emailUserName" placeholder="请输入应用ID" />
        </el-form-item>
        <el-form-item label="邮箱密码" prop="emailUserPassword">
          <el-input v-model="form.emailUserPassword" placeholder="请输入应用秘钥"/>
        </el-form-item>
        <el-form-item label="smtp地址" prop="emailSmtp">
          <el-input v-model="form.emailSmtp" placeholder="请输入smtp地址" />
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!--    测试发送-->
    <el-drawer
      title="邮箱账户发信测试"
      :visible.sync="drawer"
      :direction="direction">
      <el-form :model="emailForm" :rules="emailFormRule" ref="emailForm"
               @keyup.enter.native="sendEmail()" label-width="120px" style="width: 85%">
        <el-form-item label="消息编码" prop="messageCode">
          <el-input v-model="emailForm.messageCode" placeholder="消息编码"></el-input>
        </el-form-item>
        <el-form-item label="发送标题" prop="title">
          <el-input v-model="emailForm.title" placeholder="发送标题"></el-input>
        </el-form-item>
        <el-form-item label="发送内容" prop="content">
          <el-input type="textarea" :rows="5" v-model="emailForm.content" placeholder="发送内容"></el-input>
        </el-form-item>
        <el-form-item label="发送用户" prop="emailToUser">
          <el-input v-model="emailForm.emailToUser" placeholder="发送用户"></el-input>
        </el-form-item>
      </el-form>

      <div style="position: absolute;bottom: 0;width: 100%;height: 100px;clear:both;">
        <el-divider></el-divider>
        <div style="float: right;padding-right:20px">
          <el-button type="primary" :loading= 'sendEmailLoading' @click="sendEmail()">测试</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { getMessageEmailConfigPage,
  getMessageEmailConfigById, addMessageEmailConfig,
  updateMessageEmailConfig, delMessageEmailConfig,sendEmail } from '@/api/message/email'


export default {
  name: "Email",
  data() {
    return {
      sendEmailLoading: false,
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
      proxyList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        emailUserName: undefined,
        description: undefined
      },
      emailForm: {
        emailToUser: '',
        title: '',
        content: '',
        messageConfigId: '',
        messageCode: 'SYSTEM_TEST_NOTIFY'
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        emailSmtp: [
          {required: true, message: 'smtp地址不能为空', trigger: 'blur'}
        ],
        emailUserName: [
          {required: true, message: '邮箱账号不能为空', trigger: 'blur'}
        ],
        emailUserPassword: [
          {required: true, message: '邮箱密码不能为空', trigger: 'blur'}
        ],
        description: [
          {required: true, message: '配置描述不能为空', trigger: 'blur'}
        ]
      },
      emailFormRule: {
        title: [
          {required: true, message: '发送标题不能为空', trigger: 'blur'}
        ],
        content: [
          {required: true, message: '发送内容不能为空', trigger: 'blur'}
        ],
        emailToUser: [
          {required: true, message: '发送用户不能为空', trigger: 'blur'}
        ],
        messageCode: [
          {required: true, message: '消息编码不能为空', trigger: 'blur'}
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    sendEmail(){
      this.$refs["emailForm"].validate(valid => {
        if(valid){
          this.sendEmailLoading = true
          sendEmail(this.emailForm).then(res => {
            this.$modal.msgSuccess("执行成功，请看收信");
            this.sendEmailLoading = false
          })
        }
      });
    },
    openSendEmailDrawer(row){
      this.resetForm("emailForm");
      this.emailForm.messageCode = 'SYSTEM_TEST_NOTIFY'
      this.drawer = true
      this.sendEmailLoading = false
      this.emailForm.messageConfigId = row.id
    },
    /** 查询客户端管理列表 */
    getList() {
      this.loading = true;
      getMessageEmailConfigPage(this.queryParams).then(response => {
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
        emailUserName: undefined,
        emailUserPassword: undefined,
        emailSmtp: undefined,
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
      getMessageEmailConfigById(id).then(response => {
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
            updateMessageEmailConfig(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addMessageEmailConfig(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除邮箱配置数据？').then(function() {
        return delMessageEmailConfig(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    }
  }
}
</script>
