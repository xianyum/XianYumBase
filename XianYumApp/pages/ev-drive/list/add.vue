<template>
  <view class="form-container">
    <uni-forms ref="form" :model="formData" :rules="rules" :show-message="false">
      <view class="form-card">
        <view class="form-group">
          <view class="form-label required">驾驶日期</view>
          <uni-forms-item name="driveDate">
            <picker
                mode="date"
                :value="driveDateValue"
                :start="startDate"
                :end="endDate"
                @change="handleDateChange"
            >
              <view class="picker-box">
                <text class="picker-text">{{ formData.driveDate || '请选择驾驶日期' }}</text>
                <uni-icons type="right" size="14" color="#999"></uni-icons>
              </view>
            </picker>
          </uni-forms-item>
        </view>

        <!-- 行驶公里数 -->
        <view class="form-group">
          <view class="form-label required">行驶公里数</view>
          <uni-forms-item name="distanceKm">
            <uni-easyinput v-model="formData.distanceKm" placeholder="请输入行驶公里数" type="digit" suffixText="km" precision="2" @input="handleNumberInput('distanceKm')"
            />
          </uni-forms-item>
        </view>

        <!-- 消耗电量 -->
        <view class="form-group">
          <view class="form-label required">消耗电量</view>
          <uni-forms-item name="electricityConsumed">
            <uni-easyinput v-model="formData.electricityConsumed" placeholder="请输入消耗电量" type="digit" suffixText="kWh" precision="2" @input="handleNumberInput('electricityConsumed')"/>
          </uni-forms-item>
        </view>
      </view>

      <!-- 提交按钮 -->
      <view class="form-actions">
        <button class="submit-btn" type="primary" @click="submitForm">提 交</button>
        <button class="cancel-btn" @click="cancel">取 消</button>
      </view>
    </uni-forms>
  </view>
</template>

<script>
import { formatTime } from "@/utils/dateFormat";
import { saveEvDriveRecords } from "@/api/ev-drive/list";

export default {
  data() {
    return {
      // 表单核心数据
      formData: {
        driveDate: '',
        distanceKm: '',
        electricityConsumed: ''
      },
      // picker日期选择器绑定值（原生需要）
      driveDateValue: '',
      // 日期选择范围
      startDate: '2020-01-01', // 最小可选日期
      endDate: '', // 最大可选当前日期
      // 表单校验规则
      rules: {
        driveDate: {
          rules: [{ required: true, errorMessage: '请选择驾驶日期' }]
        },
        distanceKm: {
          rules: [
            { required: true, errorMessage: '请输入行驶公里数' },
            { pattern: /^(\d+)(\.\d{1,2})?$/, errorMessage: '公里数最多两位小数' },
            { min: 0.01, errorMessage: '公里数必须大于0' }
          ]
        },
        electricityConsumed: {
          rules: [
            { required: true, errorMessage: '请输入消耗电量' },
            { pattern: /^(\d+)(\.\d{1,2})?$/, errorMessage: '电量最多两位小数' },
            { min: 0.01, errorMessage: '电量必须大于0' }
          ]
        }
      }
    }
  },
  onLoad() {
    // 初始化当前日期，确保格式为yyyy-MM-dd
    this.endDate = formatTime(new Date(), '{y}-{m}-{d}');
    this.driveDateValue = this.endDate;
    // 初始化默认选中当天日期
    this.formData.driveDate = this.endDate;
  },
  methods: {
    // 处理日期选择
    handleDateChange(e) {
      const selectedDate = e.detail.value;
      this.formData.driveDate = selectedDate;
      this.driveDateValue = selectedDate;
    },
    // 处理数字输入过滤（防止输入多个小数点）
    handleNumberInput(field) {
      let value = this.formData[field];
      if (!value) return;

      // 只保留数字和一个小数点，且小数点后最多两位
      value = value.replace(/[^0-9.]/g, ''); // 移除非数字和小数点的字符
      value = value.replace(/\.{2,}/g, '.'); // 多个小数点替换为一个
      if (value.indexOf('.') !== -1) {
        const parts = value.split('.');
        value = parts[0] + '.' + (parts[1] || '').slice(0, 2); // 小数点后最多两位
      }
      this.formData[field] = value;
    },
    // 提交表单
    async submitForm() {
      try {
        // 表单校验
        await this.$refs.form.validate();

        uni.showLoading({
          title: '提交中...',
          mask: true
        });

        // 构造提交数据
        const submitData = {
          driveDate: this.formData.driveDate,
          distanceKm: Number(this.formData.distanceKm),
          electricityConsumed: Number(this.formData.electricityConsumed),
          matter: ["10"]
        };

        // 调用新增接口
        const result = await saveEvDriveRecords(submitData);
        uni.hideLoading();

        if (result.code === 200) {
          this.$showSuccessToast('新增成功');
          setTimeout(() => {
            const pages = getCurrentPages();
            const prevPage = pages[pages.length - 2];
            if (prevPage && prevPage.$vm) {
              prevPage.$vm.handleQuery(); // 触发列表页查询，刷新数据
            }
            uni.navigateBack();
          }, 1000);
        }
      } catch (error) {

      }
    },
    // 取消操作
    cancel() {
      uni.navigateBack();
    }
  }
}
</script>

