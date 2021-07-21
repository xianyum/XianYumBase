package cn.xianyum.common.validator;

import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.StringUtil;

/**
 * 数据校验
 * @author zhangwei
 * @date 2019/1/31 14:23
 */
public abstract class Assert {
    public static void isBlank(String str, String message) {
        if (StringUtil.isBlank(str)) {
            throw new SoException(message);
        }
    }

    public static void isNull(Object object, String message) {
        if (object == null) {
            throw new SoException(message);
        }
    }

}
