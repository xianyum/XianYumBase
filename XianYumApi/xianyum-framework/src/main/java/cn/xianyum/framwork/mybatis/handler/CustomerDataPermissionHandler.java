package cn.xianyum.framwork.mybatis.handler;

import com.baomidou.mybatisplus.extension.plugins.handler.DataPermissionHandler;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.PlainSelect;


/**
 * @Description permissionHandler
 * @Author ZhangWei
 * @Date 2023/11/1 17:09
 */
public interface CustomerDataPermissionHandler extends DataPermissionHandler {

    @Deprecated
    default Expression getSqlSegment(Expression where, String whereStatement) {
        throw new UnsupportedOperationException();
    }

    Expression getSqlSegmentWithPermission(PlainSelect plainSelect, String whereStatement);
}
