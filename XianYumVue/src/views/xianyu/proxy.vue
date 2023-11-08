<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="83px">
      <el-form-item label="客户端名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入客户端名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="客户端名称" prop="id">
        <el-input
          v-model="queryParams.id"
          placeholder="请输入客户端名称"
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
          v-hasPermi="['xianyu:proxy:save']"
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
          v-hasPermi="['xianyu:proxy:update']"
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
          v-hasPermi="['xianyu:proxy:delete']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-tooltip class="item" effect="light" content="当您对远程配置有做更改的时候，需要把数据刷入系统，保证客户端生效！" placement="top-start">
          <el-button plain v-hasPermi="['xianyu:proxy:flush']" size="mini" icon="el-icon-position" type="info" @click="flushProxy">刷入系统</el-button>
        </el-tooltip>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="proxyList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" align="center" width="50" />
      <el-table-column label="客户端名称" align="center" prop="name" >
        <template v-slot="scope">
          <el-link type="primary" @click="toDetails(scope.row.id)">{{ scope.row.name }}</el-link>
        </template>
      </el-table-column>
      <el-table-column label="客户端秘钥" align="center" prop="id"  width="280"/>
      <el-table-column label="绑定账号" align="center" prop="bindUserName"/>
      <el-table-column label="连接状态" align="center" prop="status" width="180">
        <template v-slot="scope">
          <el-tag v-if="scope.row.status === 0" size="small" type="danger">离线</el-tag>
          <el-tag v-else type="success" size="small">在线</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="登录次数" align="center" prop="loginCount"/>
      <el-table-column label="通知邮箱" align="center" prop="bindEmail" :show-overflow-tooltip="true"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="250px">
        <template v-slot="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-arrow-up"
            @click="sendEmail(scope.row.id)"
            v-hasPermi="['xianyu:proxy:download']"
          >发送配置</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-arrow-down"
            @click="downloadConfig(scope.row.id)"
            v-hasPermi="['xianyu:proxy:download']"
          >生成配置</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['xianyu:proxy:update']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['xianyu:proxy:delete']"
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
        <el-form-item label="客户端名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入客户端名称" />
        </el-form-item>
        <el-form-item label="上线通知" prop="notify">
          <el-radio-group v-model="form.notify">
            <el-radio :label=1>通知</el-radio>
            <el-radio :label=0>不通知</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="绑定用户" prop="bindUserId">
        <el-select v-model="form.bindUserId" placeholder="请绑定用户" filterable clearable>
          <el-option
            v-for="item in proxyUserData"
            :key="item.id"
            :label="`${item.username}`"
            :value="item.id"
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
  </div>
</template>

<script>
import { listProxy, getProxy, delProxy, addProxy, updateProxy ,sendConfigByEmail ,flushProxy ,getProxyBindUser} from "@/api/xianyu/proxy";

export default {
  name: "Proxy",
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
      proxyUserData: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        id: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        name: [
          {required: true, message: '客户端名称不能为空', trigger: 'blur'}
        ],
        notify: [
          {required: true, message: '上线通知不能为空', trigger: 'blur'}
        ],
        bindUserId: [
          {required: true, message: '请绑定用户', trigger: 'blur'}
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    getBindUserData(id){
      getProxyBindUser(id).then(response => {
          this.proxyUserData = response.data
      });
    },
    flushProxy(){
      this.$modal.confirm('确定要把远程配置刷入系统吗？').then(function() {
        return flushProxy();
      }).then(() => {
        this.$modal.msgSuccess("刷入系统成功");
      }).catch(() => {});
    },
    downloadConfig(id){
      let requestParams = {
          "id": id
      }
      this.download('/proxy/downloadConfig', requestParams, `config.ini`)
    },
    sendEmail(id){
      sendConfigByEmail(id).then(response => {
        this.$modal.msgSuccess("发送成功");
      });
    },
    toDetails(id){
      this.$router.push({ name: "Proxy-details", params: {'proxyId': id} });
    },
    numFilter (num) {
      return (Math.round(num*10)/10).toFixed(1)
    },
    /** 查询客户端管理列表 */
    getList() {
      this.loading = true;
      listProxy(this.queryParams).then(response => {
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
        id: null,
        name: null,
        notify: 1
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
      this.getBindUserData(null)
      this.open = true;
      this.title = "添加客户端配置";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      this.getBindUserData(id)
      getProxy(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改客户端配置";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateProxy(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addProxy(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除客户端配置数据？').then(function() {
        return delProxy(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    }
  }
}
</script>
<style rel="stylesheet/scss" lang="scss">
.proxy-table-expand {
  font-size: 0;
  margin-left: 2%;
}
.proxy-table-expand label {
  width: 150px;
  color: #99a9bf;
}
.proxy-table-expand .el-form-item {
  margin-right: 0;
  margin-bottom: 0;
  width: 50%;
}
</style>
