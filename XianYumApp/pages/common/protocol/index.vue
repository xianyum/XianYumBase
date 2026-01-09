<template>
  <view class="agreement-page">
    <!-- 顶部导航栏 -->
    <view class="nav-bar">
      <uni-icons
          type="left"
          size="20"
          color="#333"
          @click="handleBack"
          class="back-icon"
      ></uni-icons>
      <text class="nav-title">{{ pageTitle }}</text>
    </view>

    <!-- 协议内容区域（可滚动，适配长文本） -->
    <scroll-view
        class="agreement-content"
        scroll-y
        show-scrollbar="true"
    >
      <!-- 用户协议内容 -->
      <view v-if="type === 'user'" class="agreement-text">
        <h1 class="main-title">XianYum移动端用户协议</h1>

        <h2 class="sub-title">一、协议总则</h2>
        <p class="text-paragraph">1.1 欢迎您使用XianYum移动端应用（以下简称“本应用”）。本应用由XianYum团队开发并运营。本用户协议（以下简称“本协议”）是您与我们之间就使用本应用所订立的具有法律约束力的协议。</p>
        <p class="text-paragraph">1.2 您在使用本应用前，应仔细阅读并理解本协议全部条款，特别是涉及您重大权益的免责条款、争议解决条款等。您点击“同意”或实际使用本应用服务，即视为您已充分理解并同意本协议的全部内容，愿意接受本协议的约束。若您不同意本协议任何条款，请勿使用本应用。</p>
        <p class="text-paragraph">1.3 我们有权根据业务发展需要对本协议进行修订，修订后的协议将通过本应用显著位置公示。若您在协议修订后继续使用本应用，即视为您同意修订后的协议；若您不同意，应立即停止使用本应用。</p>

        <h2 class="sub-title">二、用户基本权利与义务</h2>
        <h3 class="sub-sub-title">2.1 用户权利</h3>
        <ul class="text-list">
          <li>有权在符合本协议约定的前提下，正常使用本应用提供的各项核心服务，包括但不限于美食信息浏览、商家查询、订单提交、评价分享等。</li>
          <li>有权根据本应用规则，对个人账号信息进行查询、更正、补充，并享有账号注销的权利。</li>
          <li>有权对本应用服务质量提出建议、投诉或举报，我们将依法依规进行处理。</li>
        </ul>
        <h3 class="sub-sub-title">2.2 用户义务</h3>
        <ul class="text-list">
          <li>应提供真实、准确、完整的个人信息（如注册账号时填写的手机号、昵称等），不得冒用他人身份信息注册或使用本应用。若个人信息发生变更，应及时更新。</li>
          <li>应妥善保管个人账号及密码，对账号下的所有操作行为承担法律责任。若发现账号被盗用、泄露等情况，应立即通知我们并采取相应安全措施。</li>
          <li>使用本应用时，应遵守国家法律法规及公序良俗，不得利用本应用发布违法信息、虚假信息，不得从事欺诈、侵害他人合法权益等违法违规行为。</li>
          <li>不得对本应用进行反向工程、破解、篡改，不得干扰本应用正常运行或侵犯本应用相关的知识产权。</li>
        </ul>

        <h2 class="sub-title">三、应用服务内容与规范</h2>
        <p class="text-paragraph">3.1 本应用为用户提供美食相关的信息展示、交易撮合、评价互动等服务，具体服务内容以应用实际功能为准。我们有权根据业务调整优化服务内容，无需另行单独通知用户，但重大调整将通过公示告知。</p>
        <p class="text-paragraph">3.2 用户在使用本应用交易服务时，应遵守交易规则及商家相关约定，如实提交订单信息。因用户自身原因（如填写信息错误、未及时付款等）导致的交易纠纷或损失，由用户自行承担。</p>
        <p class="text-paragraph">3.3 用户发布的评价、分享等内容，应真实、客观，不得含有侮辱、诽谤、歧视等违法或不良信息。我们有权对用户发布的内容进行审核，对违规内容可采取删除、屏蔽等措施，并追究相关用户责任。</p>

        <h2 class="sub-title">四、知识产权声明</h2>
        <p class="text-paragraph">4.1 本应用的软件著作权、商标权、专利权及所有内容（包括但不限于文字、图片、音频、视频、图标等）的知识产权均归我们所有。</p>
        <p class="text-paragraph">4.2 未经我们书面授权，用户不得擅自复制、传播、展示、使用本应用的任何知识产权成果，否则将承担相应的法律责任。</p>
        <p class="text-paragraph">4.3 用户在本应用发布的合法内容，其知识产权归用户所有，但用户授予我们免费、非独占、可转授权的使用权，允许我们在本应用相关服务中使用该内容。</p>

        <h2 class="sub-title">五、免责条款</h2>
        <p class="text-paragraph">5.1 因不可抗力（如自然灾害、网络故障、政策调整等）导致本应用无法正常提供服务的，我们不承担违约责任，但应及时通知用户并采取补救措施。</p>
        <p class="text-paragraph">5.2 因用户自身操作不当、设备故障、第三方软件干扰等非我们原因导致的损失，我们不承担责任。</p>
        <p class="text-paragraph">5.3 本应用仅为信息展示和交易撮合平台，对于商家提供的商品或服务质量，我们不承担直接保证责任。用户与商家之间的纠纷，应自行协商解决或通过法律途径处理。</p>

        <h2 class="sub-title">六、协议终止与账号注销</h2>
        <p class="text-paragraph">6.1 若用户违反本协议约定，我们有权随时终止向用户提供服务，并可注销用户账号，且无需退还已产生的费用（如有）。</p>
        <p class="text-paragraph">6.2 用户可通过本应用“我的-设置-账号注销”功能申请注销账号。账号注销后，我们将停止提供所有服务，并按照相关法律法规规定处理用户个人信息。</p>

        <h2 class="sub-title">七、争议解决</h2>
        <p class="text-paragraph">因本协议引起的或与本协议相关的争议，双方应首先通过友好协商解决；协商不成的，任何一方均有权向我们所在地有管辖权的人民法院提起诉讼。</p>

        <h2 class="sub-title">八、其他</h2>
        <p class="text-paragraph">本协议未尽事宜，适用国家相关法律法规规定。若本协议条款与法律法规相抵触，以法律法规为准。</p>
        <p class="text-paragraph">我们的电子邮箱：80616059@qq.com。</p>
      </view>

      <!-- 隐私协议内容 -->
      <view v-if="type === 'privacy'" class="agreement-text">
        <h1 class="main-title">XianYum移动端隐私协议</h1>

        <h2 class="sub-title">一、隐私保护声明</h2>
        <p class="text-paragraph">1.1 我们高度重视用户个人信息保护，严格遵守《中华人民共和国网络安全法》《中华人民共和国个人信息保护法》等相关法律法规，致力于保障用户个人信息的安全与合法使用。</p>
        <p class="text-paragraph">1.2 本隐私协议旨在向您说明我们如何收集、使用、存储、传输、共享您的个人信息，以及您享有的相关权利。您使用本应用，即视为您同意我们按照本隐私协议处理您的个人信息。</p>

        <h2 class="sub-title">二、个人信息的收集与使用</h2>
        <h3 class="sub-sub-title">2.1 必要个人信息</h3>
        <p class="text-paragraph">为保障本应用核心服务正常运行，我们将收集您的必要个人信息，未收集该信息将无法为您提供相应服务：</p>
        <ul class="text-list">
          <li>账号注册与登录：收集您的手机号，用于账号注册、登录验证及安全保障。</li>
          <li>交易服务：收集您的收货地址、联系人信息，用于订单配送与交易完成；收集您的支付相关信息（由第三方支付机构处理，我们不直接存储完整支付密码等敏感信息），用于完成交易支付。</li>
          <li>位置服务：若您使用商家查询、附近美食推荐等功能，我们将收集您的地理位置信息（需您主动授权），用于精准推荐周边商家及服务。</li>
        </ul>

        <h3 class="sub-sub-title">2.2 非必要个人信息</h3>
        <p class="text-paragraph">为提升服务体验，我们可能收集以下非必要个人信息，您可选择是否提供：</p>
        <ul class="text-list">
          <li>个人资料：如昵称、头像等，用于个性化展示及互动交流。</li>
          <li>使用行为信息：如您的浏览记录、搜索记录、订单历史等，用于为您提供个性化推荐、优化服务内容。</li>
          <li>设备信息：如设备型号、操作系统版本、唯一设备标识符等，用于保障应用运行安全、排查故障。</li>
        </ul>

        <h3 class="sub-sub-title">2.3 信息使用规则</h3>
        <p class="text-paragraph">我们仅在以下场景使用您的个人信息：</p>
        <ul class="text-list">
          <li>为您提供本应用的核心服务及功能；</li>
          <li>保障应用账号及交易安全，防范欺诈、盗号等风险；</li>
          <li>优化应用体验，提升服务质量；</li>
          <li>依法配合监管部门、司法机关的调查取证要求。</li>
        </ul>

        <h2 class="sub-title">三、个人信息的存储与保护</h2>
        <p class="text-paragraph">3.1 我们将按照法律法规要求，将您的个人信息存储在境内服务器，不向境外传输。</p>
        <p class="text-paragraph">3.2 我们采用加密存储、访问权限控制、安全审计等技术措施，保障您的个人信息不被泄露、篡改、窃取。</p>
        <p class="text-paragraph">3.3 您的个人信息存储期限将与您使用本应用的服务周期一致，账号注销后，我们将在合理期限内删除或匿名化处理您的个人信息。</p>

        <h2 class="sub-title">四、个人信息的共享与披露</h2>
        <p class="text-paragraph">4.1 未经您的明确授权，我们不会将您的个人信息共享给任何第三方，除非法律法规另有规定。</p>
        <p class="text-paragraph">4.2 为完成交易服务，我们可能将必要的订单信息共享给合作的商家、配送机构，但会要求第三方严格遵守隐私保护约定。</p>
        <p class="text-paragraph">4.3 我们不会将您的个人信息用于盈利性出售，也不会向无关第三方披露。</p>

        <h2 class="sub-title">五、用户的信息权利</h2>
        <p class="text-paragraph">5.1 您有权查询、更正、删除您的个人信息，可通过本应用“我的-设置-隐私管理”功能操作。</p>
        <p class="text-paragraph">5.2 您有权拒绝非必要个人信息的收集，可通过手机系统权限设置、应用内隐私设置关闭相关授权。</p>
        <p class="text-paragraph">5.3 您有权注销账号，账号注销后，我们将停止使用并删除您的个人信息（法律法规要求留存的除外）。</p>

        <h2 class="sub-title">六、未成年人保护</h2>
        <p class="text-paragraph">6.1 若您为未满18周岁的未成年人，应在监护人陪同下使用本应用，并由监护人同意本隐私协议。</p>
        <p class="text-paragraph">6.2 我们不会主动收集未成年人的个人信息，若发现未成年人信息，将在监护人同意后进行处理。</p>

        <h2 class="sub-title">七、协议更新</h2>
        <p class="text-paragraph">我们有权根据法律法规更新或调整本隐私协议，修订后的协议将通过应用公示，继续使用本应用即视为同意修订后的协议。</p>

        <h2 class="sub-title">八、联系方式</h2>
        <p class="text-paragraph">若您对个人信息保护有任何疑问、投诉，可通过以下方式联系我们：</p>

        <p class="text-paragraph">电子邮箱：80616059@qq.com</p>
        <p class="text-paragraph">联系地址：陕西省西安市</p>
      </view>
    </scroll-view>
  </view>
