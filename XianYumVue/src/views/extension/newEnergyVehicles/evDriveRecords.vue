<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="70px">
      <el-form-item label="驾驶日期" clearable>
        <el-date-picker
          v-model="queryParams.dateRange"
          style="width: 240px"
          value-format="yyyy-MM-dd HH:mm:ss"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="['00:00:00', '23:59:59']"
          :picker-options="pickerOptions"
        ></el-date-picker>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option
            v-for="item in statusList"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="事项">
        <el-select v-model="queryParams.matter" placeholder="请选择事项" multiple  collapse-tags clearable>
          <el-option
            v-for="dict in dict.type.ev_drive_matter"
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
          v-hasPermi="['sev-drive-records:save']"
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
          v-hasPermi="['ev-drive-records:update']"
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
          v-hasPermi="['ev-drive-records:delete']"
        >删除
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="evDriveList" show-summary :summary-method="summaryMethod"
              @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="序号" type="index" align="center" width="50"/>
      <el-table-column label="驾驶日期" align="center" prop="driveDate" width="180">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.driveDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <!--      <el-table-column label="车牌号" align="center" prop="vehicleNo"/>-->
      <el-table-column label="行驶公里数(km)" align="center" prop="distanceKm"/>
      <el-table-column label="消耗电量(kWh)" align="center" prop="electricityConsumed"/>
      <el-table-column label="每公里电量消耗(kWh/km)" align="center" prop="electricityPerKm"/>
      <el-table-column label="能耗状态" align="center" prop="status">
        <template v-slot="scope">
          <el-tag v-if="scope.row.status === 0" effect="plain" type="success">正常</el-tag>
          <el-tag v-else type="danger" effect="plain">能耗异常</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="事项" align="center" prop="matter">
        <template v-slot="scope">
          <dict-tag :options="dict.type.ev_drive_matter" :value="scope.row.matter"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['ev-drive-records:update']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['ev-drive-records:delete']"
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

    <!-- 添加或修改行驶记录对话框 -->
    <el-dialog :title="title" :visible.sync="open" :width="dialogWidth" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="130px">
        <el-form-item label="驾驶日期" prop="driveDate">
          <el-date-picker
            v-model="form.driveDate"
            type="date"
            placeholder="选择驾驶日期"
            :style="{ width: datePickerWidth }"
          />
        </el-form-item>
        <el-form-item label="行驶公里数(km)" prop="distanceKm">
          <el-input v-model="form.distanceKm" placeholder="请输入行驶公里数(km)"/>
        </el-form-item>
        <el-form-item label="消耗电量(kWh)" prop="electricityConsumed">
          <el-input v-model="form.electricityConsumed" placeholder="请输入消耗电量(kWh)"/>
        </el-form-item>
        <el-form-item label="事项" prop="matter">
          <el-select v-model="form.matter" placeholder="请选择事项" multiple  collapse-tags clearable>
            <el-option
              v-for="dict in dict.type.ev_drive_matter"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
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
import {
  getEvDriveRecordsPage,
  saveEvDriveRecords,
  updateEvDriveRecords,
  delEvDriveRecords,
  getEvDriveRecords
} from '@/api/extension/evDriveRecords'

