package cn.xianyum.framwork.mybatis.handler;

import cn.xianyum.common.constant.Constants;
import cn.xianyum.common.entity.base.BaseEntity;
import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.handler.PermissionThreadLocal;
import cn.xianyum.common.utils.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.HexValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import java.util.Objects;


/**
 * @Description
 * @Author ZhangWei
 * @Date 2023/11/1 17:18
 * @Email wei.zhang@raipiot.com
 */
@Slf4j
public class RoleDataPermissionHandler implements CustomerDataPermissionHandler{

    @Override
    public Expression getSqlSegmentWithPermission(PlainSelect plainSelect, String whereStatement) {
        if(!PermissionThreadLocal.get()){
            return null;
        }
        try {
            Expression where = plainSelect.getWhere();
            if (where == null) {
                where = new HexValue(Constants.SQL_EQUALS_DEFAULT);
            }
            FromItem fromItem = plainSelect.getFromItem();
            Alias fromItemAlias = fromItem.getAlias();

            EqualsTo selfEqualsTo = new EqualsTo();
            StringBuffer leftColumn = new StringBuffer();
            if(Objects.nonNull(fromItemAlias) && StringUtil.isNotEmpty(fromItemAlias.getName())){
                leftColumn.append(fromItemAlias.getName());
                leftColumn.append(StringPool.DOT);
            }
            leftColumn.append(BaseEntity.CREATE_BY_COLUMN);
            selfEqualsTo.setLeftExpression(new Column(leftColumn.toString()));
            selfEqualsTo.setRightExpression(new StringValue("1"));
            return new AndExpression(where, selfEqualsTo);
        } catch (Exception e) {
            log.error("数据权限插件解析异常. ",e);
            throw new SoException(e.getMessage());
        }finally {
            PermissionThreadLocal.remove();
        }
    }
}
