package cn.xianyum.framwork.mybatis.interceptor;

import cn.xianyum.framwork.mybatis.handler.CustomerDataPermissionHandler;
import com.baomidou.mybatisplus.extension.plugins.handler.MultiDataPermissionHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.PlainSelect;

import java.util.Objects;

/**
 * @Description 数据权限拦截器
 * @Author ZhangWei
 * @Date 2023/11/1 17:12
 */
@Slf4j
public class MybatisPlusPermissionInterceptor extends DataPermissionInterceptor {

    public MybatisPlusPermissionInterceptor(CustomerDataPermissionHandler dataPermissionHandler) {
        super(dataPermissionHandler);
    }


    @Override
    protected void setWhere(PlainSelect plainSelect, String whereSegment) {
        if (this.getDataPermissionHandler() instanceof MultiDataPermissionHandler) {
            super.processPlainSelect(plainSelect, whereSegment);
            return;
        }
        if(this.getDataPermissionHandler() instanceof CustomerDataPermissionHandler) {
            CustomerDataPermissionHandler handler = (CustomerDataPermissionHandler)this.getDataPermissionHandler();
            Expression sqlSegment = handler.getSqlSegmentWithPermission(plainSelect, whereSegment);
            if (Objects.nonNull(sqlSegment)) {
                plainSelect.setWhere(sqlSegment);
            }
        }
    }

}
