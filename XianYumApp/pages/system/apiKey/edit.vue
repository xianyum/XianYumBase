<template>
  <view class="form-container">
    <view class="form-card">
      <view class="form-group">
        <view class="form-label">ApiKey</view>
        <view v-if="isEdit" class="readonly-box">
          <text class="readonly-text">{{ form.apiKey || '-' }}</text>
          <view class="copy-btn" @tap="copyApiKey">复制</view>
        </view>
        <view v-else class="input-box">
          <input v-model="form.apiKey" class="input" placeholder="留空则由系统自动生成" />
        </view>
      </view>

      <view class="form-group">
        <view class="form-label required">名称</view>
        <view class="input-box">
          <input v-model="form.title" class="input" placeholder="请输入名称" maxlength="64" />
        </view>
      </view>

      <view class="form-group">
        <view class="form-label required">归属账号</view>
        <view class="select-box" :class="{ disabled: isEdit }" @tap="openUserPicker">
          <text class="select-text" :class="{ placeholder: !selectedUserLabel }">
            {{ selectedUserLabel || '请选择归属账号' }}
          </text>
          <uni-icons type="right" size="14" color="#c0c4cc"></uni-icons>
        </view>
      </view>

      <view class="form-group">
        <view class="form-label">介绍</view>
        <view class="textarea-box">
          <textarea
              v-model="form.intro"
              class="textarea"
              placeholder="请输入用途描述"
              maxlength="200"
          />
          <text class="word-count">{{ (form.intro || '').length }}/200</text>
        </view>
      </view>

      <view class="form-group">
        <view class="form-label">授权范围</view>
        <view class="scope-list" v-if="scopeOptions.length > 0">
          <view
              v-for="scope in visibleScopes"
              :key="scope"
              class="scope-chip"
              :class="{ active: form.scopes.includes(scope) }"
              @tap="toggleScope(scope)"
          >{{ scope }}</view>
        </view>
        <view class="scope-toggle" v-if="hasMoreScopes" @tap="scopeExpanded = !scopeExpanded">
          <text>{{ scopeToggleText }}</text>
          <uni-icons
              :type="scopeExpanded ? 'top' : 'bottom'"
              size="12"
              color="#409eff"
          ></uni-icons>
        </view>
        <view class="scope-add">
          <view class="input-box flex-1">
            <input
                v-model="scopeInput"
                class="input"
                placeholder="自定义授权范围，如 chat"
                confirm-type="done"
                @confirm="addCustomScope"
            />
          </view>
          <view class="add-scope-btn" @tap="addCustomScope">添加</view>
        </view>
      </view>

      <view class="form-group">
        <view class="form-label">到期时间</view>
        <view class="segment">
          <view
              class="segment-item"
              :class="{ active: form.expiresType === 'never' }"
              @tap="setExpiresType('never')"
          >永不过期</view>
          <view
              class="segment-item"
              :class="{ active: form.expiresType === 'custom' }"
              @tap="setExpiresType('custom')"
          >指定时间</view>
        </view>
        <view v-if="form.expiresType === 'custom'" class="datetime-row">
          <view class="datetime-col">
            <picker mode="date" :value="expireDate" :start="minDate" @change="handleExpireDateChange">
              <view class="picker-box">
                <text class="picker-text" :class="{ placeholder: !expireDate }">
                  {{ expireDate || '选择日期' }}
                </text>
                <uni-icons type="calendar" size="16" color="#c0c4cc"></uni-icons>
              </view>
            </picker>
          </view>
          <view class="datetime-col">
            <picker mode="time" :value="expireTime" @change="handleExpireTimeChange">
              <view class="picker-box">
                <text class="picker-text">{{ expireTime || '选择时间' }}</text>
                <uni-icons type="calendar" size="16" color="#c0c4cc"></uni-icons>
              </view>
            </picker>
          </view>
        </view>
      </view>

      <view class="form-group">
        <view class="form-label">状态</view>
        <view class="switch-box" :class="{ 'is-on': form.isValid }" @tap="toggleStatus">
          <view class="status-info">
            <view class="status-dot"></view>
            <text class="switch-text">{{ form.isValid ? '生效' : '禁用' }}</text>
          </view>
          <view class="custom-switch" :class="{ on: form.isValid }">
            <view class="switch-knob"></view>
          </view>
        </view>
      </view>
    </view>

    <view class="form-actions">
      <button class="submit-btn" type="primary" @click="submitForm">提 交</button>
      <button class="cancel-btn" @click="cancel">取 消</button>
    </view>

    <view v-if="showUserPicker" class="picker-modal" @tap="showUserPicker = false">
      <view class="picker-panel" @tap.stop="">
        <view class="picker-header">
          <text class="picker-title">选择归属账号</text>
          <uni-icons type="closeempty" size="20" color="#909399" @tap="showUserPicker = false"></uni-icons>
        </view>
        <view class="user-search">
          <uni-icons type="search" size="16" color="#909399"></uni-icons>
          <input
              v-model="userKeyword"
              type="text"
              placeholder="搜索账号"
              confirm-type="search"
              @confirm="loadUsers(true)"
          />
          <uni-icons
              v-if="userKeyword"
              type="clear"
              size="14"
              color="#c0c4cc"
              @tap="userKeyword = ''; loadUsers(true)"
          ></uni-icons>
        </view>
        <scroll-view scroll-y class="user-list" @scrolltolower="loadMoreUsers">
          <view
              v-for="user in userOptions"
              :key="user.id"
              class="user-item"
              @tap="selectUser(user)"
          >
            <view class="user-info">
              <text class="user-name">{{ user.username }}</text>
              <text v-if="user.nickName" class="user-nick">{{ user.nickName }}</text>
            </view>
            <uni-icons
                v-if="String(form.loginId) === String(user.id)"
                type="checkmarkempty"
                size="18"
                color="#409eff"
            ></uni-icons>
          </view>
          <view v-if="userOptions.length === 0 && !userLoading" class="user-empty">暂无匹配账号</view>
          <view v-if="userLoading" class="user-loading">加载中...</view>
        </scroll-view>
      </view>
    </view>
  </view>
