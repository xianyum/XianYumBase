<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="消息编码" prop="messageCode">
        <el-input
          v-model="queryParams.messageCode"
          placeholder="请输入消息编码"
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
          v-hasPermi="['message:wechat:save']"
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
          v-hasPermi="['message:wechat:update']"
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
          v-hasPermi="['message:wechat:delete']"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="proxyList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" align="center" width="50" />
      <el-table-column label="配置描述" align="center" prop="description" />
      <el-table-column label="企业ID" align="center" prop="corpId" />
      <el-table-column label="应用ID" align="center" prop="agentId" />
      <el-table-column label="应用秘钥" align="center" prop="corpSecret" :show-overflow-tooltip="true"/>
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
            @click="openSendWechatDrawer(scope.row)"
            v-hasPermi="['message:wechat:test-send']"
          >测试</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['message:wechat:update']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['message:wechat:delete']"
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
          <el-input v-model="form.description" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="企业ID" prop="corpId">
          <el-input v-model="form.corpId" placeholder="请输入企业ID" />
        </el-form-item>
        <el-form-item label="应用ID" prop="agentId">
          <el-input v-model="form.agentId" placeholder="请输入应用ID" />
        </el-form-item>
        <el-form-item label="应用秘钥" prop="corpSecret">
          <el-input v-model="form.corpSecret" placeholder="请输入应用秘钥"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

<!--    微信账号发送测试-->
    <el-drawer
      title="微信账户发信测试"
      :visible.sync="drawer"
      :direction="direction">
      <el-form :model="wechatForm" :rules="wechatFormRule" ref="wechatForm"
               @keyup.enter.native="sendWechat()" label-width="120px" style="width: 85%">
        <el-form-item label="消息编码" prop="messageCode">
          <el-input v-model="wechatForm.messageCode" placeholder="消息编码"></el-input>
        </el-form-item>
        <el-form-item label="发送标题" prop="title">
          <el-input v-model="wechatForm.title" placeholder="发送标题"></el-input>
        </el-form-item>
        <el-form-item label="发送内容" prop="content">
          <el-input type="textarea" :rows="5" v-model="wechatForm.content" placeholder="发送内容"></el-input>
        </el-form-item>
        <el-form-item label="发送用户" prop="wechatToUser">
          <el-input v-model="wechatForm.wechatToUser" placeholder="发送用户"></el-input>
        </el-form-item>
      </el-form>

      <div style="position: absolute;bottom: 0;width: 100%;height: 100px;clear:both;">
        <el-divider></el-divider>
        <div style="float: right;padding-right:20px">
          <el-button type="primary" :loading= 'sendWechatLoading' @click="sendWechat()">测试</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import { getMessageWechatConfigPage,
  getMessageWechatConfigById, addMessageWechatConfig,
  updateMessageWechatConfig, delMessageWechatConfig,sendWechat} from '@/api/message/wechat'



export default {
  name: "Wechat",
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
      proxyList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        messageCode: undefined,
        description: undefined
      },
      wechatForm: {
        wechatToUser: '',
        title: '',
        content: '',
        messageConfigId: '',
        messageCode: 'SYSTEM_TEST_NOTIFY'
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        corpId: [
          {required: true, message: '企业id不能为空', trigger: 'blur'}
        ],
        corpSecret: [
          {required: true, message: '应用秘钥不能为空', trigger: 'blur'}
        ],
        agentId: [
          {required: true, message: '应用id不能为空', trigger: 'blur'}
        ],
        description: [
          {required: true, message: '配置描述不能为空', trigger: 'blur'}
        ]
      },
      wechatFormRule: {
        title: [
          {required: true, message: '发送标题不能为空', trigger: 'blur'}
        ],
        content: [
          {required: true, message: '发送内容不能为空', trigger: 'blur'}
        ],
        wechatToUser: [
          {required: true, message: '发送用户不能为空', trigger: 'blur'}
        ],
      }
    }
  },
  created() {
    this.getList();
  },
  methods: {
    sendWechat(){
      this.$refs["wechatForm"].validate(valid => {
        if(valid){
          this.sendWechatLoading = true
          sendWechat(this.wechatForm).then(res => {
            this.$modal.msgSuccess("执行成功，请看收信");
          }).finally(() => {
            // 最后一定会执行的代码
            this.sendWechatLoading = false;
          });
        }
      });
    },
    openSendWechatDrawer(row){
      this.resetForm("wechatForm");
      this.wechatForm.messageCode = 'SYSTEM_TEST_NOTIFY'
      this.drawer = true
      this.sendWechatLoading = false
      this.wechatForm.messageConfigId = row.id
    },
    /** 查询客户端管理列表 */
    getList() {
      this.loading = true;
      getMessageWechatConfigPage(this.queryParams).then(response => {
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
      getMessageWechatConfigById(id).then(response => {
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
            updateMessageWechatConfig(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addMessageWechatConfig(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除企业微信配置数据？').then(function() {
        return delMessageWechatConfig(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    }
  }
}
</script>
