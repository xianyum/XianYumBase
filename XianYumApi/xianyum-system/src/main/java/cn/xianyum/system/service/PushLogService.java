package cn.xianyum.system.service;

import cn.xianyum.system.entity.po.PushLogEntity;
import cn.xianyum.system.entity.request.PushLogRequest;
import cn.xianyum.system.entity.response.PushLogResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * @author zhangwei
 * @date 2021/7/10 19:36
 */
public interface PushLogService {

    int insert(PushLogEntity pushLogEntity);

    IPage<PushLogResponse> getPage(PushLogRequest request);
}