</template>

<script>
import { getApiKey, addApiKey, updateApiKey, getScopeList } from '@/api/system/apiKey'
import { listUser, getUser } from '@/api/system/user'
import { formatTime } from '@/utils/dateFormat'

export default {
  data() {
    return {
      id: undefined,
      isEdit: false,
      form: {
        apiKey: '',
        title: '',
        intro: '',
        loginId: undefined,
        scopes: [],
        expiresType: 'never',
        isValid: true
      },
      scopeOptions: [],
      scopeInput: '',
      scopeExpanded: false,
      selectedUserLabel: '',
      userOptions: [],
      userKeyword: '',
      userPageNum: 1,
      userHasMore: true,
      userLoading: false,
      showUserPicker: false,
      expireDate: '',
      expireTime: '23:59',
      minDate: '',
      submitting: false
    }
  },
  computed: {
    visibleScopes() {
      const selected = []
      const unselected = []
      this.scopeOptions.forEach(scope => {
        if (this.form.scopes.includes(scope)) {
          selected.push(scope)
        } else {
          unselected.push(scope)
        }
      })
      const orderedScopes = [...selected, ...unselected]
      return this.scopeExpanded ? orderedScopes : orderedScopes.slice(0, 6)
    },
    hasMoreScopes() {
      return this.scopeOptions.length > 6
    },
    scopeToggleText() {
      if (this.scopeExpanded) {
        return '收起'
      }
      return `展开更多（+${this.scopeOptions.length - this.visibleScopes.length}）`
    }
  },
  onLoad(options) {
    this.minDate = formatTime(new Date(), '{y}-{m}-{d}')
    this.loadScopeOptions()
    if (options.id) {
      this.id = options.id
      this.isEdit = true
      uni.setNavigationBarTitle({
        title: '修改ApiKey'
      })
      this.loadDetail()
    }
  },
  methods: {
    isNeverExpires(value) {
      if (value === null || value === undefined || value === '') {
        return true
      }
      return Number(value) === -1
    },
    async loadScopeOptions() {
      try {
        const response = await getScopeList()
        this.scopeOptions = response.data || []
      } catch (error) {
        this.scopeOptions = []
      }
    },
    async loadDetail() {
      uni.showLoading({
        title: '加载中...',
        mask: true
      })
      try {
        const response = await getApiKey(this.id)
        const data = response.data || {}
        const neverExpires = this.isNeverExpires(data.expiresTime)
        this.form = {
          apiKey: data.apiKey,
          title: data.title || '',
          intro: data.intro || '',
          loginId: data.loginId ? String(data.loginId) : undefined,
          scopes: data.scopes || [],
          expiresType: neverExpires ? 'never' : 'custom',
          isValid: data.isValid !== false
        }
        this.mergeScopes()
        if (!neverExpires) {
          const expiresDate = new Date(Number(data.expiresTime))
          if (!isNaN(expiresDate.getTime())) {
            this.expireDate = formatTime(expiresDate, '{y}-{m}-{d}')
            this.expireTime = formatTime(expiresDate, '{h}:{i}')
          }
        }
        if (this.form.loginId) {
          this.loadUserLabel(this.form.loginId)
        }
      } catch (error) {
        // 请求异常已由拦截器提示
      } finally {
        uni.hideLoading()
      }
    },
    mergeScopes() {
      this.form.scopes.forEach(scope => {
        if (!this.scopeOptions.includes(scope)) {
          this.scopeOptions.push(scope)
        }
      })
    },
    async loadUserLabel(id) {
      try {
        const response = await getUser(id)
        this.selectedUserLabel = this.formatUserLabel(response.data || {})
      } catch (error) {
        this.selectedUserLabel = String(id)
      }
    },
    formatUserLabel(user) {
      if (!user.username) return ''
      return user.nickName ? `${user.username}（${user.nickName}）` : user.username
    },
    openUserPicker() {
      if (this.isEdit) return
      this.showUserPicker = true
      this.userKeyword = ''
      this.loadUsers(true)
    },
    async loadUsers(reset = false) {
      if (reset) {
        this.userPageNum = 1
        this.userOptions = []
        this.userHasMore = true
      }
      if (this.userLoading || !this.userHasMore) return
      this.userLoading = true
      try {
        const response = await listUser({
          pageNum: this.userPageNum,
          pageSize: 20,
          username: this.userKeyword || undefined
        })
        const listData = response.data || []
        const total = response.total || 0
        this.userOptions = this.userPageNum === 1 ? listData : [...this.userOptions, ...listData]
        this.userHasMore = this.userOptions.length < total
      } catch (error) {
        this.userOptions = []
        this.userHasMore = false
      } finally {
        this.userLoading = false
      }
    },
    loadMoreUsers() {
      if (this.userHasMore && !this.userLoading) {
        this.userPageNum++
        this.loadUsers()
      }
    },
    selectUser(user) {
      this.form.loginId = String(user.id)
      this.selectedUserLabel = this.formatUserLabel(user)
      this.userHasMore = false
      this.showUserPicker = false
    },
    toggleScope(scope) {
      const index = this.form.scopes.indexOf(scope)
      if (index > -1) {
        this.form.scopes.splice(index, 1)
      } else {
        this.form.scopes.push(scope)
      }
    },
    addCustomScope() {
      const inputValue = (this.scopeInput || '').trim()
      if (!inputValue) {
        this.$modal.msg('请输入授权范围')
        return
      }
      inputValue.split(/[,，\s]+/).filter(Boolean).forEach(scope => {
        if (!this.scopeOptions.includes(scope)) {
          this.scopeOptions.push(scope)
        }
        if (!this.form.scopes.includes(scope)) {
          this.form.scopes.unshift(scope)
        }
      })
      this.scopeInput = ''
    },
    setExpiresType(type) {
      if (this.form.expiresType === type) return
      this.form.expiresType = type
      if (type === 'custom' && !this.expireDate) {
        this.expireDate = formatTime(new Date(Date.now() + 24 * 60 * 60 * 1000), '{y}-{m}-{d}')
        this.expireTime = this.expireTime || '23:59'
      }
    },
    handleExpireDateChange(e) {
      this.expireDate = e.detail.value
    },
    handleExpireTimeChange(e) {
      this.expireTime = e.detail.value
    },
    getExpiresTimestamp() {
      if (!this.expireDate) return null
      const origin = `${this.expireDate.replace(/-/g, '/')} ${this.expireTime || '23:59'}:00`
      const timestamp = new Date(origin).getTime()
      return isNaN(timestamp) ? null : timestamp
    },
    toggleStatus() {
      this.form.isValid = !this.form.isValid
    },
    copyApiKey() {
      if (!this.form.apiKey) return
      uni.setClipboardData({
        data: this.form.apiKey,
        success: () => {
          this.$modal.msgSuccess('复制成功')
        }
      })
    },
    validate() {
      if (!(this.form.title || '').trim()) {
        this.$modal.msg('请输入名称')
        return false
      }
      if (!this.form.loginId) {
        this.$modal.msg('请选择归属账号')
        return false
      }
      if (this.form.expiresType === 'custom') {
        const timestamp = this.getExpiresTimestamp()
        if (!timestamp) {
          this.$modal.msg('请选择到期时间')
          return false
        }
        if (timestamp <= Date.now()) {
          this.$modal.msg('到期时间必须晚于当前时间')
          return false
        }
      }
      return true
    },
    async submitForm() {
      if (this.submitting) return
      if (!this.validate()) return
      this.submitting = true
      uni.showLoading({
        title: '提交中...',
        mask: true
      })
      let success = false
      try {
        const payload = {
          title: (this.form.title || '').trim(),
          intro: (this.form.intro || '').trim(),
          scopes: this.form.scopes,
          expiresTime: this.form.expiresType === 'never' ? -1 : this.getExpiresTimestamp(),
          isValid: this.form.isValid
        }
        let response
        if (this.isEdit) {
          payload.id = this.id
          response = await updateApiKey(payload)
        } else {
          const apiKey = (this.form.apiKey || '').trim()
          if (apiKey) {
            payload.apiKey = apiKey
          }
          payload.loginId = this.form.loginId
          response = await addApiKey(payload)
        }
        if (response.code === 200) {
          success = true
          this.$modal.msgSuccess(this.isEdit ? '修改成功' : '新增成功')
          setTimeout(() => {
            const pages = getCurrentPages()
            const prevPage = pages[pages.length - 2]
            if (prevPage && prevPage.$vm && prevPage.$vm.handleQuery) {
              prevPage.$vm.handleQuery()
            }
            uni.navigateBack()
          }, 800)
        }
      } catch (error) {
        // 请求异常已由拦截器提示
      } finally {
        uni.hideLoading()
        if (!success) {
          this.submitting = false
        }
      }
    },
    cancel() {
      uni.navigateBack()
    }
  }
}
</script>

