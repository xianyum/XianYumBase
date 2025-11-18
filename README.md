# XianYumBase

## 项目概述
XianYumBase 是一个功能丰富的基础平台，提供定时数据爬取与推送、第三方登录集成、精细化权限管理、动态配置等企业级功能，支持多场景业务扩展，为开发者提供高效的系统构建解决方案。

在线演示地址：[https://base.xianyum.cn](https://base.xianyum.cn)

## 功能特点
1. **数据爬取与推送**  
   定时爬取娱乐网站数据，支持实时推送到微信、钉钉等平台，通过定时任务调度（Cron表达式配置）实现自动化流程。

2. **用户认证与授权**  
   - 支持支付宝等第三方登录，集成支付宝OAuth授权流程  
   - 基于Spring Security的权限控制，包含按钮级权限与数据权限管理  
   - 自定义部门数据权限配置，保障数据访问安全

3. **系统配置与管理**  
   - 动态菜单管理，统一配置前端路由，支持菜单显隐与权限关联  
   - 字典管理与缓存刷新机制，确保配置实时生效  
   - 多数据源支持，简单配置即可实现数据源切换

4. **消息与通知**  
   - 完善的消息发送机制，支持简单消息与标准消息模板  
   - 企业微信配置管理，自定义消息推送参数  
   - 阿里云效流水线执行结果回调通知

5. **安全与加密**  
   - 集成RSA、AES、XOR等多种加密算法，保障数据传输安全  
   - 滑动/点选验证机制，防止恶意请求  
   - 接口请求加密传输，支持DES、JSEncrypt加密方式

6. **日志与监控**  
   - 完善的日志记录体系，通过注解快速实现操作日志记录  
   - 在线用户监控与强退功能，便于系统运维

## 技术架构

### 后端技术栈
- **核心框架**：Spring Boot  
- **数据访问**：MyBatis-Plus（增强MyBatis，简化CRUD操作）  
- **安全框架**：Spring Security（认证授权、令牌管理）  
- **缓存组件**：Redis（数据缓存、会话管理）  
- **接口文档**：Swagger/SpringFox（API文档自动生成）  
- **任务调度**：基于Cron表达式的定时任务框架  
- **网络通信**：OkHttp3（HTTP客户端）、Netty（代理服务支持）  
- **数据爬取**：Gecco（网页爬虫框架）  
- **加密工具**：RSA、AES、XOR等多种加密算法实现  
- **第三方集成**：支付宝SDK（第三方登录）

### 前端技术栈
- **核心框架**：Vue 2.x  
- **UI组件库**：Element UI  
- **状态管理**：Vuex  
- **路由管理**：Vue Router  
- **HTTP客户端**：Axios  
- **加密工具**：Crypto-js、JSEncrypt  
- **其他工具**：vue-puzzle-vcode（图形验证）、vue-treeselect（树形选择器）等

## 安装教程
### 后端部署
1. 配置数据源与缓存：  
   修改 `application-test.yml` 中第160行的MySQL连接信息（账号、密码、URL），以及第139、141行的Redis密码。  
2. 启动项目：通过IDE直接运行主类，或打包为JAR文件后执行 `java -jar xianyum-base.jar`。

### 前端部署
1. 安装依赖（建议使用npm，国内用户可配置镜像）：  
   ```bash
   npm install
   # 或使用国内镜像加速
   npm install --registry=https://registry.npmmirror.com
   ```
2. 启动开发服务：  
   ```bash
   npm run dev
   ```
3. 访问地址：http://localhost:80

## 构建与发布
- 测试环境构建：  
  ```bash
  npm run build:stage
  ```
- 生产环境构建：  
  ```bash
  npm run build:prod
  ```

## 使用说明
1. 初始账号：admin，初始密码：123456  
2. 接口文档：项目集成Swagger，启动后可通过配置路径（默认 `/swagger-ui/index.html`）访问  
3. 图标设置：使用自定义图标时需手动移除 `fill` 属性  
4. 第三方登录：支付宝登录需在系统中配置对应应用的APP_ID、私钥等信息

## 项目截图
![微信截图_20201203201546](https://xiaoyaxiaokeai.gitee.io/base/20201113/%E5%BE%AE%E4%BF%A1%E6%88%AA%E5%9B%BE_20201203201546.png)  
![微信截图_20201203201602](https://xiaoyaxiaokeai.gitee.io/base/20201113/%E5%BE%AE%E4%BF%A1%E6%88%AA%E5%9B%BE_20201203201602.png)

## 贡献指南
1. Fork 本仓库  
2. 创建特性分支（Feat_xxx）  
3. 提交代码并确保通过基础测试  
4. 创建 Pull Request 描述功能改进或问题修复

## 致谢
感谢开源社区提供的技术支持与灵感，本项目在开发过程中借鉴了诸多优秀开源项目的设计思路。

## 开源协议
本项目基于 MIT 协议开源，详情参见 LICENSE 文件。
