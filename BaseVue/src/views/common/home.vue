<template>
  <div class="mod-home">
    <div class="main_home">
      <div class="leftProject">
        <h3>项目介绍</h3>
        <ul>
          <li>前后端分离，前端采用vue+element-ui，后端采用springboot主流框架</li>
          <li>完全响应式布局（支持电脑、平板、手机等所有主流设备）</li>
          <li>支持多数据源，简单配置即可实现切换。</li>
          <li>支持按钮及数据权限，可自定义部门数据权限。</li>
          <li>动态菜单，通过菜单管理统一管理访问路由</li>
          <li>完善的XSS防范及脚本过滤，彻底杜绝XSS攻击</li>
          <li>Maven多项目依赖，模块及插件分项目，尽量松耦合，方便模块升级、增减模块。</li>
          <li>完善的日志记录体系简单注解即可实现</li>
          <li>支持第三方登录以及管理</li>
        </ul>
      </div>
      <div class="rightUpdate">
        <h3>更新日志</h3>
        <ul>
          <el-collapse v-model="activeName" accordion @change="handleChange">
            <el-collapse-item title="不忘初心，方得始终" name="1">
              <div style="color: green">不乱于心，不困于情。 不畏将来，不念过往。</div>
              <div style="color: blue">深谋若谷，深交若水。深明大义，深悉小节。</div>
              <div style="color: red">善宽以怀，善感以恩。善博以浪，善精以业。</div>
              <div style="color: skyblue">无愧于天，无愧于地。无怍于人，无惧于鬼。</div>
            </el-collapse-item>
            <el-collapse-item title="v2.1 2020-08-17 切换回调地址" name="19">
              <div>更换微信，qq第三方回调地址，切换为：http://xianyum.cn/base/</div>
            </el-collapse-item>
            <el-collapse-item title="v2.0 2019-12-29 日志增加删除" name="18">
              <div>日志增加删除,异步保存用户操作日志.</div>
            </el-collapse-item>
            <el-collapse-item title="v1.8 2019-11-27 首页改版" name="17">
              <div>首页改版，去除天气插件</div>
            </el-collapse-item>
            <el-collapse-item title="v1.7 2019-11-24 接入QQ第三方登录" name="2">
              <div>接入QQ第三方登录</div>
            </el-collapse-item>
            <el-collapse-item title="v1.6 2019-11-11 接入支付宝第三方登录，登录界面更换样式" name="3">
              <div>接入支付宝第三方登录，登录界面更换样式</div>
            </el-collapse-item>
            <el-collapse-item title="v1.5 2019-09-30 加载速度优化，增加微信推送中心功能" name="4">
              <div>网站首页初次加载速度优化，新增微信推送中心，用户可以订阅二维码，来进行实时接收消息</div>
            </el-collapse-item>
            <el-collapse-item title="v1.4 2019-09-27 增加爬虫监控，界面图标美化" name="5">
              <div>集成Gecco爬虫框架，把爬取的数据写到数据库里，并且把每天更新内容推送到微信上，以及界面图标美化</div>
            </el-collapse-item>
            <el-collapse-item title="v1.3 2019-06-28 注册功能" name="6">
              <div>增加用户注册功能，以及用户重复性校验优化</div>
            </el-collapse-item>
            <el-collapse-item title="v1.2 2019-06-20 优化登录模块，增加日志注解" name="7">
              <div>优化登录模块，增加日志注解，使用redis存储token，验证码，ip地址等</div>
            </el-collapse-item>
            <el-collapse-item title="v1.1 2019-06-10 版本新增一些常用的工具类,以及集成mybatis-plus多数据源" name="8">
              <div>版本新增一些常用的工具类,以及集成mybatis-plus多数据源</div>
            </el-collapse-item>
            <el-collapse-item title="v1.0 2019-05-01 开始开发模板" name="9">
              <div>v1.0 开始开发模板</div>
            </el-collapse-item>
          </el-collapse>
        </ul>
      </div>
    </div>
    <div class="contact">
      <h3 >联系方式</h3>
      <ul>
        <img
          style="margin-left: 40px;height: 200px;"
          :src="src"
          alt=""
        >
        <img
          style="margin-left: 40px;height: 172px;"
          :src="zhudong"
          alt=""
        >
      </ul>
    </div>
  </div>
</template>
<style>
  .mod-home {
    line-height: 1.5;
  }
  .main_home{
    width: 100%;
    height: 100%;
  }
  .main_home .leftProject{
    position: absolute;
    margin-left: 20px;
    width: 750px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1)
  }
  .main_home .rightUpdate{
    position: absolute;
    margin-left: 800px;
    width: 48%;
    float: right;
    margin-right: 20px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    height: 750px; /*增加日志会影响这里*/
  }
  .contact{
    margin-left: 20px;
    float:left;
    width: 750px;
    margin-top: 281px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1)
  }
</style>
<script>
  export default {
    data () {
      return {
        activeName: '1',
        src: 'http://oss.xianyum.cn/wechat.jpg',
        zhudong: 'http://oss.xianyum.cn/zhudong.jpg'
      }
    },
    methods: {
      handleChange () {
        if (this.activeName === '') {
          this.activeName = '1'
        }
      }
    }
  }
</script>
