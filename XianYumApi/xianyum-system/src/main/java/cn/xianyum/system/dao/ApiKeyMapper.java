package cn.xianyum.system.dao;

import cn.xianyum.system.entity.po.ApiKeyEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * API Key Mapper
 */
public interface ApiKeyMapper extends BaseMapper<ApiKeyEntity> {

    /**
     * 累加 API Key 累计请求次数
     *
     * @param apiKey API Key 值
     * @param count  需要累加的次数
     * @return 影响行数
     */
    int increaseTotalCount(@Param("apiKey") String apiKey, @Param("count") long count);
}
