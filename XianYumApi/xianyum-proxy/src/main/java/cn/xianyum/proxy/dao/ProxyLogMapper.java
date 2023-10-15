package cn.xianyum.proxy.dao;

import cn.xianyum.proxy.entity.po.ProxyLogEntity;
import cn.xianyum.proxy.entity.request.ProxyLogRequest;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProxyLogMapper extends BaseMapper<ProxyLogEntity> {

    IPage<ProxyLogEntity> getPage(@Param("request") ProxyLogRequest request, Page<ProxyLogEntity> page);
}
