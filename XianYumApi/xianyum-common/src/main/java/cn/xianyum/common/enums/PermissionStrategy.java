package cn.xianyum.common.enums;

/**
 * @author wei.zhang@onecontract-cloud.com
 * @description 权限策略
 * @date 2023/5/8 16:44
 */
public enum PermissionStrategy {

    // 允许所有权限通行
    ALLOW_ALL("allow_all"),
    // 仅允许最高权限访问
    ALLOW_ADMIN("allow_admin");


    private String strategy;

    PermissionStrategy(String strategy) {
        this.strategy = strategy;
    }

    public String getStrategy() {
        return strategy;
    }
}