</template>

<script>
export default {
  name: 'XianYumAgreement',
  data() {
    return {
      type: 'user', // 协议类型：user-用户协议，privacy-隐私协议
      pageTitle: '用户协议'
    }
  },
  onLoad(options) {
    // 接收跳转参数，切换协议类型（如从注册页传 type=privacy）
    if (options?.type) {
      this.type = options.type;
      this.pageTitle = options.type === 'user' ? '用户协议' : '隐私协议';
    }
  },
  methods: {
    // 返回上一页
    handleBack() {
      uni.navigateBack({
        delta: 1
      });
    }
  }
}
</script>

<style scoped>
/* 页面整体样式 */
.agreement-page {
  width: 100%;
  height: 100vh;
  background-color: #f8f8f8;
}

/* 导航栏样式 */
.nav-bar {
  display: flex;
  align-items: center;
  padding: 12px 15px;
  background-color: #fff;
  border-bottom: 1px solid #eee;
  position: sticky;
  top: 0;
  z-index: 99;
}
.back-icon {
  margin-right: 10px;
}
.nav-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

/* 协议内容区域 */
.agreement-content {
  width: 100%;
  height: calc(100vh - 50px);
  padding: 20px 15px;
  box-sizing: border-box;
}

/* 协议文本样式（适配移动端阅读） */
.agreement-text {
  color: #333;
  font-size: 14px;
  line-height: 1.8;
}
.main-title {
  font-size: 18px;
  font-weight: 700;
  text-align: center;
  margin-bottom: 25px;
  color: #222;
}
.sub-title {
  font-size: 16px;
  font-weight: 600;
  margin: 20px 0 10px;
  color: #222;
}
.sub-sub-title {
  font-size: 15px;
  font-weight: 600;
  margin: 15px 0 8px;
  color: #444;
}
.text-paragraph {
  margin-bottom: 10px;
  text-indent: 2em; /* 首行缩进，提升阅读体验 */
}
.text-list {
  margin: 10px 0 10px 20px;
}
.text-list li {
  margin-bottom: 8px;
  list-style: disc;
}
</style>