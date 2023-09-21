package cn.xianyum.common.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangwei
 * @date 2019/5/24 16:02
 */
@Data
public class BaseRequest {

    public Integer pageNum;
    public Integer pageSize;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> params = new HashMap<>();
}
