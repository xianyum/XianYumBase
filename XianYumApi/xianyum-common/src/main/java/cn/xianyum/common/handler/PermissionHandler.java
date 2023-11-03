package cn.xianyum.common.handler;

import org.springframework.stereotype.Service;

/**
 * @Description 权限实现类
 * @Author ZhangWei
 * @Date 2023/11/3 9:12
 */
@Service("ph")
public class PermissionHandler {

    public boolean hasPerm(String permission){
        return true;
    }
}
