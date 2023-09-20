<template>
  <div>
    <el-dialog
      :title="!dataForm.jobId ? '调度任务新增' : '调度任务修改'"
      :close-on-click-modal="false"
      :visible.sync="visible"
      :before-close="cancel"
      width="40%"
      center append-to-body>
      <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()"
               label-width="115px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="任务名称" prop="jobName">
              <el-input v-model="dataForm.jobName" placeholder="请输入任务名称"/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item prop="jobHandler">
              <span slot="label">
                JobHandler
                <el-tooltip placement="top">
                  <div slot="content">
                    使用示例：java注解@JobHandler("testJob")
                    <br/>必须实现IJobHandler类，重写execute方法
                  </div>
                  <i class="el-icon-question"></i>
                </el-tooltip>
              </span>
              <el-input v-model="dataForm.jobHandler" placeholder="请输入JobHandler(spring bean名称)"/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="cron表达式" prop="cronExpression">
              <el-input v-model="dataForm.cronExpression" placeholder="请输入cron执行表达式">
                <template slot="append">
                  <el-button type="primary" @click="handleShowCron">
                    生成表达式
                    <i class="el-icon-time el-icon--right"></i>
                  </el-button>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item prop="jobParams">
              <span slot="label">
                任务参数
                <el-tooltip placement="top">
                  <div slot="content">
                    使用示例：可以在execute方法里直接用jobMapParams进行使用
                    <br/>格式必须为JSON，value为字符串
                  </div>
                  <i class="el-icon-question"></i>
                </el-tooltip>
              </span>
              <el-input v-model="dataForm.jobParams" placeholder="任务参数（json格式）"></el-input>
            </el-form-item>
          </el-col>

          <el-col :span="24">
            <el-form-item label="执行策略" prop="misfirePolicy">
              <el-radio-group v-model="dataForm.misfirePolicy" size="small">
                <el-radio-button :label="0">默认策略</el-radio-button>
                <el-radio-button :label="1">立即执行</el-radio-button>
                <el-radio-button :label="2">执行一次</el-radio-button>
                <el-radio-button :label="3">放弃执行</el-radio-button>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否并发" prop="concurrent">
              <el-radio-group v-model="dataForm.concurrent" size="small">
                <el-radio-button label="0">允许</el-radio-button>
                <el-radio-button label="1">禁止</el-radio-button>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="dataForm.status" size="small">
                <el-radio label=0>正常</el-radio>
                <el-radio label=1>暂停</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="告警通知" prop="messageCode">
              <el-select v-model="dataForm.messageCode" placeholder="请选择消息类型通知" filterable clearable>
                <el-option
                  v-for="item in messageTypeList"
                  :key="item.messageCode"
                  :label="`${item.messageCode}---${item.description}`"
                  :value="item.messageCode"
                >
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="任务备注" prop="remark">
              <el-input v-model="dataForm.remark" placeholder="任务备注"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <span slot="footer" class="dialog-footer">
      <el-button @click="cancel" plain>取消</el-button>
      <el-button type="primary" plain @click="dataFormSubmit()">确定</el-button>
    </span>
    </el-dialog>

    <el-dialog title="Cron表达式生成器" :visible.sync="openCron" append-to-body destroy-on-close class="scrollbar">
      <crontab @hide="openCron=false" @fill="crontabFill" :expression="expression"></crontab>
    </el-dialog>
  </div>

</template>

<script>
import Crontab from '@/components/Crontab'

export default {
  components: {Crontab},
  data () {
    return {
      // 是否显示Cron表达式弹出层
      openCron: false,
      expression: '',
      visible: false,
      messageTypeList: [],
      dataForm: {
        jobParams: '',
        jobId: undefined,
        jobName: undefined,
        jobHandler: undefined,
        invokeTarget: undefined,
        cronExpression: undefined,
        misfirePolicy: '0',
        concurrent: 1,
        status: '0'
      },
      dataRule: {
        jobName: [
          {required: true, message: '任务名称不能为空', trigger: 'blur'}
        ],
        jobHandler: [
          {required: true, message: 'jobHandler不能为空', trigger: 'blur'}
        ],
        cronExpression: [
          {required: true, message: 'cron执行表达式不能为空', trigger: 'blur'}
        ]
      }
    }
  },
  methods: {
    getMessageTypeList() {
      this.$http({
        url: this.$http.adornUrl('/v1/messageTypeConfig/getList'),
        method: 'post',
        data: this.$http.adornData({})
      }).then(({data}) => {
        if (data && data.code === 200) {
            this.messageTypeList = data.data
        }
      })
    },
    cancel () {
      this.visible = false
      this.$refs['dataForm'].resetFields()
    },
    /** cron表达式按钮操作 */
    handleShowCron () {
      this.expression = this.dataForm.cronExpression
      this.openCron = true
    },
    /** 确定后回传值 */
    crontabFill (value) {
      this.dataForm.cronExpression = value
    },
    getDataList () {
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
        if (this.dataForm.jobId) {
          this.$http({
            url: this.$http.adornUrl('/v1/job/getById'),
            method: 'post',
            data: this.$http.adornData({
              'jobId': this.dataForm.jobId
            })
          }).then(({data}) => {
            if (data && data.code === 200) {
              this.dataForm = data.data
            }
          })
        }
      })
    },
    init: function (id) {
      this.getMessageTypeList()
      this.dataForm.jobId = id || 0
      this.visible = true
      if (id) {
        this.getDataList()
      }
      this.$forceUpdate()
    },
    // 表单提交
    dataFormSubmit () {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.$http({
            url: this.$http.adornUrl(`/v1/job/${!this.dataForm.jobId ? 'save' : 'update'}`),
            method: 'post',
            data: this.dataForm
          }).then(({data}) => {
            if (data && data.code === 200) {
              this.$message({
                message: '操作成功',
                type: 'success',
                duration: 1500,
                onClose: () => {
                  this.visible = false
                  this.$emit('refreshDataList')
                }
              })
              this.$refs['dataForm'].resetFields()
            } else {
              this.$message.error(data.msg)
            }
          })
        }
      })
    }
  }
}
</script>
