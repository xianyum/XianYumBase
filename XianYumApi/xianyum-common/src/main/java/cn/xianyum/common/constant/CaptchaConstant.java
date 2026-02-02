package cn.xianyum.common.constant;

/**
 * @author xianyum
 * @date 2026/1/31 21:01
 */
public class CaptchaConstant {

    // ====================== 验证码模板文件路径常量 ======================
    /** 滑块1模板-轮廓图片路径 */
    public static final String SLIDER_TEMPLATE_1_ACTIVE_PATH = "META-INF/cut-image/template/slider_1/active.png";
    /** 滑块1模板-凹槽图片路径 */
    public static final String SLIDER_TEMPLATE_1_FIXED_PATH = "META-INF/cut-image/template/slider_1/fixed.png";

    // ====================== 验证码资源URL常量 ======================
    /** 验证码默认背景图URL（统一管理，便于后续替换） */
    public static final String CAPTCHA_DEFAULT_BACKGROUND_URL = "https://picsum.photos/600/360";

    // ====================== 资源类型常量 ======================
    /** 资源类型-类路径 */
    public static final String RESOURCE_TYPE_CLASSPATH = "classpath";
    /** 资源类型-URL */
    public static final String RESOURCE_TYPE_URL = "url";

    /** 字体路径  */
    public static final String FONT_PATH = "font/alibaba.ttf";

    /** 字体标识  */
    public static final String FONT_TAG = "font1";
}
