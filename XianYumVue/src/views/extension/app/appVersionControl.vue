<template>
  <div class="app-container">
    <!-- 查询表单 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="70px">
      <el-form-item label="应用ID" prop="appId">
        <el-input
          v-model="queryParams.appId"
          placeholder="请输入应用唯一标识"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['app:version_control:save']"
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
          v-hasPermi="['app:version_control:update']"
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
          v-hasPermi="['app:version_control:delete']"
        >删除
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="appVersionList" @selection-change="handleSelectionChange">

      <el-table-column type="expand" width="50">
        <template v-slot="scope">
          <div class="expand-row-content">
              <!-- 更新日志：适配\n换行，为空不显示 -->
            <div class="expand-item" v-if="scope.row.updateLog && scope.row.updateLog.trim()">
              <label class="expand-label">更新日志：</label>
              <!-- 双重兼容：replace转<br/> + white-space: pre-wrap兜底 -->
              <div class="log-content" v-html="formatLineBreak(scope.row.updateLog)"></div>
            </div>

            <!-- 备注：为空不显示 -->
              <div class="expand-item">
                <label class="expand-label">备注：</label>
                <div class="remark-content">{{ scope.row.remark }}</div>
              </div>
          </div>
        </template>
      </el-table-column>

      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="序号" type="index" align="center" width="50"/>
      <el-table-column label="应用ID" align="center" prop="appId"/>
      <el-table-column label="版本号" align="center" prop="version"/>
      <el-table-column label="更新包类型" align="center" prop="packageType">
        <template v-slot="scope">
          <el-tag v-if="scope.row.packageType === 1" effect="plain" type="success">热更新包(wgt)</el-tag>
          <el-tag v-else type="primary" effect="plain">整包安装包(apk)</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="更新标题" align="center" prop="updateTitle" :show-overflow-tooltip="true"/>
      <el-table-column label="强制更新" align="center" prop="isForceUpdate">
        <template v-slot="scope">
          <el-tag v-if="scope.row.isForceUpdate === 0" effect="plain" type="danger">是</el-tag>
          <el-tag v-else type="success" effect="plain">否</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['app:version_control:update']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['app:version_control:delete']"
          >删除
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-download"
            @click="handleDownload(scope.row)"
          >下载
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 新增/修改对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <!-- 第一行：应用ID + 版本号 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="应用ID" prop="appId">
              <el-input v-model="form.appId" :disabled="true" placeholder="请输入应用唯一标识"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="版本号" prop="version">
              <el-input v-model="form.version" placeholder="请输入版本号（如1.0.1）"/>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 第二行：更新包类型 + 强制更新 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="更新包类型" prop="packageType">
              <el-select v-model="form.packageType" placeholder="请选择更新包类型">
                <el-option label="热更新包(wgt)" :value="1"/>
                <el-option label="整包安装包(apk)" :value="2"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="强制更新" prop="isForceUpdate">
              <el-radio-group v-model="form.isForceUpdate" style="width: 100%">
                <el-radio :label="0" style="margin-right: 20px;">是</el-radio>
                <el-radio :label="1">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 第三行：更新包文件（独占一行） -->
        <el-form-item label="更新包文件" prop="packageFileId">
          <el-upload
            ref="upload"
            :limit="1"
            :headers="uploadHeaders"
            accept=".wgt,.apk"
            :file-list="fileList"
            :action="uploadUrl"
            :on-success="handleFileSuccess"
            :before-upload="beforeUpload"
            drag
            style="width: 100%"
          >
            <i class="el-icon-upload"/>
            <div class="el-upload__text">
              将文件拖到此处，或<em>点击上传</em>
            </div>
            <div class="el-upload__tip" slot="tip">
              提示：仅支持上传.wgt/.apk格式文件，单个文件大小不超过100MB
            </div>
          </el-upload>
        </el-form-item>

        <!-- 第四行：更新标题（独占一行） -->
        <el-form-item label="更新标题" prop="updateTitle">
          <el-input v-model="form.updateTitle" placeholder="请输入更新标题（如V1.0.1 功能优化）"/>
        </el-form-item>

        <!-- 第五行：更新日志（独占一行） -->
        <el-form-item label="更新日志" prop="updateLog">
          <el-input
            v-model="form.updateLog"
            type="textarea"
            :rows="5"
            @keydown.enter="handleLogEnter"
            placeholder="请输入更新日志内容（如1.修复XX问题；2.新增XX功能）"
          />
        </el-form-item>

        <!-- 第六行：备注（独占一行） -->
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息（选填）"
          />
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
import { getToken } from "@/utils/auth";
import {getAppVersionControlPage, updateAppVersionControl, saveAppVersionControl, delAppVersionControl, getAppVersionControl} from '@/api/extension/appVersionControl'
import {presignedFileUrl} from "@/api/system/file";

