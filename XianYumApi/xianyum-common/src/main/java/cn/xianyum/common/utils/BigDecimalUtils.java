package cn.xianyum.common.utils;

import java.math.BigDecimal;

/**
 * @author zhangwei
 * @date 2025/2/15 23:11
 */
public class BigDecimalUtils {

    /**
     * 把字符串转为BigDecimal
     * @param str
     * @return
     */
    public static BigDecimal formatString(String str) {
        String regex = "^-?\\d*(\\.\\d+)?$";
        if(StringUtil.isBlank(str) || !str.matches(regex)){
            return null;
        }
        return new BigDecimal(str);
    }
}