<style lang="scss" scoped>
.form-container {
  padding: 20rpx;
  background-color: #f5f5f5;
  min-height: 100vh;

  .form-card {
    background-color: #fff;
    border-radius: 16rpx;
    padding: 30rpx;
    margin-bottom: 24rpx;
    box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.05);

    .form-group {
      margin-bottom: 32rpx;

      &:last-child {
        margin-bottom: 0;
      }

      .form-label {
        font-size: 28rpx;
        color: #606266;
        margin-bottom: 16rpx;
        font-weight: 500;

        &.required::before {
          content: '*';
          color: #f56c6c;
          margin-right: 8rpx;
        }
      }

      :deep(.uni-forms-item) {
        margin: 0;
        padding: 0;

        .uni-forms-item__content {
          // 日期选择器样式（与输入框统一）
          .picker-box {
            display: flex;
            align-items: center;
            justify-content: space-between;
            background-color: #f8f9fa;
            border-radius: 12rpx;
            height: 88rpx;
            padding: 0 28rpx;
            font-size: 28rpx;

            .picker-text {
              color: #909399;
              &:not(:empty) {
                color: #2c3e50;
              }
            }
          }

          // 数字输入框样式
          .uni-easyinput__content {
            background-color: #f8f9fa;
            border-radius: 12rpx;
            height: 88rpx;
            padding: 0 28rpx;

            input {
              font-size: 28rpx;
              color: #2c3e50;
              &::placeholder {
                color: #909399;
              }
            }

            .uni-easyinput__suffix {
              font-size: 26rpx;
              color: #606266;
              margin-right: 16rpx;
            }
          }
        }
      }
    }
  }

  .form-actions {
    display: flex;
    justify-content: center;
    gap: 32rpx;
    margin-top: 48rpx;
    padding: 0 30rpx;
    margin-bottom: 48rpx;

    button {
      flex: 1;
      height: 88rpx;
      line-height: 88rpx;
      font-size: 30rpx;
      font-weight: 500;
      border-radius: 44rpx;
      transition: all 0.3s;

      &:active {
        transform: scale(0.98);
        opacity: 0.9;
      }

      &.submit-btn {
        background-color: #409eff;
        color: #fff;
        box-shadow: 0 8rpx 16rpx rgba(64,158,255,0.2);
      }

      &.cancel-btn {
        background-color: #f5f7fa;
        color: #606266;
        border: 2rpx solid #dcdfe6;
      }
    }
  }
}
</style>