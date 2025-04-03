# XianYumBase

#### 项目简介

1. 定时爬取某娱乐网站数据，进行实时推送到微信/钉钉
2. 支持QQ、支付宝等第三方登录
3. 支持按钮及数据权限，可自定义部门数据权限。
4. 动态菜单，通过菜单管理统一管理访问路由
5. 支持多数据源，简单配置即可实现切换
6. 完善的日志记录体系简单注解即可实现
7. 在线演示地址：[https://base.xianyum.cn](https://base.xianyum.cn)

#### 项目截图

![微信截图_20201203201546](https://xiaoyaxiaokeai.gitee.io/base/20201113/%E5%BE%AE%E4%BF%A1%E6%88%AA%E5%9B%BE_20201203201546.png)



![微信截图_20201203201602](https://xiaoyaxiaokeai.gitee.io/base/20201113/%E5%BE%AE%E4%BF%A1%E6%88%AA%E5%9B%BE_20201203201602.png)

#### 软件架构

**后端**

SpringBoot 

Mybatis-Plus

Shiro

Redis

Gecco爬虫

**前端**

vue

element-ui

#### 安装教程

1. 后端修改application-test.yml 160行mysql账号密码以及url，139以及141行redis密码，启动项目即可

2. 前端安装

   ```java
   npm install
   
   npm run dev
   ```

   

#### 使用说明

1. 账号密码默认admin/123456

#### 项目感谢

​	感谢[renren-fast](https://gitee.com/renrenio)，本项目大多借鉴renren开源

​	
