<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
      <el-form-item label="角色名称" prop="roleName">
        <el-input
          v-model="queryParams.roleName"
          placeholder="请输入角色名称"
          clearable
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="角色编码" prop="roleCode">
        <el-input
          v-model="queryParams.roleCode"
          placeholder="请输入角色编码"
          clearable
          style="width: 240px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="角色状态"
          clearable
          style="width: 240px"
        >
          <el-option
            v-for="dict in dict.type.sys_normal_disable"
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
          v-hasPermi="['system:role:save']"
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
          v-hasPermi="['system:role:update']"
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
          v-hasPermi="['system:role:delete']"
        >删除
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="roleList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="角色编码" prop="roleCode"/>
      <el-table-column label="角色名称" prop="roleName"/>
      <el-table-column label="数据范围" prop="dataScope">
        <template v-slot="scope">
          <dict-tag :options="dict.type.sys_data_scope" :value="scope.row.dataScope"/>
        </template>
      </el-table-column>
      <el-table-column label="显示顺序" prop="roleSort"/>
      <el-table-column label="状态" align="center">
        <template v-slot="scope">
          <el-switch
            v-model="scope.row.status"
            :active-value=0
            :inactive-value=1
            @change="handleStatusChange(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" :show-overflow-tooltip="true"/>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="250px">
        <template v-slot="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-circle-plus-outline"
            @click="authorizeRole(scope.row)"
            v-if="scope.row.roleCode !== 'admin'"
            v-hasPermi="['system:role:authorize']"
          >授权
          </el-button>
          <el-popover
            placement="left"
            trigger="click">
            <el-table :data="userData">
              <el-table-column width="100" property="username" label="账号"></el-table-column>
              <el-table-column width="170" property="nickName" label="用户名称"></el-table-column>
            </el-table>
            <el-button style="margin-left: 6px" v-hasPermi="['system:role:user']" slot="reference" size="mini"
                       type="text" icon="el-icon-user" @click="getUser(scope.row)">已分配用户</el-button>
          </el-popover>
          <el-button
            style="margin-left: 6px"
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:role:update']"
            v-if="scope.row.roleCode !== 'admin'"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:role:delete']"
            v-if="scope.row.roleCode !== 'admin'"
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

    <!-- 添加或修改角色配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item prop="roleCode">
          <span slot="label">
            <el-tooltip content="用于接口和按钮权限控制字符！" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
            角色编码
          </span>
          <el-input v-model="form.roleCode" placeholder="请输入角色编码"/>
        </el-form-item>
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称"/>
        </el-form-item>
        <el-form-item label="角色顺序" prop="roleSort">
          <el-input-number v-model="form.roleSort" controls-position="right" :min="0"/>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in dict.type.sys_normal_disable"
              :key="dict.value"
              :label="dict.value"
            >{{ dict.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <!--        <el-form-item label="菜单权限">-->
        <!--          <el-checkbox v-model="menuExpand" @change="handleCheckedTreeExpand($event, 'menu')">展开/折叠</el-checkbox>-->
        <!--          <el-checkbox v-model="menuNodeAll" @change="handleCheckedTreeNodeAll($event, 'menu')">全选/全不选</el-checkbox>-->
        <!--          <el-tree-->
        <!--            class="tree-border"-->
        <!--            :data="menuOptions"-->
        <!--            show-checkbox-->
        <!--            ref="menu"-->
        <!--            node-key="id"-->
        <!--            empty-text="加载中，请稍候"-->
        <!--            :props="defaultProps"-->
        <!--          ></el-tree>-->
        <!--        </el-form-item>-->
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 添加或修改角色配置对话框 -->
    <el-dialog title="角色授权" :visible.sync="authorizeRoleTag" width="800px" append-to-body>
      <el-tabs tab-position="left" v-model="activeName" @tab-click="handleMenuOrDataPermissionClick">
        <el-tab-pane label="菜单权限" name="menuPermissionTab">
          <el-form ref="menuPermissionForm" :model="menuPermissionForm">
            <el-form-item>
              <el-checkbox v-model="menuExpand" @change="handleCheckedTreeExpand($event, 'menu')">展开/折叠
              </el-checkbox>
              <el-checkbox v-model="menuNodeAll" @change="handleCheckedTreeNodeAll($event, 'menu')">全选/全不选
              </el-checkbox>
              <el-checkbox v-model="menuCheckStrictly" @change="handleCheckedTreeConnect($event, 'menu')">父子联动
              </el-checkbox>
              <el-tree
                class="tree-border"
                :data="menuOptions"
                show-checkbox
                :check-strictly="!menuCheckStrictly"
                ref="menu"
                node-key="id"
                empty-text="加载中，请稍候"
                :props="defaultProps"
              ></el-tree>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="数据权限" name="dataPermissionTab">
          <el-form ref="dataPermissionForm" :model="dataPermissionForm">
            <el-form-item>
              <el-radio-group  v-model="dataPermissionForm.dataScope">
                <el-radio
                  v-for="dict in dict.type.sys_data_scope"
                  :key="dict.value"
                  :label="dict.value"
                >{{ dict.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="saveMenuOrDataPermission">确 定</el-button>
        <el-button @click="cancelAuthorizeRoleTag">取 消</el-button>
      </div>
    </el-dialog>


  </div>
</template>

<script>
import {listRole, getRole, delRole, addRole, updateRole, changeRoleStatus ,changeDataScope,authorizationMenu,getUserByRoleId} from "@/api/system/role";
import {treeselect as menuTreeselect, roleMenuTreeselect} from "@/api/system/menu";

export default {
  name: "Role",
  dicts: ['sys_normal_disable', 'sys_data_scope'],
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
      // 角色表格数据
      roleList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      authorizeRoleTag: false,
      activeName: 'menuPermissionTab',
      // 是否显示弹出层（数据权限）
      openDataScope: false,
      menuExpand: false,
      menuNodeAll: false,
      menuCheckStrictly: true,
      deptExpand: true,
      deptNodeAll: false,
      // 日期范围
      dateRange: [],
      // 菜单列表
      menuOptions: [],
      // 部门列表
      deptOptions: [],
      // 已分配用户数据
      userData: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        roleName: undefined,
        roleCode: undefined,
        status: undefined
      },
      dataPermissionForm:{
        id: undefined,
        dataScope: undefined
      },
      menuPermissionForm:{
        menuIds: [],
        id: undefined
      },
      // 表单参数
      form: {},
      defaultProps: {
        children: "children",
        label: "label"
      },
      // 表单校验
      rules: {
        roleName: [
          {required: true, message: "角色名称不能为空", trigger: "blur"}
        ],
        roleCode: [
          {required: true, message: "角色编码不能为空", trigger: "blur"}
        ],
        roleSort: [
          {required: true, message: "角色顺序不能为空", trigger: "blur"}
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    getUser(row){
      getUserByRoleId(row.id).then(response => {
        this.userData = response.data
      });
    },
    // 树权限（父子联动）
    handleCheckedTreeConnect(value, type) {
      if (type == 'menu') {
        this.menuCheckStrictly = value ? true: false;
      }
    },
    saveMenuOrDataPermission(){
      if(this.activeName === 'menuPermissionTab'){
        // 提交菜单权限
        this.submitMenuPermission();
      }else if(this.activeName === 'dataPermissionTab'){
        // 提交数据权限
        this.submitDataPermission();
      }else{
        this.$modal.msgError("暂不支持此权限类型！");
      }
    },
    submitMenuPermission(){
      this.menuPermissionForm.menuIds = this.getMenuAllCheckedKeys();
      authorizationMenu(this.menuPermissionForm).then(response => {
        this.$modal.msgSuccess("授权菜单权限成功");
        this.authorizeRoleTag = false;
        this.menuCheckStrictly = true
        this.getList();
      });
    },
    submitDataPermission(){
      changeDataScope(this.dataPermissionForm).then(response => {
        this.$modal.msgSuccess("授权数据权限成功");
        this.authorizeRoleTag = false;
        this.menuCheckStrictly = true
        this.getList();
      });
    },
    cancelAuthorizeRoleTag(){
      this.menuCheckStrictly = true
      this.authorizeRoleTag = false
      this.resetMenuPermissionForm()
      this.resetDataPermissionForm()
    },
    handleMenuOrDataPermissionClick(tab, event) {

    },
    authorizeRole(row) {
      const roleMenu = this.getRoleMenuTreeselect(row.id);
      getRole(row.id).then(response => {
        this.dataPermissionForm.dataScope = response.data.dataScope;
        this.dataPermissionForm.id = response.data.id
        this.menuPermissionForm.id = response.data.id
        this.authorizeRoleTag = true
        this.$nextTick(() => {
          roleMenu.then(res => {
            let checkedKeys = res.data.checkedKeys
            checkedKeys.forEach((v) => {
              this.$nextTick(() => {
                this.$refs.menu.setChecked(v, true, false);
              })
            })
          });
        });
      })
    },
    /** 查询角色列表 */
    getList() {
      this.loading = true;
      listRole(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
          this.roleList = response.data;
          this.total = response.total;
          this.loading = false;
        }
      );
    },
    /** 查询菜单树结构 */
    getMenuTreeselect() {
      menuTreeselect().then(response => {
        this.menuOptions = response.data;
      });
    },
    // 所有菜单节点数据
    getMenuAllCheckedKeys() {
      // 目前被选中的菜单节点
      let checkedKeys = this.$refs.menu.getCheckedKeys();
      // 半选中的菜单节点
      let halfCheckedKeys = this.$refs.menu.getHalfCheckedKeys();
      checkedKeys.unshift.apply(checkedKeys, halfCheckedKeys);
      return checkedKeys;
    },
    /** 根据角色ID查询菜单树结构 */
    getRoleMenuTreeselect(roleId) {
      return roleMenuTreeselect(roleId).then(response => {
        this.menuOptions = response.data.menus;
        return response;
      });
    },
    resetDataPermissionForm(){
      this.dataPermissionForm = {
        id: undefined,
        dataScope: undefined
      }
    },
    resetMenuPermissionForm(){
      if (this.$refs.menu != undefined) {
        this.$refs.menu.setCheckedKeys([]);
      }
      this.menuPermissionForm = {
        menuIds: [],
        id: undefined
      }
    },
    // 角色状态修改
    handleStatusChange(row) {
      let text = row.status === 0 ? "启用" : "停用";
      this.$modal.confirm('确认要' + text + '【' + row.roleName + '】角色吗？').then(function () {
        return changeRoleStatus(row.id, row.status);
      }).then(() => {
        this.$modal.msgSuccess(text + "成功");
      }).catch(function () {
        row.status = row.status === 0 ? 1 : 0;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      if (this.$refs.menu != undefined) {
        this.$refs.menu.setCheckedKeys([]);
      }
      this.menuExpand = false,
        this.menuNodeAll = false,
        this.deptExpand = true,
        this.deptNodeAll = false,
        this.form = {
          id: undefined,
          roleName: undefined,
          roleCode: undefined,
          roleSort: 0,
          status: "0",
          menuIds: [],
          deptIds: [],
          menuCheckStrictly: true,
          deptCheckStrictly: true,
          remark: undefined
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
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length != 1
      this.multiple = !selection.length
    },
    // 树权限（展开/折叠）
    handleCheckedTreeExpand(value, type) {
      if (type == 'menu') {
        let treeList = this.menuOptions;
        for (let i = 0; i < treeList.length; i++) {
          this.$refs.menu.store.nodesMap[treeList[i].id].expanded = value;
        }
      }
    },
    // 树权限（全选/全不选）
    handleCheckedTreeNodeAll(value, type) {
      if (type == 'menu') {
        this.$refs.menu.setCheckedNodes(value ? this.menuOptions : []);
      }
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加角色";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const roleId = row.id || this.ids
      // const roleMenu = this.getRoleMenuTreeselect(roleId);
      getRole(roleId).then(response => {
        if (response.data) {
          response.data.status = response.data.status.toString()
        }
        this.form = response.data;
        this.open = true;
        // this.$nextTick(() => {
        //   roleMenu.then(res => {
        //     let checkedKeys = res.checkedKeys
        //     checkedKeys.forEach((v) => {
        //         this.$nextTick(()=>{
        //             this.$refs.menu.setChecked(v, true ,false);
        //         })
        //     })
        //   });
        // });
        this.title = "修改角色";
      });
    },
    /** 提交按钮 */
    submitForm: function () {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != undefined) {
            // this.form.menuIds = this.getMenuAllCheckedKeys();
            updateRole(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            // this.form.menuIds = this.getMenuAllCheckedKeys();
            addRole(this.form).then(response => {
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
      const roleIds = row.id ? [row.id] : this.ids;
      this.$modal.confirm('是否确认删除角色数据？').then(function () {
        return delRole(roleIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      });
    }
  }
};
</script>
