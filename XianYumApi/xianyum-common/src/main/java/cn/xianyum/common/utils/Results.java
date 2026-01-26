package cn.xianyum.common.utils;

import cn.xianyum.common.entity.base.BaseResponse;
import cn.xianyum.common.entity.base.PageResponse;
import org.springframework.http.HttpStatus;
import java.util.HashMap;


/**
 * 泛型版本的返回结果封装类
 * @param <T> data字段的类型
 */
public class Results<T> extends HashMap<String, Object> {

    private static final long serialVersionUID = 1L;
    private static final String DATA_FIELD = "data";
    private static final String TOTAL_FIELD = "total";
    private static final String OTHER_INFO_FIELD = "otherInfo";
    private static final String MSG_FIELD = "msg";
    private static final String CODE_FIELD = "code";
    private static final String SUCCESS_FIELD = "success";
    private static final String ERROR_EXCEPTION_FIELD = "系统异常";

    /**
     * 默认构造器：初始化成功状态
     */
    public Results() {
        put(CODE_FIELD, HttpStatus.OK.value());
        put(MSG_FIELD, SUCCESS_FIELD);
    }

    // -------------------- 错误返回方法 --------------------
    public static <T> Results<T> error() {
        return error(HttpStatus.INTERNAL_SERVER_ERROR.value(), ERROR_EXCEPTION_FIELD);
    }

    public static <T> Results<T> error(String msg) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
    }

    public static <T> Results<T> error(int code, String msg) {
        Results<T> result = new Results<>();
        result.put(CODE_FIELD, code);
        result.put(MSG_FIELD, msg);
        return result;
    }

    // -------------------- 成功返回方法 --------------------
    public static <T> Results<T> success(String msg) {
        Results<T> result = new Results<>();
        result.put(MSG_FIELD, msg);
        return result;
    }

    /**
     * 带泛型数据的成功返回
     * @param data 泛型类型的业务数据
     * @return 包含指定类型数据的结果对象
     */
    public static <T> Results<T> success(T data) {
        Results<T> result = new Results<>();
        result.put(DATA_FIELD, data);
        return result;
    }

    public static <T> Results<T> success() {
        return new Results<>();
    }

    // -------------------- 分页返回方法 --------------------
    /**
     * 分页结果返回（泛型约束PageResponse，同时指定data字段类型为分页数据列表的类型）
     * @param page 分页响应对象
     * @param <T> 分页数据列表的元素类型
     * @return 包含分页数据的结果对象
     */
    public static <T extends BaseResponse> Results<T> page(PageResponse<T> page) {
        Results<T> result = new Results<>();
        result.put(DATA_FIELD, page.getDataList());
        result.put(TOTAL_FIELD, page.getTotal());
        result.put(OTHER_INFO_FIELD, page.getOtherInfo());
        return result;
    }

    /**
     * 重写put方法，支持链式调用
     */
    @Override
    public Results<T> put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    // -------------------- 便捷获取方法（新增，提升易用性） --------------------
    /**
     * 类型安全地获取data字段数据
     * @return 泛型类型的data数据
     */
    @SuppressWarnings("unchecked")
    public T getData() {
        return (T) this.get(DATA_FIELD);
    }

    /**
     * 类型安全地设置data字段数据
     * @param data 泛型类型的业务数据
     * @return 当前对象，支持链式调用
     */
    public Results<T> setData(T data) {
        this.put(DATA_FIELD, data);
        return this;
    }

    // 可选：补充其他字段的便捷获取/设置方法
    public Integer getCode() {
        return (Integer) this.get(CODE_FIELD);
    }

    public String getMsg() {
        return (String) this.get(MSG_FIELD);
    }
}
