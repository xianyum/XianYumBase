package cn.xianyum.proxy.dao;

import cn.xianyum.proxy.entity.po.ProxyDetailsEntity;
import cn.xianyum.proxy.entity.request.ProxyDetailsRequest;
import cn.xianyum.proxy.entity.response.ProxyDetailsResponse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ProxyDetailsMapper extends BaseMapper<ProxyDetailsEntity> {

    List<ProxyDetailsResponse> getPage(@Param("request") ProxyDetailsRequest request, Page<ProxyDetailsEntity> page);

    void flushBytes(@Param("id")  String id,@Param("nowWroteBytes") long nowWroteBytes,@Param("nowReadBytes")  long nowReadBytes);
}
