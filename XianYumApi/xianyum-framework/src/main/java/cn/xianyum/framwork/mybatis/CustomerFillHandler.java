package cn.xianyum.framwork.mybatis;

import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.Objects;

/**
 * @author zhangwei
 * @date 2023/10/13 20:54
 */
@Component
public class CustomerFillHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(Objects.isNull(loginUser)){
            this.setFieldValByName("create_by","-1",metaObject);
            this.setFieldValByName("create_by_name","system",metaObject);
        }else{
            this.setFieldValByName("create_by",loginUser.getId(),metaObject);
            this.setFieldValByName("create_by_name",loginUser.getUsername(),metaObject);
        }
        this.setFieldValByName("create_time",new Date(),metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(Objects.isNull(loginUser)){
            this.setFieldValByName("update_by","-1",metaObject);
            this.setFieldValByName("update_by_name","system",metaObject);
        }else{
            this.setFieldValByName("update_by",loginUser.getId(),metaObject);
            this.setFieldValByName("update_by_name",loginUser.getUsername(),metaObject);
        }
        this.setFieldValByName("update_time",new Date(),metaObject);
    }
}
