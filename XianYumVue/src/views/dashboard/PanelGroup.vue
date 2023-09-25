<template>
  <el-row :gutter="40" class="panel-group">
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel" @click="handleSetLineChartData('operLog')">
        <div class="card-panel-icon-wrapper icon-people">
          <svg-icon icon-class="api" class-name="card-panel-icon" />
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            接口访问量
          </div>
          <count-to :start-val="0" :end-val= operLogCount :duration="1000" class="card-panel-num" />
        </div>
      </div>
    </el-col>
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel" @click="handleSetLineChartData('messages')">
        <div class="card-panel-icon-wrapper icon-message">
          <svg-icon icon-class="message" class-name="card-panel-icon" />
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            消息发送量
          </div>
          <count-to :start-val="0" :end-val= messageLogCount :duration="1000" class="card-panel-num" />
        </div>
      </div>
    </el-col>
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel" @click="handleSetLineChartData('job')">
        <div class="card-panel-icon-wrapper icon-money">
          <svg-icon icon-class="job-home-index" class-name="card-panel-icon" />
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            任务调度量
          </div>
          <count-to :start-val="0" :end-val= jobLogCount :duration="1000" class="card-panel-num" />
        </div>
      </div>
    </el-col>
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel" @click="handleSetLineChartData('proxy')">
        <div class="card-panel-icon-wrapper icon-shopping">
          <svg-icon icon-class="proxy" class-name="card-panel-icon" />
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            远程在线量
          </div>
          <count-to :start-val="0" :end-val= onlineProxyCount :duration="1000" class="card-panel-num" />
        </div>
      </div>
    </el-col>
  </el-row>
</template>

<script>
import CountTo from 'vue-count-to'
import {getOnlineProxyCount} from "@/api/xianyu/proxy";
import {getMessageLogCount} from "@/api/message/monitor";
import {getJobLogCount} from "@/api/job/jobLog";
import {getOperLogCount} from "@/api/monitor/operlog";

export default {
  components: {
    CountTo
  },
  data() {
    return {
      onlineProxyCount: 0,
      messageLogCount: 0,
      jobLogCount: 0,
      operLogCount: 0
    }
  },
  created() {
    this.getVisitCountByBase();
  },
  methods: {
    getVisitCountByBase(){
      this.getOnlineProxyCount()
      this.getMessageLogCount()
      this.getJobLogCount()
      this.getOperLogCount()
    },
    getOnlineProxyCount(){
      getOnlineProxyCount().then(res => {
        this.onlineProxyCount = res.data
      });
    },
    getOperLogCount(){
      getOperLogCount().then(res => {
        this.operLogCount = res.data
      });
    },
    getMessageLogCount(){
      getMessageLogCount().then(res => {
        this.messageLogCount = res.data
      });
    },
    getJobLogCount(){
      getJobLogCount().then(res => {
        this.jobLogCount = res.data
      });
    },
    handleSetLineChartData(type) {
      this.$emit('handleSetLineChartData', type)
    }
  }
}
</script>

<style lang="scss" scoped>
.panel-group {
  margin-top: 18px;

  .card-panel-col {
    margin-bottom: 32px;
  }

  .card-panel {
    height: 108px;
    cursor: pointer;
    font-size: 12px;
    position: relative;
    overflow: hidden;
    color: #666;
    background: #fff;
    box-shadow: 4px 4px 40px rgba(0, 0, 0, .05);
    border-color: rgba(0, 0, 0, .05);

    &:hover {
      .card-panel-icon-wrapper {
        color: #fff;
      }

      .icon-people {
        background: #40c9c6;
      }

      .icon-message {
        background: #36a3f7;
      }

      .icon-money {
        background: #b1c671;
      }

      .icon-shopping {
        background: #34bfa3
      }
    }

    .icon-people {
      color: #40c9c6;
    }

    .icon-message {
      color: #36a3f7;
    }

    .icon-money {
      color: #51f49d;
    }

    .icon-shopping {
      color: #34bfa3
    }

    .card-panel-icon-wrapper {
      float: left;
      margin: 14px 0 0 14px;
      padding: 16px;
      transition: all 0.38s ease-out;
      border-radius: 6px;
    }

    .card-panel-icon {
      float: left;
      font-size: 48px;
    }

    .card-panel-description {
      float: right;
      font-weight: bold;
      margin: 26px;
      margin-left: 0px;

      .card-panel-text {
        line-height: 18px;
        color: rgba(0, 0, 0, 0.45);
        font-size: 16px;
        margin-bottom: 12px;
      }

      .card-panel-num {
        font-size: 20px;
      }
    }
  }
}

@media (max-width:550px) {
  .card-panel-description {
    display: none;
  }

  .card-panel-icon-wrapper {
    float: none !important;
    width: 100%;
    height: 100%;
    margin: 0 !important;

    .svg-icon {
      display: block;
      margin: 14px auto !important;
      float: none !important;
    }
  }
}
</style>
