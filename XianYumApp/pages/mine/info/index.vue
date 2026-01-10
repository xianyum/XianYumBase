<template>
  <view class="container">
    <uni-list>
      <uni-list-item showExtraIcon="true" :extraIcon="{type: 'person-filled'}" title="用户昵称" :rightText="user.nickName" />
      <uni-list-item showExtraIcon="true" :extraIcon="{type: 'staff-filled'}" title="用户角色" :rightText="user.groupRoleName" />
      <uni-list-item showExtraIcon="true" :extraIcon="{type: 'phone-filled'}" title="手机号码" :rightText="user.mobile" />
      <uni-list-item showExtraIcon="true" :extraIcon="{type: 'email-filled'}" title="用户邮箱" :rightText="user.email" />
	    <uni-list-item showExtraIcon="true" :extraIcon="{type: 'calendar-filled'}" title="注册日期" :rightText="formatTime(user.createTime)"/>
    </uni-list>
  </view>
</template>

<script>
  import { getUserProfile } from "@/api/system/user"
  import { formatTime } from '@/utils/dateFormat.js'

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
      formatTime(time) {
        return formatTime(time)
      },
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
