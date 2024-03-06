<template>
  <div class="app-container">
    <el-table v-if="showThirdUserList" :data="thirdUserList">
      <el-table-column label="序号" type="index" align="center" width="50" />
      <el-table-column label="绑定账号信息" align="center">
        <template v-slot="scope">
          <div v-if="scope.row.thirdType == 0">QQ</div>
          <div v-if="scope.row.thirdType == 1">支付宝</div>
        </template>
      </el-table-column>
      <el-table-column label="三方用户" align="center" prop="openUserName" />
      <el-table-column label="绑定时间" align="center">
        <template v-slot="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template v-slot="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-close"
            @click="unbindUserThirdRelation(scope.row.id)"
          >解除绑定</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div v-if="isAllUnbindFlag">
      <br>
      <div>你还可以绑定以下第三方帐号</div>
      <br>
      <span v-if="bindAliFlag" class="other_login_info" @click="bindAli()" title="绑定支付宝"><svg-icon icon-class="zhifubaologin" class-name='icon_style'></svg-icon></span>
      <span v-if="bindQQFlag" class="other_login_info" @click="bindQQ()" title="绑定QQ"><svg-icon icon-class="qqlogin" class-name='icon_style'></svg-icon></span>
    </div>
  </div>
</template>

<script>
import { getCurrentUserThirdRelation,unbindUserThirdRelation } from "@/api/system/user";

export default {
  data() {
    return {
      thirdUserList: [],
      isAllUnbindFlag: true,
      bindAliFlag: true,
      bindQQFlag: true,
      showThirdUserList: true
    };
  },
  created() {
  },
  methods: {
    unbindUserThirdRelation(id){
      this.$modal.confirm('确定要解除绑定吗?').then(function() {
        return unbindUserThirdRelation(id);
      }).then(() => {
        this.getCurrentUserThirdRelation();
        this.$modal.msgSuccess("解除绑定成功！");
      }).catch(() => {});
    },
    bindAli(){
      let qqUrl = 'https://graph.qq.com/oauth2.0/authorize?response_type=code&client_id=101831000&redirect_uri=https%3A%2F%2Fbase.xianyum.cn%2FthirdLogin%3FloginType%3Dqq%26type%3DbindUser%0A%20%20%20%20%20';
      window.location.replace(qqUrl)
    },
    bindQQ(){
      let aliUrl = 'https://openauth.alipay.com/oauth2/publicAppAuthorize.htm?app_id=2019110868997443&scope=auth_user&redirect_uri=https://base.xianyum.cn/thirdLogin?loginType=alipay&type=bindUser'
      window.location.replace(aliUrl)
    },
    getCurrentUserThirdRelation(){
      getCurrentUserThirdRelation().then(result =>{
        this.thirdUserList = result.data
        if(this.thirdUserList.length > 0){
          this.bindQQFlag = !this.thirdUserList.some(item => item.thirdType === 0);
          this.bindAliFlag = !this.thirdUserList.some(item => item.thirdType === 1);
          this.isAllUnbindFlag = this.bindQQFlag || this.bindAliFlag;
        }else{
          this.showThirdUserList = false
        }
      })
    }
  }
};
</script>
<style rel="stylesheet/scss" lang="scss">
.icon_style {
  font-size: 38px;
  margin-right: 10px;
}
</style>
