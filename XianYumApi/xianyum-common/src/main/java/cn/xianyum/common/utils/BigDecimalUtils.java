package cn.xianyum.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;

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

    /**
     * v1 + v2
     *
     * @param v1
     * @param v2
     * @return
     */
    public static BigDecimal add(BigDecimal v1, BigDecimal v2) {
        return getValue(v1).add(getValue(v2));
    }

    /**
     * v1 - v2
     *
     * @param v1
     * @param v2
     * @return
     */
    public static BigDecimal subtract(BigDecimal v1, BigDecimal v2) {
        return getValue(v1).subtract(getValue(v2));
    }

    /**
     * v1 / v2
     *
     * @param v1
     * @param v2
     * @param roundingMode
     * @return
     */
    public static BigDecimal divide(BigDecimal v1, BigDecimal v2, int scale, int roundingMode) {
        if (Objects.compare(getValue(v2), BigDecimal.ZERO, BigDecimal::compareTo) == 0) {
            return null;
        }
        return getValue(v1).divide(getValue(v2), scale, roundingMode);
    }

    /**
     * v1 / v2 保留2位小数
     * @param v1
     * @param v2
     * @return
     */
    public static BigDecimal divide(BigDecimal v1, BigDecimal v2) {
        if (Objects.compare(getValue(v2), BigDecimal.ZERO, BigDecimal::compareTo) == 0) {
            return null;
        }
        return getValue(v1).divide(getValue(v2), 2, RoundingMode.HALF_UP);
    }

    /**
     * v1 * v2
     *
     * @param v1
     * @param v2
     * @return
     */
    public static BigDecimal multiply(BigDecimal v1, BigDecimal v2) {
        return getValue(v1).multiply(getValue(v2));
    }

    /**
     * v1 < v2
     *
     * @param v1
     * @param v2
     * @return
     */
    public static boolean lt(BigDecimal v1, BigDecimal v2) {
        return getValue(v1).compareTo(getValue(v2)) == -1;
    }

    /**
     * v1 = v2
     *
     * @param v1
     * @param v2
     * @return
     */
    public static boolean eq(BigDecimal v1, BigDecimal v2) {
        return getValue(v1).compareTo(getValue(v2)) == 0;
    }

    /**
     * v1 > v2
     *
     * @param v1
     * @param v2
     * @return
     */
    public static boolean gt(BigDecimal v1, BigDecimal v2) {
        return getValue(v1).compareTo(getValue(v2)) == 1;
    }

    /**
     * v1 <= v2
     *
     * @param v1
     * @param v2
     * @return
     */
    public static boolean le(BigDecimal v1, BigDecimal v2) {
        return getValue(v1).compareTo(getValue(v2)) <= 0;
    }

    /**
     * v1 >= v2
     *
     * @param v1
     * @param v2
     * @return
     */
    public static boolean ge(BigDecimal v1, BigDecimal v2) {
        return getValue(v1).compareTo(getValue(v2)) >= 0;
    }

    private static BigDecimal getValue(BigDecimal value) {
        return Optional.ofNullable(value).orElse(BigDecimal.ZERO);
    }

    /**
     * 是否是零
     * @param val
     * @return true：是零，false：非零
     */
    public static boolean isZero(BigDecimal val) {
        if (Objects.nonNull(val)) {
            return Objects.compare(BigDecimal.ZERO, val, BigDecimal::compareTo) == 0;
        }
        return false;
    }

}
