<template>
  <view class="operlog-container">
    <view class="top-bar">
      <view class="search-input-wrap">
        <uni-icons type="search" size="16" color="#909399"></uni-icons>
        <input
            v-model="queryParams.userName"
            type="text"
            placeholder="搜索账号"
            confirm-type="search"
            @confirm="handleQuery"
        />
        <uni-icons
            v-if="queryParams.userName"
            type="clear"
            size="14"
            color="#c0c4cc"
            @tap="queryParams.userName = ''; handleQuery()"
        ></uni-icons>
      </view>
      <view class="filter-btn" :class="{ active: hasFilter }" @tap="showFilter = true">
        <uni-icons type="list" size="18" color="#606266"></uni-icons>
      </view>
    </view>

    <view class="filter-drawer" v-if="showFilter" @tap="showFilter = false">
      <view class="drawer-mask"></view>
      <view class="drawer-content" @tap.stop="">
        <view class="drawer-header">
          <text class="drawer-title">筛选条件</text>
          <uni-icons type="closeempty" size="20" color="#909399" @tap="showFilter = false"></uni-icons>
        </view>
        <scroll-view scroll-y class="drawer-body">
          <view class="filter-group">
            <view class="filter-label">模块</view>
            <view class="filter-input-wrap">
              <input
                  v-model="queryParams.module"
                  type="text"
                  placeholder="请输入模块名称"
              />
            </view>
          </view>
          <view class="filter-group">
            <view class="filter-label">操作类型 <text class="multi-hint">可多选</text></view>
            <view class="chip-list">
              <view
                  v-for="item in operTypeOptions"
                  :key="item.dictValue"
                  class="chip"
                  :class="{ active: operTypeList.includes(item.dictValue) }"
                  @tap="toggleOperType(item.dictValue)"
              >{{ item.dictLabel }}</view>
            </view>
          </view>
          <view class="filter-group">
            <view class="filter-label">操作状态</view>
            <view class="chip-list">
              <view
                  class="chip"
                  :class="{ active: !queryParams.status }"
                  @tap="selectStatus(null)"
              >全部</view>
              <view
                  class="chip"
                  :class="{ active: queryParams.status === '1' }"
                  @tap="selectStatus('1')"
              >成功</view>
              <view
                  class="chip"
                  :class="{ active: queryParams.status === '0' }"
                  @tap="selectStatus('0')"
              >失败</view>
            </view>
          </view>
        </scroll-view>
        <view class="drawer-footer">
          <view class="drawer-btn reset" @tap="resetQuery">重置</view>
          <view class="drawer-btn confirm" @tap="handleFilterConfirm">确定</view>
        </view>
      </view>
    </view>

    <view class="operlog-list" v-if="operlogList.length > 0">
      <view v-for="(item, index) in operlogList" :key="item.id" class="operlog-item" @tap="handleView(item)">
        <view class="operlog-header">
          <view class="operlog-title">
            <text class="module-tag">{{ item.module || '-' }}</text>
            <text class="oper-desc">{{ item.operDesc || '-' }}</text>
          </view>
          <view class="status-tag" :class="item.status === 1 ? 'status-success' : 'status-fail'">
            {{ item.status === 1 ? '成功' : '失败' }}
          </view>
        </view>
        <view class="operlog-body">
          <view class="info-row">
            <text class="label">账号：</text>
            <text class="value">{{ item.userName || '-' }}</text>
          </view>
          <view class="info-row">
            <text class="label">操作类型：</text>
            <text class="value">{{ getDictLabel('user_oper_type', item.operType) || '-' }}</text>
          </view>
          <view class="info-row">
            <text class="label">请求方式：</text>
            <text class="value">{{ item.requestMethod || '-' }}</text>
          </view>
          <view class="info-row">
            <text class="label">IP地址：</text>
            <text class="value">{{ item.ipAddress || '-' }}</text>
          </view>
          <view class="info-row">
            <text class="label">IP归属地：</text>
            <text class="value clickable" @tap.stop="showText('IP归属地', item.location)">{{ item.location || '-' }}</text>
          </view>
          <view class="info-row">
            <text class="label">耗时：</text>
            <text class="value">{{ item.costTime || 0 }}ms</text>
          </view>
          <view class="info-row full-width">
            <text class="label">操作时间：</text>
            <text class="value">{{ formatTime(item.createTime) }}</text>
          </view>
        </view>
      </view>

      <uni-load-more :status="loadMoreStatus" :icon-size="16" :content-text="contentText" />
    </view>

    <view v-else class="empty-box">
      <uni-icons type="info" size="50" color="#999"></uni-icons>
      <text class="empty-text">暂无操作日志数据</text>
    </view>

    <view v-if="showDetail" class="detail-modal" @tap="showDetail = false">
      <view class="detail-content" @tap.stop="">
        <view class="detail-header">
          <text class="detail-title">操作日志详情</text>
          <uni-icons type="close" size="20" color="#909399" @tap="showDetail = false"></uni-icons>
        </view>
        <scroll-view scroll-y class="detail-body">
          <view class="detail-section">
            <view class="section-title">基本信息</view>
            <view class="detail-grid">
              <view class="detail-row">
                <text class="row-label">日志编号</text>
                <text class="row-value">{{ detailData.id || '-' }}</text>
              </view>
              <view class="detail-row">
                <text class="row-label">操作账号</text>
                <text class="row-value">{{ detailData.userName || '-' }}</text>
              </view>
              <view class="detail-row">
                <text class="row-label">操作模块</text>
                <text class="row-value">{{ detailData.module || '-' }}</text>
              </view>
              <view class="detail-row">
                <text class="row-label">操作类型</text>
                <text class="row-value">{{ getDictLabel('user_oper_type', detailData.operType) || '-' }}</text>
              </view>
              <view class="detail-row">
                <text class="row-label">操作状态</text>
                <text class="row-value" :class="detailData.status === 1 ? 'text-success' : 'text-danger'">
                  {{ detailData.status === 1 ? '成功' : '失败' }}
                </text>
              </view>
              <view class="detail-row">
                <text class="row-label">执行耗时</text>
                <text class="row-value">{{ detailData.costTime || 0 }}ms</text>
              </view>
              <view class="detail-row full">
                <text class="row-label">操作时间</text>
                <text class="row-value">{{ formatTime(detailData.createTime) }}</text>
              </view>
            </view>
          </view>

          <view class="detail-section">
            <view class="section-title">请求信息</view>
            <view class="detail-grid">
              <view class="detail-row">
                <text class="row-label">请求方式</text>
                <text class="row-value">{{ detailData.requestMethod || '-' }}</text>
              </view>
              <view class="detail-row">
                <text class="row-label">IP地址</text>
                <text class="row-value">{{ detailData.ipAddress || '-' }}</text>
              </view>
              <view class="detail-row full">
                <text class="row-label">IP归属地</text>
                <text class="row-value">{{ detailData.location || '-' }}</text>
              </view>
              <view class="detail-row full">
                <text class="row-label">请求URL</text>
                <text class="row-value break-all">{{ detailData.requestUrl || '-' }}</text>
              </view>
              <view class="detail-row full">
                <text class="row-label">操作描述</text>
                <text class="row-value break-all">{{ detailData.operDesc || '-' }}</text>
              </view>
            </view>
          </view>

          <view class="detail-section" v-if="detailData.className || detailData.methodName">
            <view class="section-title">代码信息</view>
            <view class="detail-grid">
              <view class="detail-row" v-if="detailData.className">
                <text class="row-label">类名</text>
                <text class="row-value break-all">{{ detailData.className }}</text>
              </view>
              <view class="detail-row" v-if="detailData.methodName">
                <text class="row-label">方法名</text>
                <text class="row-value break-all">{{ detailData.methodName }}</text>
              </view>
            </view>
          </view>

          <view class="detail-section" v-if="detailData.errorMsg">
            <view class="section-title">错误信息</view>
            <view class="error-msg">{{ detailData.errorMsg }}</view>
          </view>

          <view class="detail-section" v-if="detailData.requestParams">
            <view class="section-title-row">
              <view class="section-title">请求参数</view>
              <text class="view-btn" @tap="showJsonDetail('请求参数', detailData.requestParams)">查看完整</text>
            </view>
            <view class="json-content" @tap="showJsonDetail('请求参数', detailData.requestParams)">{{ parseJson(detailData.requestParams) }}</view>
          </view>

          <view class="detail-section" v-if="detailData.responseResult">
            <view class="section-title-row">
              <view class="section-title">响应结果</view>
              <text class="view-btn" @tap="showJsonDetail('响应结果', detailData.responseResult)">查看完整</text>
            </view>
            <view class="json-content" @tap="showJsonDetail('响应结果', detailData.responseResult)">{{ parseJson(detailData.responseResult) }}</view>
          </view>
        </scroll-view>
      </view>
    </view>

    <view v-if="showJson" class="json-modal" @tap="showJson = false">
      <view class="json-modal-content" @tap.stop="">
        <view class="json-modal-header">
          <text class="json-modal-title">{{ jsonTitle }}</text>
          <uni-icons type="close" size="20" color="#909399" @tap="showJson = false"></uni-icons>
        </view>
        <scroll-view scroll-y class="json-modal-body">
          <text class="json-full">{{ jsonContent }}</text>
        </scroll-view>
        <view class="json-modal-footer">
          <button class="btn copy-btn" @tap="copyJson">复制</button>
          <button class="btn close-btn" @tap="showJson = false">关闭</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { list } from '@/api/monitor/operlog'
