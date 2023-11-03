package cn.xianyum.common.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * @Description 数据权限范围枚举
 * @Author ZhangWei
 * @Date 2023/11/3 16:25
 */
public enum DataScopeEnum {
    ALL("1","全部数据权限"),
    SELF("2","仅本人数据权限"),
    VISITOR("3","游客数据权限");

    private String dataScope;
    private String desc;

    DataScopeEnum(String dataScope, String desc) {
        this.dataScope = dataScope;
        this.desc = desc;
    }

    public String getDataScope() {
        return dataScope;
    }

    public static DataScopeEnum getDataScope(String dataScope) {
        Optional<DataScopeEnum> first = Arrays.stream(DataScopeEnum.values())
                .filter(e -> e.getDataScope().equals(dataScope))
                .findFirst();
        return first.isPresent()?first.get():null;

    }
}
