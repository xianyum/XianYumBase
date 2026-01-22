package cn.xianyum.common.constant;

/**
 * 通用常量信息
 *
 * @author zhangwei
 */
public class Constants {

    /**
     * 私有构造方法，防止类被实例化（常量类无需实例化）
     */
    private Constants() {
        throw new AssertionError("常量类禁止实例化");
    }

    /**
     * www主域
     */
    public static final String WWW = "www.";

    /**
     * http请求
     */
    public static final String HTTP = "http://";

    /**
     * https请求
     */
    public static final String HTTPS = "https://";

    /** 默认USER_AGENT */
    public static final String USER_AGENT_KEY = "User-Agent";
    public static final String USER_AGENT_VALUE = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36)";

    /** 是否菜单外链（否） */
    public static final String NO_FRAME = "1";

    /** 菜单类型（目录） */
    public static final String TYPE_DIR = "M";

    /** 菜单类型（菜单） */
    public static final String TYPE_MENU = "C";

    /** 菜单类型（按钮） */
    public static final String TYPE_BUTTON = "F";

    /** Layout组件标识 */
    public static final String LAYOUT = "Layout";

    /** ParentView组件标识 */
    public static final String PARENT_VIEW = "ParentView";

    /** InnerLink组件标识 */
    public static final String INNER_LINK = "InnerLink";

    /** 是否菜单外链（是） */
    public static final String YES_FRAME = "0";

    /** 没有权限提示语 */
    public static final String NO_PERMISSION_MESSAGE = "对不起，您没有权限操作,请联系网站管理员！";
    public static final String CHECK_SIGN_MESSAGE = "Check Sign Error.";

    /** admin账号 */
    public static final String USER_ADMIN_ACCOUNT = "admin";

    /** SQL WHERE 1=1  */
    public static final String SQL_EQUALS_DEFAULT = " 1 = 1 ";

    /** http请求方法 */
    public static final String HTTP_GET_METHOD = "get";
    public static final String HTTP_POST_METHOD = "post";
    public static final String HTTP_PUT_METHOD = "put";
    public static final String HTTP_DELETE_METHOD = "delete";

    public static final String MD5_DEFAULT_SECRET = "EcBK0BUARh";

    public static final String DEFAULT_EMAIL_HTML = "common";

    public static final String DEFAULT_PASSWORD = "123456";

    public static final String ERROR_CODE_FIELD = "code";
    public static final String ERROR_MSG_FIELD = "msg";

    /** 服务500错误码 */
    public static final int SERVER_ERROR_STATUS_CODE = 500;

    /** 法定节假日url */
    public static final String HOLIDAY_URL= "http://timor.tech/api/holiday/year/%s?t=%s";

    public static final String SAVE_ERROR_MESSAGE = "保存失败";

    /** 端口 */
    public static final String MQTT_PORT_FIELD = "port";

    /** host地址 */
    public static final String MQTT_HOST_FIELD = "host";

    public static final String SLASH = "/";
}
