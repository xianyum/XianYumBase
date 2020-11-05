package com.base.entity.enums;

/**
 * 权限标识
 * @author zhangwei
 * @date 2019/6/19 22:17
 * @email 80616059@qq.com
 */
public enum PermissionEnum {

    ADMIN(0,"admin"),
    COMMON(1,"common");

    private Integer status;
    private String name;

    PermissionEnum(Integer status, String name) {
        this.status = status;
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 根据权限状态获取权限标识符
     * @param status
     * @return
     */
    public static String getNameByStatus(Integer status){
        for(PermissionEnum item : PermissionEnum.values()){
            if(status == item.getStatus()){
                return item.getName();
            }
        }
        return  null;
    }
}