import { getDictData, getDictLabel } from '@/utils/dict'
import { formatTime } from '@/utils/dateFormat'

export default {
  data() {
    return {
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userName: undefined,
        module: undefined,
        status: undefined
      },
      operTypeList: [],
      operlogList: [],
      loading: false,
      hasMore: true,
      total: 0,
      loadMoreStatus: 'more',
      contentText: {
        contentdown: '上拉显示更多',
        contentrefresh: '正在加载...',
        contentnomore: '没有更多数据了'
      },
      operTypeOptions: [],
      showDetail: false,
      detailData: {},
      showJson: false,
      jsonTitle: '',
      jsonContent: '',
      showFilter: false
    }
  },
  computed: {
    hasFilter() {
      return !!(this.queryParams.module || this.operTypeList.length || this.queryParams.status)
    }
  },
  onLoad(options) {
    if (options.pageTitle) {
      uni.setNavigationBarTitle({
        title: options.pageTitle
      })
    }
    this.initDictData()
    this.getList()
  },
  methods: {
    async initDictData() {
      this.operTypeOptions = await getDictData('user_oper_type') || []
    },
    getDictLabel,
    formatTime,
    toggleOperType(val) {
      const idx = this.operTypeList.indexOf(val)
      if (idx > -1) {
        this.operTypeList.splice(idx, 1)
      } else {
        this.operTypeList.push(val)
      }
    },
    selectStatus(val) {
      this.queryParams.status = val || undefined
    },
    handleFilterConfirm() {
      this.showFilter = false
      this.handleQuery()
    },
    parseJson(str) {
      if (!str) return '-'
      if (typeof str === 'object') return JSON.stringify(str, null, 2)
      try {
        return JSON.stringify(JSON.parse(str), null, 2)
      } catch (e) {
        return str
      }
    },
    showJsonDetail(title, content) {
      this.jsonTitle = title
      this.jsonContent = this.parseJson(content)
      this.showJson = true
    },
    copyJson() {
      uni.setClipboardData({
        data: this.jsonContent,
        success: () => {
          this.$modal.msgSuccess('已复制到剪贴板')
        }
      })
    },
    handleView(item) {
      this.detailData = item
      this.showDetail = true
    },
    showText(title, content) {
      if (!content) return
      uni.showModal({
        title: title,
        content: content,
        showCancel: false,
        confirmText: '关闭'
      })
    },
    async getList(type = 'more') {
      if (this.loading) return
      this.loading = true
      this.loadMoreStatus = 'loading'
      try {
        const params = { ...this.queryParams }
        if (this.operTypeList.length) {
          params.operTypeList = this.operTypeList
        }
        const response = await list(params)
        if (response.code === 200) {
          const listData = response.data || []
          const total = response.total || 0

          if (type === 'refresh' || this.queryParams.pageNum === 1) {
            this.operlogList = listData
          } else {
            this.operlogList = [...this.operlogList, ...listData]
          }
          this.total = total
          this.hasMore = this.operlogList.length < total
          this.loadMoreStatus = this.hasMore ? 'more' : 'noMore'
        } else {
          this.loadMoreStatus = 'more'
        }
      } catch (error) {
        this.loadMoreStatus = 'more'
      } finally {
        this.loading = false
        if (type === 'refresh') {
          uni.stopPullDownRefresh()
        }
      }
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.operlogList = []
      this.hasMore = true
      this.loadMoreStatus = 'more'
      this.getList('refresh')
    },
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        userName: undefined,
        module: undefined,
        status: undefined
      }
      this.operTypeList = []
      this.showFilter = false
      this.handleQuery()
    },
    onPullDownRefresh() {
      this.queryParams.pageNum = 1
      this.operlogList = []
      this.hasMore = true
      this.getList('refresh')
    },
    onReachBottom() {
      if (this.hasMore && !this.loading) {
        this.queryParams.pageNum++
        this.getList('more')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
$color-main: #409eff;
$color-success: #67c23a;
$color-warning: #e6a23c;
$color-danger: #f56c6c;
$color-gray: #909399;
$color-text: #303133;
$color-text-light: #606266;
$color-border: #ebeef5;
$color-bg: #f5f5f5;
$color-card: #fff;

.operlog-container {
  background-color: $color-bg;
  min-height: 100vh;

  .top-bar {
    position: sticky;
    top: 0;
    z-index: 100;
    display: flex;
    align-items: center;
    gap: 20rpx;
    padding: 20rpx 24rpx;
    background-color: $color-card;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);

    .search-input-wrap {
      flex: 1;
      display: flex;
      align-items: center;
      gap: 12rpx;
      height: 72rpx;
      padding: 0 24rpx;
      background-color: #f5f7fa;
      border-radius: 36rpx;

      input {
        flex: 1;
        font-size: 28rpx;
        color: $color-text;
        background: transparent;
        height: 100%;

        &::placeholder {
          color: #c0c4cc;
        }
      }
    }

    .filter-btn {
      width: 72rpx;
      height: 72rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      background-color: #f5f7fa;
      border-radius: 50%;
      flex-shrink: 0;
      position: relative;

      &.active {
        background-color: rgba($color-main, 0.1);

        &::after {
          content: '';
          position: absolute;
          top: 12rpx;
          right: 12rpx;
          width: 14rpx;
          height: 14rpx;
          background-color: $color-main;
          border-radius: 50%;
        }
      }

      &:active {
        opacity: 0.7;
      }
    }
  }

  .filter-drawer {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    z-index: 2000;

    .drawer-mask {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background-color: rgba(0, 0, 0, 0.45);
    }

    .drawer-content {
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      background-color: $color-card;
      border-radius: 24rpx 24rpx 0 0;
      display: flex;
      flex-direction: column;
      max-height: 75vh;

      .drawer-header {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 32rpx;
        border-bottom: 1rpx solid $color-border;
        flex-shrink: 0;

        .drawer-title {
          font-size: 32rpx;
          font-weight: 600;
          color: $color-text;
        }
      }

      .drawer-body {
        flex: 1;
        height: 0;
        padding: 24rpx 32rpx;

        .filter-group {
          margin-bottom: 32rpx;

          &:last-child {
            margin-bottom: 0;
          }

          .filter-label {
            font-size: 28rpx;
            color: $color-text-light;
            margin-bottom: 20rpx;

            .multi-hint {
              font-size: 22rpx;
              color: #c0c4cc;
              margin-left: 8rpx;
            }
          }

          .filter-input-wrap {
            height: 76rpx;
            background-color: #f5f7fa;
            border-radius: 12rpx;
            padding: 0 24rpx;
            display: flex;
            align-items: center;

            input {
              flex: 1;
              font-size: 28rpx;
              color: $color-text;
              background: transparent;

              &::placeholder {
                color: #c0c4cc;
              }
            }
          }

          .chip-list {
            display: flex;
            flex-wrap: wrap;
            gap: 16rpx;

            .chip {
              padding: 14rpx 32rpx;
              background-color: #f5f7fa;
              border-radius: 8rpx;
              font-size: 26rpx;
              color: $color-text-light;
              transition: all 0.2s;

              &.active {
                background-color: rgba($color-main, 0.1);
                color: $color-main;
                border: 1rpx solid $color-main;
              }

              &:active {
                opacity: 0.7;
              }
            }
          }
        }
      }

      .drawer-footer {
        display: flex;
        gap: 24rpx;
        padding: 24rpx 32rpx;
        padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
        border-top: 1rpx solid $color-border;
        flex-shrink: 0;

        .drawer-btn {
          flex: 1;
          height: 84rpx;
          line-height: 84rpx;
          text-align: center;
          border-radius: 12rpx;
          font-size: 30rpx;

          &.reset {
            background-color: #f5f7fa;
            color: $color-text-light;

            &:active {
              background-color: #e9e9eb;
            }
          }

          &.confirm {
            background-color: $color-main;
            color: #fff;

            &:active {
              background-color: darken($color-main, 5%);
            }
          }
        }
      }
    }
  }

  .operlog-list {
    padding: 20rpx 24rpx 40rpx;

    .operlog-item {
      background-color: $color-card;
      margin-bottom: 24rpx;
      border-radius: 16rpx;
      box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
      overflow: hidden;

      .operlog-header {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 24rpx 28rpx;
        border-bottom: 1rpx solid #f0f2f5;

        .operlog-title {
          flex: 1;
          display: flex;
          align-items: center;
          gap: 16rpx;
          overflow: hidden;

          .module-tag {
            background-color: #ecf5ff;
            color: $color-main;
            font-size: 24rpx;
            padding: 6rpx 16rpx;
            border-radius: 6rpx;
            flex-shrink: 0;
          }

          .oper-desc {
            font-size: 30rpx;
            font-weight: 600;
            color: $color-text;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }
        }

        .status-tag {
          font-size: 24rpx;
          padding: 8rpx 20rpx;
          border-radius: 6rpx;
          flex-shrink: 0;
          margin-left: 16rpx;

          &.status-success {
            background-color: #f0f9eb;
            color: $color-success;
          }

          &.status-fail {
            background-color: #fef0f0;
            color: $color-danger;
          }
        }
      }

      .operlog-body {
        padding: 16rpx 28rpx;
        display: flex;
        flex-wrap: wrap;
      }

      .info-row {
        display: flex;
        align-items: center;
        width: 50%;
        line-height: 1.6;
        box-sizing: border-box;
        padding-right: 16rpx;
        margin-bottom: 6rpx;

        &:nth-child(2n) {
          padding-right: 0;
        }

        &.full-width {
          width: 100%;
          padding-right: 0;
        }

        .label {
          font-size: 24rpx;
          color: $color-gray;
          flex-shrink: 0;
          margin-right: 6rpx;
        }

        .value {
          flex: 1;
          font-size: 24rpx;
          color: $color-text;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;

          &.text-ellipsis {
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }

          &.clickable {
            color: $color-main;
            text-decoration: underline;
          }
        }
      }

      .operlog-footer {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 16rpx 28rpx;
        background-color: #fafafa;
        border-top: 1rpx solid #f0f2f5;

        .log-id {
          font-size: 24rpx;
          color: $color-gray;
        }

        .detail-hint {
          display: flex;
          align-items: center;
          gap: 4rpx;
          font-size: 24rpx;
          color: #c0c4cc;
        }
      }

      &:active {
        opacity: 0.85;
      }
    }
  }

  .empty-box {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 120rpx 0;
    background-color: $color-card;
    border-radius: 16rpx;
    margin: 24rpx;

    .empty-text {
      margin-top: 24rpx;
      font-size: 28rpx;
      color: $color-gray;
      letter-spacing: 1rpx;
    }
  }
}

.detail-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 1000;
  display: flex;
  align-items: flex-end;

  .detail-content {
    width: 100%;
    height: 85vh;
    background-color: $color-card;
    border-radius: 24rpx 24rpx 0 0;
    display: flex;
    flex-direction: column;
    overflow: hidden;

    .detail-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 32rpx;
      border-bottom: 1rpx solid $color-border;
      flex-shrink: 0;

      .detail-title {
        font-size: 32rpx;
        font-weight: 600;
        color: $color-text;
      }
    }

    .detail-body {
      flex: 1;
      height: 0;
      padding: 24rpx 32rpx;
      box-sizing: border-box;

      .detail-section {
        margin-bottom: 28rpx;

        .section-title {
          font-size: 28rpx;
          font-weight: 600;
          color: $color-main;
          margin-bottom: 16rpx;
          padding-left: 16rpx;
          border-left: 6rpx solid $color-main;
        }

        .section-title-row {
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin-bottom: 16rpx;

          .section-title {
            margin-bottom: 0;
          }

          .view-btn {
            font-size: 24rpx;
            color: $color-main;
            padding: 6rpx 16rpx;
            border: 1rpx solid $color-main;
            border-radius: 6rpx;
          }
        }

        .detail-grid {
          display: flex;
          flex-wrap: wrap;
        }

        .detail-row {
          display: flex;
          padding: 10rpx 0;
          border-bottom: 1rpx solid #f5f5f5;
          width: 50%;
          box-sizing: border-box;
          padding-right: 20rpx;

          &:nth-child(2n) {
            padding-right: 0;
          }

          &.full {
            width: 100%;
            padding-right: 0;
          }

          &.full + .detail-row:not(.full) {
            width: 50%;
          }

          .row-label {
            min-width: 130rpx;
            font-size: 26rpx;
            color: $color-gray;
            flex-shrink: 0;
          }

          .row-value {
            flex: 1;
            font-size: 26rpx;
            color: $color-text;
            word-break: break-all;

            &.break-all {
              word-break: break-all;
            }

            &.text-success {
              color: $color-success;
            }

            &.text-danger {
              color: $color-danger;
            }
          }
        }

        .error-msg {
          background-color: #fef0f0;
          border-radius: 8rpx;
          padding: 20rpx;
          font-size: 26rpx;
          color: $color-danger;
          word-break: break-all;
        }

        .json-content {
          background-color: #f5f7fa;
          border-radius: 8rpx;
          padding: 20rpx;
          font-size: 24rpx;
          color: $color-text-light;
          word-break: break-all;
          font-family: monospace;
          white-space: pre-wrap;
          max-height: 400rpx;
          overflow: hidden;

          &:active {
            opacity: 0.8;
          }
        }
      }
    }
  }
}

