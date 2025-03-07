package cn.xianyum.extension.dao;

import cn.xianyum.extension.entity.request.EvDriveRecordsRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.xianyum.extension.entity.po.EvDriveRecordsEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 新能源车行驶记录(EvDriveRecords)表数据库访问层
 *
 * @author makejava
 * @since 2025-03-06 20:43:41
 */
public interface EvDriveRecordsMapper extends BaseMapper<EvDriveRecordsEntity> {

    Map<String, Object> selectSummaryData(@Param("request") EvDriveRecordsRequest request);
}

