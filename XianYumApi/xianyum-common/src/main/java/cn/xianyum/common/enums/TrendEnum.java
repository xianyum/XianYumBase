package cn.xianyum.common.enums;

import java.math.BigDecimal;

/**
 * @author xianyum
 * @date 2026/1/16 20:19
 */
public enum TrendEnum {
    /**
     * 上升
     */
    UP("up", "上升"),
    /**
     * 下降
     */
    DOWN("down", "下降"),
    /**
     * 平稳（包含首次加载、数值相等）
     */
    STABLE("stable", "平稳");

    // 对应前端返回的标识字符串
    private final String code;
    // 中文描述，便于日志/展示使用
    private final String desc;

    TrendEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 核心方法：根据当前值和上一个值（均为BigDecimal）判断数据趋势
     * 逻辑规则：
     * 1. last为0 → 平稳（首次加载场景）
     * 2. current > last → 上升
     * 3. current < last → 下降
     * 4. 其余情况（相等/空值）→ 平稳
     *
     * @param current 当前值（BigDecimal）
     * @param last    上一个值（BigDecimal）
     * @return 趋势枚举
     */
    public static TrendEnum judgeTrend(BigDecimal current, BigDecimal last) {
        // 空值防御：任意一个值为空，默认返回平稳
        if (current == null || last == null) {
            return STABLE;
        }

        // 首次加载（last=0）→ 平稳
        if (BigDecimal.ZERO.compareTo(last) == 0) {
            return STABLE;
        }

        // 比较当前值和上一个值的大小
        int compareResult = current.compareTo(last);
        if (compareResult > 0) {
            return UP;
        } else if (compareResult < 0) {
            return DOWN;
        } else {
            return STABLE;
        }
    }

    // 获取前端适配的编码（up/down/stable）
    public String getCode() {
        return code;
    }

    // 获取中文描述
    public String getDesc() {
        return desc;
    }
}
