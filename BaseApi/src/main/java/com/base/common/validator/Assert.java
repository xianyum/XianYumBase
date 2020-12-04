package com.base.common.validator;

import com.base.common.exception.SoException;
import com.base.common.utils.StringUtil;

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
