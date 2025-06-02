package cn.xianyum.framwork.mybatis.handler;

import cn.xianyum.common.utils.StringUtil;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangwei
 * @date 2025/6/3 0:29
 */
@MappedTypes(List.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class StringToListTypeHandler extends AbstractJsonTypeHandler<List<String>> {

    @Override
    protected List<String> parse(String json) {
        if (StringUtil.isBlank(json)) {
            return Collections.emptyList();
        }
        return Arrays.stream(json.split(",")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
    }

    @Override
    protected String toJson(List<String> obj) {
        if (CollectionUtils.isEmpty(obj)) {
            return "";
        }
        return obj.stream().filter(s -> s != null && !s.isEmpty()).collect(Collectors.joining(","));
    }

    @Override
    public void setNonNullParameter(java.sql.PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws java.sql.SQLException {
        ps.setString(i, toJson(parameter));
    }

    @Override
    public List<String> getNullableResult(java.sql.ResultSet rs, String columnName) throws java.sql.SQLException {
        return parse(rs.getString(columnName));
    }

    @Override
    public List<String> getNullableResult(java.sql.ResultSet rs, int columnIndex) throws java.sql.SQLException {
        return parse(rs.getString(columnIndex));
    }

    @Override
    public List<String> getNullableResult(java.sql.CallableStatement cs, int columnIndex) throws java.sql.SQLException {
        return parse(cs.getString(columnIndex));
    }
}
