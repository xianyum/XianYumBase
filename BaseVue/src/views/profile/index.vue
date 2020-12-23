<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="6" :xs="24">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>个人信息</span>
          </div>
          <div>
            <div class="text-center">
              <userAvatar :user="user" />
            </div>
            <ul class="list-group list-group-striped">
              <li class="list-group-item">
                <icon-svg name="user"></icon-svg>用户账号
                <div class="pull-right">{{ user.username }}</div>
              </li>
              <li class="list-group-item">
                <icon-svg name="phone"></icon-svg>手机号码
                <div class="pull-right">{{ user.mobile }}</div>
              </li>
              <li class="list-group-item">
                <icon-svg name="email"></icon-svg>用户邮箱
                <div class="pull-right">{{ user.email }}</div>
              </li>
              <li class="list-group-item">
                <icon-svg name="date"></icon-svg>创建日期
                <div class="pull-right">{{ user.createTime }}</div>
              </li>
            </ul>
          </div>
        </el-card>
      </el-col>
      <el-col :span="18" :xs="24">
        <el-card>
          <div slot="header" class="clearfix">
            <span>基本资料</span>
          </div>
          <el-tabs v-model="activeTab">
            <el-tab-pane label="基本资料" name="userinfo">
              <userInfo :user="user" @refreshDataList="getUserInfo"/>
            </el-tab-pane>
            <el-tab-pane label="修改密码" name="resetPwd">
              <resetPwd :user="user" />
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import userAvatar from "./userAvatar";
import userInfo from "./userInfo";
import resetPwd from "./resetPwd";

export default {
  name: "Profile",
  components: { userAvatar, userInfo, resetPwd },
  data() {
    return {
      user: {},
      roleGroup: {},
      postGroup: {},
      activeTab: "userinfo"
    };
  },
  created() {
    this.getUserInfo();
  },
  methods: {
    getUserInfo () {
      this.$http({
        url: this.$http.adornUrl('/user/currentUser'),
        method: 'get',
        params: ''
      }).then(({data}) => {
        if (data && data.code === 200) {
          this.user = data.user
          this.user.avatar = 'https://xiaoyaxiaokeai.gitee.io/base/20201018/457a981a-b7fe-4777-a7b8-ed1a5e8abf2e.jpg'
          this.user.createTime = new Date(data.user.createTime).Format('yyyy-MM-dd hh:mm:ss');
        }
      })
    }
  }
};
</script>
<style rel="stylesheet/scss" lang="scss">

.app-container {
  padding: 5px;
}
/* image */
.img-circle {
  border-radius: 50%;
}

.img-lg {
  width: 120px;
  height: 120px;
}
.list-group {
  padding-left: 0px;
  list-style: none;
}
.list-group-striped > .list-group-item {
  border-left: 0;
  border-right: 0;
  border-radius: 0;
  padding-left: 0;
  padding-right: 0;
}
.clearfix {
  &:after {
    visibility: hidden;
    display: block;
    font-size: 0;
    content: " ";
    clear: both;
    height: 0;
  }
}
.text-center {
  text-align: center
}
.pull-right {
  float: right !important;
}
.list-group-item {
  border-bottom: 1px solid #e7eaec;
  border-top: 1px solid #e7eaec;
  margin-bottom: -1px;
  padding: 11px 0px;
  font-size: 13px;
}
.list-group-striped > .list-group-item {
  border-left: 0;
  border-right: 0;
  border-radius: 0;
  padding-left: 0;
  padding-right: 0;
}

</style>
