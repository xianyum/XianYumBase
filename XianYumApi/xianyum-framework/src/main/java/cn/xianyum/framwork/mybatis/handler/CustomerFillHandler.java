package cn.xianyum.framwork.mybatis.handler;

import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.utils.SecurityUtils;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.Objects;

/**
 * @author zhangwei
 * @date 2023/10/13 20:54
 */
@Component
@Slf4j
public class CustomerFillHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(Objects.isNull(loginUser)){
            this.setFieldValByName("createBy","-1",metaObject);
            this.setFieldValByName("createByName", "system",metaObject);
            this.setFieldValByName("updateBy","-1",metaObject);
            this.setFieldValByName("updateByName", "system",metaObject);
        }else{
            this.setFieldValByName("createBy",loginUser.getId(),metaObject);
            this.setFieldValByName("createByName",loginUser.getUsername(),metaObject);
            this.setFieldValByName("updateBy",loginUser.getId(),metaObject);
            this.setFieldValByName("updateByName",loginUser.getUsername(),metaObject);
        }
        this.setFieldValByName("createTime",DateTime.now().toDate(),metaObject);
        this.setFieldValByName("updateTime",DateTime.now().toDate(),metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(Objects.isNull(loginUser)){
            this.setFieldValByName("updateBy","-1",metaObject);
            this.setFieldValByName("updateByName", "system",metaObject);
        }else{
            this.setFieldValByName("updateBy",loginUser.getId(),metaObject);
            this.setFieldValByName("updateByName",loginUser.getUsername(),metaObject);
        }
        this.setFieldValByName("updateTime",DateTime.now().toDate(),metaObject);
    }
}
