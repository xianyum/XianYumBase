package cn.xianyum.common.utils.validator;

import cn.xianyum.common.exception.SoException;
import cn.hutool.core.util.StrUtil;

/**
 * 数据校验
 * @author zhangwei
 * @date 2019/1/31 14:23
 */
public abstract class Assert {
    public static void isBlank(String str, String message) {
        if (StrUtil.isBlank(str)) {
            throw new SoException(message);
        }
    }

    public static void isNull(Object object, String message) {
        if (object == null) {
            throw new SoException(message);
        }
    }

}