<style lang="scss" scoped>
$color-main: #409eff;
$color-success: #67c23a;
$color-danger: #f56c6c;
$color-gray: #909399;
$color-text: #303133;
$color-text-light: #606266;
$color-border: #ebeef5;
$color-bg: #f5f5f5;
$color-card: #fff;

.form-container {
  padding: 20rpx;
  background-color: $color-bg;
  min-height: 100vh;
  box-sizing: border-box;

  .form-card {
    background-color: $color-card;
    border-radius: 16rpx;
    padding: 30rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);

    .form-group {
      margin-bottom: 32rpx;

      &:last-child {
        margin-bottom: 0;
      }

      .form-label {
        font-size: 28rpx;
        color: $color-text-light;
        margin-bottom: 14rpx;
        font-weight: 500;

        &.required::before {
          content: '*';
          color: $color-danger;
          margin-right: 8rpx;
        }
      }

      .input-box {
        display: flex;
        align-items: center;
        height: 88rpx;
        padding: 0 28rpx;
        background-color: #f8f9fa;
        border-radius: 12rpx;

        .input {
          flex: 1;
          height: 100%;
          font-size: 28rpx;
          color: #2c3e50;
          background: transparent;

          &::placeholder {
            color: $color-gray;
          }
        }
      }

      .readonly-box {
        display: flex;
        align-items: center;
        gap: 16rpx;
        min-height: 88rpx;
        padding: 16rpx 28rpx;
        background-color: #f8f9fa;
        border-radius: 12rpx;
        box-sizing: border-box;

        .readonly-text {
          flex: 1;
          min-width: 0;
          font-size: 26rpx;
          color: #2c3e50;
          font-family: monospace;
          word-break: break-all;
        }

        .copy-btn {
          flex-shrink: 0;
          padding: 8rpx 20rpx;
          font-size: 22rpx;
          color: $color-main;
          background-color: #ecf5ff;
          border-radius: 6rpx;

          &:active {
            opacity: 0.7;
          }
        }
      }

      .select-box {
        display: flex;
        align-items: center;
        justify-content: space-between;
        height: 88rpx;
        padding: 0 28rpx;
        background-color: #f8f9fa;
        border-radius: 12rpx;

        &.disabled {
          opacity: 0.7;
        }

        .select-text {
          flex: 1;
          font-size: 28rpx;
          color: #2c3e50;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;

          &.placeholder {
            color: $color-gray;
          }
        }

        &:active {
          opacity: 0.8;
        }
      }

      .textarea-box {
        position: relative;
        padding: 20rpx 24rpx 40rpx;
        background-color: #f8f9fa;
        border-radius: 12rpx;

        .textarea {
          width: 100%;
          height: 150rpx;
          font-size: 28rpx;
          color: #2c3e50;
          line-height: 1.5;
        }

        .word-count {
          position: absolute;
          right: 24rpx;
          bottom: 12rpx;
          font-size: 22rpx;
          color: #c0c4cc;
        }
      }

      .scope-list {
        display: flex;
        flex-wrap: wrap;
        gap: 16rpx;
        margin-bottom: 20rpx;

        .scope-chip {
          padding: 12rpx 26rpx;
          font-size: 24rpx;
          color: $color-text-light;
          background-color: #f5f7fa;
          border-radius: 8rpx;
          border: 1rpx solid transparent;
          transition: all 0.2s;

          &.active {
            color: $color-main;
            background-color: rgba($color-main, 0.1);
            border-color: $color-main;
          }

          &:active {
            opacity: 0.7;
          }
        }
      }

      .scope-toggle {
        display: inline-flex;
        align-items: center;
        gap: 4rpx;
        margin-top: -8rpx;
        margin-bottom: 20rpx;
        font-size: 24rpx;
        color: $color-main;

        &:active {
          opacity: 0.7;
        }
      }

      .scope-add {
        display: flex;
        align-items: center;
        gap: 16rpx;

        .flex-1 {
          flex: 1;
        }

        .add-scope-btn {
          flex-shrink: 0;
          height: 88rpx;
          line-height: 88rpx;
          padding: 0 32rpx;
          font-size: 26rpx;
          color: $color-main;
          background-color: #ecf5ff;
          border-radius: 12rpx;

          &:active {
            opacity: 0.7;
          }
        }
      }

      .segment {
        display: flex;
        padding: 6rpx;
        background-color: #eef0f7;
        border-radius: 999rpx;

        .segment-item {
          flex: 1;
          height: 64rpx;
          line-height: 64rpx;
          text-align: center;
          font-size: 26rpx;
          color: $color-text-light;
          border-radius: 999rpx;

          &.active {
            color: $color-main;
            font-weight: 600;
            background-color: $color-card;
            box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.08);
          }
        }
      }

      .datetime-row {
        display: flex;
        gap: 20rpx;
        margin-top: 20rpx;

        .datetime-col {
          flex: 1;
          min-width: 0;
        }

        picker {
          flex: 1;
        }

        .picker-box {
          display: flex;
          align-items: center;
          justify-content: space-between;
          height: 88rpx;
          padding: 0 24rpx;
          background-color: #f8f9fa;
          border-radius: 12rpx;

          .picker-text {
            font-size: 28rpx;
            color: #2c3e50;

            &.placeholder {
              color: $color-gray;
            }
          }
        }
      }

      .switch-box {
        display: flex;
        align-items: center;
        justify-content: space-between;
        height: 88rpx;
        padding: 0 28rpx;
        background-color: #f8f9fa;
        border: 1rpx solid transparent;
        border-radius: 12rpx;
        transition: all 0.25s;

        &.is-on {
          background-color: #f6fbf3;
          border-color: rgba(103, 194, 58, 0.35);
        }

        .status-info {
          display: flex;
          align-items: center;
          gap: 12rpx;

          .status-dot {
            width: 14rpx;
            height: 14rpx;
            border-radius: 50%;
            background-color: #c0c4cc;
            transition: all 0.25s;
          }

          .switch-text {
            font-size: 28rpx;
            color: $color-gray;
            transition: all 0.25s;
          }
        }

        &.is-on {
          .status-dot {
            background-color: $color-success;
          }

          .switch-text {
            color: $color-success;
            font-weight: 600;
          }
        }

        .custom-switch {
          width: 96rpx;
          height: 56rpx;
          padding: 4rpx;
          box-sizing: border-box;
          border-radius: 28rpx;
          background-color: #dcdfe6;
          transition: background-color 0.25s;

          .switch-knob {
            width: 48rpx;
            height: 48rpx;
            border-radius: 50%;
            background-color: #fff;
            box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.18);
            transition: transform 0.25s;
          }

          &.on {
            background-color: $color-success;

            .switch-knob {
              transform: translateX(40rpx);
            }
          }
        }

        &:active {
          opacity: 0.85;
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
        background-color: $color-main;
        color: #fff;
        box-shadow: 0 8rpx 16rpx rgba(64, 158, 255, 0.2);
      }

      &.cancel-btn {
        background-color: #f5f7fa;
        color: $color-text-light;
        border: 2rpx solid #dcdfe6;
      }
    }
  }
}

