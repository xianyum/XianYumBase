package cn.xianyum.common.utils;

import cn.xianyum.common.entity.base.PageResponse;
import org.springframework.http.HttpStatus;
import java.util.HashMap;


/**
 * @author zhangwei
 * @date 2019/1/31 14:10
 */
public class Results extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;
    private static final String DATA_FILED = "data";
    private static final String TOTAL_FILED = "total";
    private static final String MSG_FILED = "msg";
    private static final String CODE_FILED = "code";

    private static final String SUCCESS_FILED = "success";

    private static final String ERROR_EXCEPTION_FILED = "系统异常";

    public Results() {
        put(CODE_FILED, HttpStatus.OK.value());
        put(MSG_FILED, SUCCESS_FILED);
    }

    public static Results error() {
        return error(HttpStatus.INTERNAL_SERVER_ERROR.value(), ERROR_EXCEPTION_FILED);
    }

    public static Results error(String msg) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
    }

    public static Results error(int code, String msg) {
        Results result = new Results();
        result.put(CODE_FILED, code);
        result.put(MSG_FILED, msg);
        return result;
    }

    public static Results success(String msg) {
        Results result = new Results();
        result.put(MSG_FILED, msg);
        return result;
    }


    public static Results success(Object obj) {
        Results result = new Results();
        result.put(DATA_FILED,obj);
        return result;
    }

    public static Results success() {
        return new Results();
    }

    public static Results page(PageResponse page) {
        Results result = new Results();
        result.put(DATA_FILED,page.getDataList());
        result.put(TOTAL_FILED,page.getTotal());
        return result;
    }

    public Results put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
