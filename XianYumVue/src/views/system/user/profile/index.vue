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
              <userAvatar />
            </div>
            <ul class="list-group list-group-striped">
              <li class="list-group-item">
                <svg-icon icon-class="user" />
                用户账号
                <div class="pull-right">{{ user.username }}</div>
              </li>
              <li class="list-group-item">
                <svg-icon icon-class="people" />
                用户名称
                <div class="pull-right">{{ user.nickName }}</div>
              </li>
              <li class="list-group-item">
                <svg-icon icon-class="peoples" />
                用户角色
                <div class="pull-right">{{ user.groupRoleName }}</div>
              </li>
              <li class="list-group-item">
                <svg-icon icon-class="phone" />
                手机号码
                <div class="pull-right">{{ user.mobile }}</div>
              </li>
              <li class="list-group-item">
                <svg-icon icon-class="email" />
                用户邮箱
                <div class="pull-right">{{ user.email }}</div>
              </li>
              <li class="list-group-item">
                <svg-icon icon-class="date" />
                创建日期
                <div class="pull-right">{{ parseTime(user.createTime) }}</div>
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
          <el-tabs @tab-click="clickTabPane" v-model="activeTab">
            <el-tab-pane label="基本资料" name="userinfo">
              <userInfo :user="user" />
            </el-tab-pane>
            <el-tab-pane label="修改密码" name="resetPwd">
              <resetPwd />
            </el-tab-pane>
            <el-tab-pane label="三方账户" name="thirdUser" >
              <thirdUser ref="thirdUserRef" />
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
import thirdUser from "./thirdUser";
import { getUserProfile } from "@/api/system/user";

export default {
  name: "Profile",
  components: { userAvatar, userInfo, resetPwd,thirdUser },
  data() {
    return {
      user: {},
      activeTab: "userinfo"
    };
  },
  created() {
    this.getUser();
  },
  mounted() {
    let activeTab = this.$route.params.activeTab
    let successMsg = this.$route.params.successMsg
    let errorMsg = this.$route.params.errorMsg
    if(successMsg){
      this.$modal.msgSuccess(successMsg);
    }
    if(errorMsg){
      this.$modal.msgError(errorMsg);
    }
    if(activeTab){
      this.activeTab = this.$route.params.activeTab;
      // 使用 $nextTick 确保在 DOM 更新后调用方法
      this.$nextTick(() => {
        if(this.activeTab  === 'thirdUser'){
          this.$refs.thirdUserRef.getCurrentUserThirdRelation();
        }
      });
    }
  },
  methods: {
    clickTabPane(tab,event){
      if(tab.index == 2){
        this.$refs.thirdUserRef.getCurrentUserThirdRelation();
      }
    },
    getUser() {
      getUserProfile().then(response => {
        this.user = response.data;
      });
    }
  }
};
</script>
