package cn.xianyum.system.service.impl;

import cn.xianyum.common.enums.PermissionEnum;
import cn.xianyum.common.utils.BeanUtils;
import cn.xianyum.common.utils.SecurityUtils;
import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.system.dao.PushLogMapper;
import cn.xianyum.system.entity.po.PushLogEntity;
import cn.xianyum.system.entity.request.PushLogRequest;
import cn.xianyum.system.entity.response.PushLogResponse;
import cn.xianyum.system.service.PushLogService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhangwei
 * @date 2021/7/10 19:36
 */
@Service
@Slf4j
public class PushLogServiceImpl implements PushLogService {

    @Autowired
    private PushLogMapper pushLogMapper;

    @Override
    public int insert(PushLogEntity pushLogEntity) {
        return pushLogMapper.insert(pushLogEntity);
    }

    @Override
    public IPage<PushLogResponse> getPage(PushLogRequest request) {

        IPage<PushLogResponse> responseIPage = new Page<>();

        if(SecurityUtils.getLoginUser().getPermission() != PermissionEnum.ADMIN.getStatus()){
            return responseIPage;
        }

        Page<PushLogEntity> page = new Page<>(request.getPageNum(),request.getPageSize());
        QueryWrapper<PushLogEntity> queryWrapper = new QueryWrapper<PushLogEntity>()
                .eq(StringUtil.isNotEmpty(request.getId()),"id",request.getId())
                .like(StringUtil.isNotEmpty(request.getTitle()),"title",request.getTitle())
                .like(StringUtil.isNotEmpty(request.getContent()),"content",request.getContent())
                .gt(null != request.getStartTime(),"create_time",request.getStartTime())
                .lt(null != request.getEndTime(),"create_time",request.getEndTime())
                .orderByDesc("create_time");
        IPage<PushLogEntity> pageResult = pushLogMapper.selectPage(page,queryWrapper);
        responseIPage.setTotal(pageResult.getTotal());
        responseIPage.setRecords(BeanUtils.copyList(pageResult.getRecords(),PushLogResponse.class));
        return responseIPage;
    }
}