.picker-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.45);
  z-index: 3000;
  display: flex;
  align-items: flex-end;

  .picker-panel {
    width: 100%;
    height: 76vh;
    background-color: $color-card;
    border-radius: 24rpx 24rpx 0 0;
    display: flex;
    flex-direction: column;
    overflow: hidden;

    .picker-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 32rpx;
      border-bottom: 1rpx solid $color-border;
      flex-shrink: 0;

      .picker-title {
        font-size: 32rpx;
        font-weight: 600;
        color: $color-text;
      }
    }

    .user-search {
      display: flex;
      align-items: center;
      gap: 12rpx;
      height: 72rpx;
      margin: 24rpx 32rpx;
      padding: 0 24rpx;
      background-color: #f5f7fa;
      border-radius: 36rpx;
      flex-shrink: 0;

      input {
        flex: 1;
        height: 100%;
        font-size: 28rpx;
        color: $color-text;
        background: transparent;

        &::placeholder {
          color: #c0c4cc;
        }
      }
    }

    .user-list {
      flex: 1;
      height: 0;

      .user-item {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 26rpx 32rpx;
        border-bottom: 1rpx solid #f5f5f5;

        .user-info {
          flex: 1;
          min-width: 0;
          display: flex;
          align-items: baseline;
          gap: 16rpx;

          .user-name {
            font-size: 28rpx;
            color: $color-text;
            font-weight: 500;
          }

          .user-nick {
            font-size: 24rpx;
            color: $color-gray;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }
        }

        &:active {
          background-color: #fafafa;
        }
      }

      .user-empty,
      .user-loading {
        text-align: center;
        padding: 60rpx 0;
        font-size: 26rpx;
        color: $color-gray;
      }
    }
  }
}
</style>
