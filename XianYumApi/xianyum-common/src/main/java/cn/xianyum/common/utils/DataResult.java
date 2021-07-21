package cn.xianyum.common.utils;

import java.util.HashMap;


/**
 * @author zhangwei
 * @date 2019/1/31 14:10
 */
public class DataResult extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    public DataResult() {
        put("code", 200);
        put("msg", "success");
    }

    public static DataResult error() {
        return error(500, "系统异常");
    }

    public static DataResult error(String msg) {
        return error(500, msg);
    }

    public static DataResult error(int code, String msg) {
        DataResult result = new DataResult();
        result.put("code", code);
        result.put("msg", msg);
        return result;
    }

    public static DataResult success(String msg) {
        DataResult result = new DataResult();
        result.put("msg", msg);
        return result;
    }


    public static DataResult success(Object obj) {
        DataResult result = new DataResult();
        result.put("data",obj);
        return result;
    }

    public static DataResult success() {
        return new DataResult();
    }

    public DataResult put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