export default {
  name: 'appVersion',
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
      // 版本列表数据
      appVersionList: [],
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      open: false,
      // 文件上传相关
      uploadUrl: process.env.VUE_APP_BASE_API + "/xym-system/v1/file/uploadFile",
      fileList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        appId: undefined
      },
      uploadHeaders: {},
      // 表单参数
      form: {
        id: undefined,
        appId: '__UNI__B40ED08',
        version: undefined,
        packageType: 1,
        packageFileId: undefined,
        updateLog: undefined,
        updateTitle: undefined,
        isForceUpdate: 0, // 默认非强制更新
        remark: undefined
      },
      // 表单校验规则
      rules: {
        appId: [
          { required: true, message: '应用ID不能为空', trigger: 'blur' }
        ],
        version: [
          { required: true, message: '版本号不能为空', trigger: 'blur' }
        ],
        packageType: [
          { required: true, message: '更新包类型不能为空', trigger: 'change' }
        ],
        packageFileId: [
          { required: true, message: '请上传更新包文件', trigger: 'change' }
        ],
        updateTitle: [
          { required: true, message: '更新标题不能为空', trigger: 'blur' }
        ],
        isForceUpdate: [
          { required: true, message: '请选择是否强制更新', trigger: 'change' }
        ],
        updateLog: [
          { required: true, message: '更新日志不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    this.uploadHeaders = {
      'Authorization': 'Bearer ' + getToken()
    }
  },
  created() {
    this.getList()
  },
  methods: {
    handleDownload(row) {
      // 无文件ID直接提示
      if (!row.packageFileId) {
        this.$modal.msgError('该版本未上传更新包，无法下载');
        return;
      }
      // 加载提示
      this.$modal.loading("正在获取下载地址...");

      // 1. 调用接口获取预签名URL
      presignedFileUrl(row.packageFileId).then(response => {
        this.$modal.closeLoading();
        if (response.code === 200 && response.data) {
          const customFileName = response.data.originalFilename;

          // 2. 关键：通过XMLHttpRequest获取文件流（兼容预签名URL跨域）
          const xhr = new XMLHttpRequest();
          xhr.open('GET', response.data.fileUrl, true);
          xhr.responseType = 'blob'; // 以Blob形式接收文件

          // 3. 请求成功后处理
          xhr.onload = function() {
            if (xhr.status === 200) {
              // 创建Blob URL
              const blobUrl = URL.createObjectURL(xhr.response);
              // 创建a标签触发下载
              const a = document.createElement('a');
              a.href = blobUrl;
              a.download = customFileName; // 强制指定文件名（必生效）
              a.style.display = 'none';
              document.body.appendChild(a);
              a.click();

              // 4. 清理资源（避免内存泄漏）
              document.body.removeChild(a);
              URL.revokeObjectURL(blobUrl);
            } else {
              this.$modal.msgError('文件下载失败，请重试');
            }
          }.bind(this);

          // 5. 处理请求异常
          xhr.onerror = function() {
            this.$modal.msgError('获取文件失败，请检查网络或链接有效性');
          }.bind(this);

          // 6. 发送请求（预签名URL无需额外请求头，如有需要可加）
          xhr.send();
        } else {
          this.$modal.msgError('获取下载地址失败');
        }
      }).catch(() => {
        this.$modal.closeLoading();
        this.$modal.msgError('获取下载地址异常，请重试');
      });
    },
    formatLineBreak(text) {
      if (!text) return '';
      // 先替换\r\n（Windows换行），再替换\n（Unix换行），最后处理转义的\\n
      return text.replace(/\r\n/g, '<br/>').replace(/\n/g, '<br/>').replace(/\\n/g, '<br/>');
    },
    handleLogEnter(e) {
      // 阻止回车触发表单提交
      e.preventDefault();
      // 在光标位置插入换行符
      const el = e.target;
      const start = el.selectionStart;
      const end = el.selectionEnd;
      this.form.updateLog =
        this.form.updateLog.substring(0, start) +
        '\n' +
        this.form.updateLog.substring(end);
      // 重置光标位置
      el.selectionStart = el.selectionEnd = start + 1;
    },
    /** 查询APP版本列表 */
    getList() {
      this.loading = true
      getAppVersionControlPage(this.queryParams).then(response => {
        this.appVersionList = response.data
        this.total = response.total
        this.loading = false
      })
    },

    /** 文件上传前置校验 */
    beforeUpload(file) {
      // 校验文件类型
      const fileName = file.name
      const fileExt = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase()
      if (fileExt !== 'wgt' && fileExt !== 'apk') {
        this.$modal.msgError('仅支持上传.wgt/.apk格式的文件！')
        return false
      }
      // 校验文件大小（100MB）
      const isLt100M = file.size / 1024 / 1024 < 100
      if (!isLt100M) {
        this.$modal.msgError('上传文件大小不能超过100MB！')
        return false
      }
      // 设置上传请求头（携带token）
      this.uploadHeaders = {
        'Authorization': 'Bearer ' + getToken()
      };
      return true
    },

    /** 文件上传成功回调 */
    handleFileSuccess(response) {
      if (response.code === 200) {
        console.log(response)
        this.form.packageFileId = response.data.id
        // 更新文件列表
        this.fileList = [{
          name: response.data.originalFilename,
          url: response.data.fileUrl
        }]
        this.$modal.msgSuccess('文件上传成功！')
      } else {
        this.$modal.msgError('文件上传失败：' + response.msg)
      }
    },

    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
      // 清空文件上传列表
      this.fileList = []
    },

    // 表单重置
    reset() {
      this.form = {
        id: undefined,
        appId: '__UNI__B40ED08',
        version: undefined,
        packageType: 1,
        packageFileId: undefined,
        updateLog: undefined,
        updateTitle: undefined,
        isForceUpdate: 0,
        remark: undefined
      }
      if (this.$refs['form']) {
        this.$refs['form'].resetFields()
      }
      // 清空文件上传组件
      if (this.$refs['upload']) {
        this.$refs['upload'].clearFiles()
      }
    },

    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },

    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm('queryForm')
      this.queryParams.appId = undefined
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
      this.title = '新增APP版本'
      // 清空文件列表
      this.fileList = []
    },

    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getAppVersionControl(id).then(response => {
        this.form = response.data
        // 回显文件列表（如果有文件ID）
        if (this.form.packageFileId) {
          this.fileList = [{
            name: this.form.fileInfo.originalFilename,
            url: this.form.fileInfo.fileUrl
          }]
        }
        this.open = true
        this.title = '修改APP版本'
      })
    },

    /** 提交按钮 */
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          // 转换强制更新为数字类型
          this.form.isForceUpdate = parseInt(this.form.isForceUpdate)

          if (this.form.id != null) {
            updateAppVersionControl(this.form).then(response => {
              this.$modal.msgSuccess('修改成功')
              this.open = false
              this.getList()
            })
          } else {
            saveAppVersionControl(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除所选APP版本记录？').then(() => {
        return delAppVersionControl(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
</style>
