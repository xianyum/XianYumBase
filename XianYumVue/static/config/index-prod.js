/**
 * 生产环境
 */
;(function () {
  window.SITE_CONFIG = {};

  // 阿里云地址
  window.SITE_CONFIG['baseUrl'] = 'https://base.xianyum.cn/api';
  // cdn地址 = 域名 + 版本号
  window.SITE_CONFIG['domain']  = './'; // 域名
  window.SITE_CONFIG['version'] = '';   // 版本号(年月日时分)
  window.SITE_CONFIG['cdnUrl']  = window.SITE_CONFIG.domain + window.SITE_CONFIG.version;
})();
