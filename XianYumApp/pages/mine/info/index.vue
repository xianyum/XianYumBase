<template>
  <view class="container">
    <uni-list>
      <uni-list-item showExtraIcon="true" :extraIcon="{type: 'person-filled'}" title="用户昵称" :rightText="user.nickName" />
      <uni-list-item showExtraIcon="true" :extraIcon="{type: 'phone-filled'}" title="手机号码" :rightText="user.mobile" />
      <uni-list-item showExtraIcon="true" :extraIcon="{type: 'email-filled'}" title="用户邮箱" :rightText="user.email" />
      <uni-list-item showExtraIcon="true" :extraIcon="{type: 'staff-filled'}" title="用户角色" :rightText="user.groupRoleName" />
	  <uni-list-item showExtraIcon="true" :extraIcon="{type: 'calendar-filled'}" title="创建日期" :rightText="user.createTime"/>
    </uni-list>
  </view>
</template>

<script>
  import { getUserProfile } from "@/api/system/user"

  export default {
    data() {
      return {
        user: {}
      }
    },
    onLoad() {
      this.getUser()
    },
    methods: {
      getUser() {
        getUserProfile().then(response => {
          this.user = response.data
        })
      }
    }
  }
</script>

<style lang="scss">
  page {
    background-color: #ffffff;
  }
</style>
