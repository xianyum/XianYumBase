package cn.xianyum.common.entity;

import cn.xianyum.common.enums.DataScopeEnum;
import lombok.Data;

import java.util.Set;

@Data
public class UserRoleCache {

    private Set<String> roleCodes;

    private Set<String> roleNames;

    private DataScopeEnum dataScopeEnum;
}
