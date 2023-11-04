<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="85px">
      <el-form-item label="客户端名称" prop="proxyName">
        <el-input
          v-model="queryParams.proxyName"
          placeholder="请输入客户端秘钥"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="客户端秘钥" prop="proxyId">
        <el-input
          v-model="queryParams.proxyId"
          placeholder="请输入客户端秘钥"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="公网端口" prop="inetPort">
        <el-input
          v-model="queryParams.inetPort"
          placeholder="请输入公网端口  "
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
          v-hasPermi="['xianyu:proxy-details:save']"
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
          v-hasPermi="['xianyu:proxy-details:update']"
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
          v-hasPermi="['xianyu:proxy-details:delete']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-tooltip class="item" effect="light" content="当您对远程配置有做更改的时候，需要把数据刷入系统，保证客户端生效！" placement="top-start">
          <el-button plain v-hasPermi="['xianyu:proxy:flush']" size="mini" icon="el-icon-position" type="info" @click="flushProxy">刷入系统</el-button>
        </el-tooltip>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="detailsList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" align="center" width="50"/>
      <el-table-column label="客户端名称" align="center" prop="proxyName" />
      <el-table-column label="代理名称" align="center" prop="name" />
      <el-table-column label="公网端口" align="center" prop="inetPort" />
      <el-table-column label="本地代理信息" align="center" prop="lan" />
      <el-table-column label="写入量" align="center" prop="writeBytesStr" />
      <el-table-column label="读取量" align="center" prop="readBytesStr" />
      <el-table-column label="当前连接数" align="center" prop="connectCount" />
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
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['xianyu:proxy-details:update']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['xianyu:proxy-details:delete']"
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

    <!-- 添加或修改客户端配置详细对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="客户端名称" prop="proxyId">
          <el-select v-model="form.proxyId" placeholder="请选择客户端" filterable :disabled="form.id">
            <el-option
              v-for="item in proxyList"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="代理名称" prop="name">
          <el-input v-model="form.name" placeholder="代理名称"></el-input>
        </el-form-item>
        <el-form-item label="公网端口" prop="inetPort">
          <el-input v-model="form.inetPort" placeholder="公网端口(不填默认生成)" onkeyup="value=value.replace(/[^\d]/g,'')"
                    @blur="inetPortChange"></el-input>
        </el-form-item>
        <el-form-item label="代理地址" prop="lan">
          <el-input v-model="form.lan" placeholder="代理地址，格式ip:port"></el-input>
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
import { listDetails, getDetails, delDetails, addDetails, updateDetails } from "@/api/xianyu/proxyDetails";
import { flushProxy ,getAllProxyList} from "@/api/xianyu/proxy";

export default {
  name: "Details",
  data() {
    return {
      proxyList: [],
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
      // 客户端配置详细表格数据
      detailsList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        inetPort: null,
        proxyId: null,
        proxyName: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        name: [
          {required: true, message: '代理名称不能为空', trigger: 'blur'}
        ],
        lan: [
          {required: true, message: '代理地址不能为空', trigger: 'blur'}
        ],
        proxyId: [
          {required: true, message: '请选择客户端', trigger: 'blur'}
        ]
      }
    };
  },
  created() {
    this.queryParams.proxyId = this.$route.params.proxyId
    this.getList();
    this.getAllProxyList();
  },
  methods: {
    getAllProxyList(){
      getAllProxyList().then(res =>{
        this.proxyList = res.data
      })
    },
    inetPortChange (e) {
      this.form.inetPort = e.target.value
    },
    flushProxy(){
      this.$modal.confirm('确定要把远程配置刷入系统吗？').then(function() {
        return flushProxy();
      }).then(() => {
        this.$modal.msgSuccess("刷入系统成功");
      }).catch(() => {});
    },
    /** 查询客户端配置详细列表 */
    getList() {
      this.loading = true;
      listDetails(this.queryParams).then(response => {
        this.detailsList = response.data;
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
        proxyId: null,
        inetPort: null,
        lan: null,
        name: null
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
      this.queryParams.proxyId = undefined
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
      this.title = "添加代理配置";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getDetails(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改代理配置";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateDetails(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addDetails(this.form).then(response => {
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
      const ids = row.id ? [row.id] : this.ids;
      this.$modal.confirm('是否确认删除代理配置数据？').then(function() {
        return delDetails(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    }
  }
};
</script>