export default {
  name: 'evDriveRecords',
  dicts: ['ev_drive_matter'],
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
      statusList: [{
        value: 0,
        label: '正常'
        }, {
        value: 1,
        label: '能耗异常'
      }],
      dialogWidth: '800px', // 默认宽度
      datePickerWidth: '100%',
      evDriveList: [],
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        dateRange: [
          this.parseTime(new Date(new Date().getFullYear(), new Date().getMonth(), 1, 0, 0, 0)),
          this.parseTime(new Date())
        ],
        status: undefined,
        vehicleNo: undefined,
        matter: undefined
      },
      pickerOptions: {
        shortcuts: [
          {
            text: '上个月',
            onClick(picker) {
              const end = new Date(); // 当前日期
              const start = new Date();
              console.log(start)
              // 设置上个月的最后一天
              end.setMonth(end.getMonth()); // 上个月
              end.setDate(0); // 上个月的最后一天
              end.setHours(23)
              end.setMinutes(59)
              end.setSeconds(59)
              // 设置上个月的第一天
              start.setMonth(end.getMonth()); // 上个月
              start.setDate(1); // 上个月的1号
              start.setHours(0)
              start.setMinutes(0)
              start.setSeconds(0)
              picker.$emit('pick', [start, end]);
            }
          }, {
            text: '近三个月',
            onClick(picker) {
              const end = new Date()
              const start = new Date()
              start.setMonth(start.getMonth() - 3)
              picker.$emit('pick', [start, end])
            }
          }, {
            text: '近半年',
            onClick(picker) {
              const end = new Date()
              const start = new Date()
              start.setMonth(start.getMonth() - 6)
              picker.$emit('pick', [start, end])
            }
          }, {
            text: '今年',
            onClick(picker) {
              const end = new Date()
              const start = new Date()
              start.setMonth(0)  // 今年的1月1日
              start.setDate(1)   // 今年的1月1日
              start.setHours(0)
              start.setMinutes(0)
              start.setSeconds(0)
              picker.$emit('pick', [start, end])
            }
          }, {
            text: '去年',
            onClick(picker) {
              const end = new Date()
              const start = new Date()
              start.setFullYear(start.getFullYear() - 1)  // 去年
              start.setMonth(0)  // 去年的1月1日
              start.setDate(1)   // 去年的1月1日
              start.setHours(0)
              start.setMinutes(0)
              start.setSeconds(0)
              end.setFullYear(end.getFullYear() - 1)  // 去年的12月31日
              end.setMonth(11)   // 去年的12月
              end.setDate(31)    // 去年的12月31日
              end.setHours(23)
              end.setMinutes(59)
              end.setSeconds(59)
              picker.$emit('pick', [start, end])
            }
          }
        ]
      },
      // 表单参数
      form: {
        id: undefined,
        vehicleNo: undefined,
        //   driveDate: undefined,
        distanceKm: undefined,
        electricityConsumed: undefined,
        matter: ['10'],
        driveDate: new Date().toISOString().split('T')[0]
      },
      // 表单汇总数据
      summaryInfo: {
        totalDistanceKm: null,
        totalElectricityConsumed: null,
        electricityPerKm: null
      },
      // 临时存储驾驶日期
      templateDriverDate: undefined,
      // 表单校验
      rules: {
        driveDate: [
          { required: true, message: '驾驶日期不能为空', trigger: 'blur' }
        ],
        distanceKm: [
          { required: true, message: '行驶公里数不能为空', trigger: 'blur' }
        ],
        electricityConsumed: [
          { required: true, message: '消耗电量不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  mounted() {
    this.adjustDialogWidth()
    window.addEventListener('resize', this.adjustDialogWidth) // 响应式处理
  },
  destroyed() {
    window.removeEventListener('resize', this.adjustDialogWidth)
  },
  methods: {
    summaryMethod({ columns, data }) {
      const sums = []
      sums[0] = '-'
      sums[1] = '合计'
      sums[2] = '-'
      sums[3] = this.summaryInfo.totalDistanceKm
      sums[4] = this.summaryInfo.totalElectricityConsumed
      sums[5] = this.summaryInfo.electricityPerKm
      sums[6] = '-'
      return sums
    },
    adjustDialogWidth() {
      // 根据屏幕宽度调整对话框宽度
      const screenWidth = window.innerWidth
      if (screenWidth <= 768) { // 手机屏幕
        this.dialogWidth = '90%'  // 让宽度占据90%的屏幕宽度
      } else {
        this.dialogWidth = '800px'  // 大屏幕的默认宽度
      }
    },
    /** 查询客户端管理列表 */
    getList() {
      this.loading = true
      getEvDriveRecordsPage(this.addDateRange(this.queryParams, this.queryParams.dateRange)).then(response => {
        this.evDriveList = response.data
        this.summaryInfo = response.otherInfo
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
      // 确保 $refs.form 在执行时已正确引用
      if (this.$refs['form']) {
        this.$refs['form'].resetFields();
      }
      this.form = {
        id: undefined,
        vehicleNo: undefined,
        //   driveDate: undefined,
        distanceKm: undefined,
        electricityConsumed: undefined,
        matter: ['10'],
        driveDate: new Date().toISOString().split('T')[0]
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
      this.queryParams.dateRange = [
        this.parseTime(new Date(new Date().getFullYear(), new Date().getMonth(), 1, 0, 0, 0)),
        this.parseTime(new Date())
      ]
      this.queryParams.status = undefined
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
      this.title = '添加车辆行驶记录'
      if(!this.templateDriverDate){
        this.form.driveDate = new Date().toISOString().split('T')[0]
      }else{
        this.form.driveDate = this.templateDriverDate
      }
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getEvDriveRecords(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = '修改车辆行驶记录'
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateEvDriveRecords(this.form).then(response => {
              this.$modal.msgSuccess('修改成功')
              this.open = false
              this.getList()
              this.templateDriverDate = this.form.driveDate
            })
          } else {
            saveEvDriveRecords(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除车辆行驶记录？').then(function() {
        return delEvDriveRecords(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {
      })
    }
  }
}
</script>
