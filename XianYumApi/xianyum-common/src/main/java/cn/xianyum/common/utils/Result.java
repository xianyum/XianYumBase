package cn.xianyum.common.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.HashMap;


/**
 * @author zhangwei
 * @date 2019/1/31 14:10
 */
public class Result extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    public Result() {
        put("code", 200);
        put("msg", "success");
    }

    public static Result error() {
        return error(500, "系统异常");
    }

    public static Result error(String msg) {
        return error(500, msg);
    }

    public static Result error(int code, String msg) {
        Result result = new Result();
        result.put("code", code);
        result.put("msg", msg);
        return result;
    }

    public static Result success(String msg) {
        Result result = new Result();
        result.put("msg", msg);
        return result;
    }


    public static Result success(Object obj) {
        Result result = new Result();
        result.put("data",obj);
        return result;
    }

    public static Result success() {
        return new Result();
    }

    public static Result page(IPage<?> page) {
        Result result = new Result();
        result.put("data",page.getRecords());
        result.put("total",page.getTotal());
        return result;
    }

    public Result put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
