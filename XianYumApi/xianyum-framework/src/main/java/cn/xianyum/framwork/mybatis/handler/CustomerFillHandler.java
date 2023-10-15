package cn.xianyum.framwork.mybatis.handler;

import cn.xianyum.common.entity.LoginUser;
import cn.xianyum.common.utils.SecurityUtils;
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
            this.strictInsertFill(metaObject,"createBy",String.class,"-1");
            this.strictInsertFill(metaObject,"createByName", String.class, "system");
        }else{
            this.strictInsertFill(metaObject,"createBy",String.class,loginUser.getId());
            this.strictInsertFill(metaObject,"createByName",String.class,loginUser.getUsername());
        }
        this.strictInsertFill(metaObject,"createTime",Date.class,DateTime.now().toDate());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if(Objects.isNull(loginUser)){
            this.strictUpdateFill(metaObject,"updateBy",String.class,"-1");
            this.strictUpdateFill(metaObject,"updateByName", String.class, "system");
        }else{
            this.strictUpdateFill(metaObject,"updateBy",String.class,loginUser.getId());
            this.strictUpdateFill(metaObject,"updateByName",String.class,loginUser.getUsername());
        }
        this.strictUpdateFill(metaObject,"updateTime",Date.class,DateTime.now().toDate());
    }
}