.json-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.6);
  z-index: 2000;
  display: flex;
  align-items: center;
  justify-content: center;

  .json-modal-content {
    width: 92%;
    height: 70vh;
    background-color: $color-card;
    border-radius: 16rpx;
    display: flex;
    flex-direction: column;
    overflow: hidden;

    .json-modal-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 28rpx 32rpx;
      border-bottom: 1rpx solid $color-border;
      flex-shrink: 0;

      .json-modal-title {
        font-size: 30rpx;
        font-weight: 600;
        color: $color-text;
      }
    }

    .json-modal-body {
      flex: 1;
      height: 0;
      padding: 24rpx 32rpx;
      background-color: #1e1e1e;

      .json-full {
        font-size: 24rpx;
        color: #d4d4d4;
        font-family: monospace;
        white-space: pre-wrap;
        word-break: break-all;
        line-height: 1.6;
      }
    }

    .json-modal-footer {
      display: flex;
      gap: 20rpx;
      padding: 24rpx 32rpx;
      border-top: 1rpx solid $color-border;
      flex-shrink: 0;

      .btn {
        flex: 1;
        height: 80rpx;
        line-height: 80rpx;
        border-radius: 8rpx;
        font-size: 28rpx;
        border: none;
        padding: 0;

        &.copy-btn {
          background-color: #f0f2f5;
          color: $color-text;

          &:active {
            background-color: #e4e7ed;
          }
        }

        &.close-btn {
          background-color: $color-main;
          color: #fff;

          &:active {
            background-color: #3a8ee6;
          }
        }
      }
    }
  }
}
</style>