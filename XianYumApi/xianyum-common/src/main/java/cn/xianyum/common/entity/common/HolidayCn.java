package cn.xianyum.common.entity.common;

import lombok.Data;

/**
 * 中国法定节假日
 * @author zhangwei
 * @date 2025/3/14 21:16
 */
@Data
public class HolidayCn {

    /** 节假日名称 */
    private String name;

    /** 日期yyyy-MM-dd */
    private String date;

    /** 是否班，true班，false不是班 */
    private boolean isOffDay;
}
