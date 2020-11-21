package com.base.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.common.exception.SoException;
import com.base.common.utils.AuthUserToken;
import com.base.common.utils.BeanUtils;
import com.base.common.utils.StringUtil;
import com.base.common.utils.UUIDUtils;
import com.base.dao.GiteeCommitMapper;
import com.base.dao.ProgramMapper;
import com.base.dao.SystemConstantMapper;
import com.base.entity.enums.PermissionEnum;
import com.base.entity.po.SystemConstantEntity;
import com.base.entity.po.program.GiteeCommitEntity;
import com.base.entity.po.program.ProgramEntity;
import com.base.entity.request.ProgramRequest;
import com.base.service.iservice.ProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zhangwei
 * @date 2020/11/20 20:29
 */
@Service
public class ProgramServiceImpl implements ProgramService {

    @Autowired
    private ProgramMapper programMapper;

    @Autowired
    private GiteeCommitMapper giteeCommitMapper;

    @Autowired
    private SystemConstantMapper systemConstantMapper;


    @Override
    public IPage<ProgramEntity> queryAll(ProgramRequest request) {

        Page<ProgramEntity> page = new Page<>(request.getPageNum(),request.getPageSize());
        QueryWrapper<ProgramEntity> queryWrapper = new QueryWrapper<ProgramEntity>()
                .eq(PermissionEnum.ADMIN.getStatus() != AuthUserToken.getUser().getPermission(),"create_user_id",AuthUserToken.getUser().getId())
                .like(StringUtil.isNotEmpty(request.getContactName()),"contact_name",request.getContactName())
                .like(StringUtil.isNotEmpty(request.getProgramTitle()),"program_title",request.getProgramTitle())
                .eq(null != request.getStatus(),"status",request.getStatus())
                .ge(StringUtil.isNotEmpty(request.getBeginTime()),"create_time",request.getBeginTime())
                .le(StringUtil.isNotEmpty(request.getEndTime()),"create_time",request.getEndTime())
                .orderByDesc("create_time");
        IPage<ProgramEntity> programEntityIPage = programMapper.selectPage(page, queryWrapper);
        return programEntityIPage;
    }

    @Override
    public ProgramEntity selectOneById(ProgramRequest request) {
        return programMapper.selectById(request.getId());
    }

    @Override
    public int save(ProgramRequest request) {
        ProgramEntity bean = BeanUtils.copy(request, ProgramEntity.class);
        String id = UUIDUtils.UUIDReplace();
        bean.setId(id);
        bean.setCreateTime(new Date());
        bean.setCreateUserId(AuthUserToken.getUser().getId().toString());
        bean.setCreateUserName(AuthUserToken.getUser().getUsername());
        bean.setStatus(1);

        GiteeCommitEntity giteeCommitEntity = new GiteeCommitEntity();
        giteeCommitEntity.setId(UUIDUtils.UUIDReplace());
        giteeCommitEntity.setProgramId(id);
        giteeCommitEntity.setCreateTime(new Date());
        giteeCommitEntity.setCommitMessage("感谢您的信任，["+bean.getProgramTitle()+"] 订单已经录入系统，等待管理员审核订单后，订单状态会进入开发期！");
        giteeCommitMapper.insert(giteeCommitEntity);
        return programMapper.insert(bean);
    }

    @Override
    public int update(ProgramRequest request) {
        ProgramEntity programEntity = programMapper.selectById(request.getId());
        if(0 == programEntity.getStatus()){
            throw new SoException("订单已完成不能再次修改！");
        }
        ProgramEntity bean = BeanUtils.copy(request, ProgramEntity.class);
        bean.setUpdateTime(new Date());
        bean.setUpdateUserId(AuthUserToken.getUser().getId());
        bean.setUpdateUserName(AuthUserToken.getUser().getUsername());
        return programMapper.updateById(bean);
    }

    @Override
    public void deleteById(String[] ids) {

        for(String id : ids){
            ProgramEntity programEntity = programMapper.selectById(id);
            if(0 == programEntity.getStatus()){
                throw new SoException("订单已完成，不能删除！");
            }
            programMapper.deleteById(id);
        }
    }

    @Override
    public void complete(ProgramRequest request) {
        ProgramEntity programEntity = programMapper.selectById(request.getId());
        if(0 == programEntity.getStatus()){
            throw new SoException("订单状态已经完成，不能再操作！");
        }
        if("develop".equals(request.getTag()) && 2 == programEntity.getStatus()){
            throw new SoException("订单状态已经在开发中，无需重复操作！");
        }
        if("success".equals(request.getTag()) && 1 == programEntity.getStatus()){
            throw new SoException("订单状态在审核中，不能操作完成！");
        }
        ProgramEntity bean = new ProgramEntity();
        bean.setId(request.getId());
        GiteeCommitEntity giteeCommitEntity = new GiteeCommitEntity();
        giteeCommitEntity.setId(UUIDUtils.UUIDReplace());
        giteeCommitEntity.setProgramId(request.getId());
        giteeCommitEntity.setCreateTime(new Date());
        if("success".equals(request.getTag())){
            bean.setStatus(0);
            bean.setCompletionTime(new Date());
            giteeCommitEntity.setCommitMessage("["+programEntity.getProgramTitle()+"] 订单已经完成，后期如果有什么事请及时联系。同时也希望您能给介绍同学/朋友过来哦，再次感谢您的信任~");
        }else if("develop".equals(request.getTag())){
            bean.setStatus(2);
            giteeCommitEntity.setCommitMessage("["+programEntity.getProgramTitle()+"] 订单已经审核完成，目前已进入开发期，期间您也可以查看当前开发进度，会实时更新！");
        }
        giteeCommitMapper.insert(giteeCommitEntity);
        programMapper.updateById(bean);
    }

    @Override
    public List<JSONObject> schedule(ProgramRequest request) {
        ProgramEntity programEntity = programMapper.selectById(request.getId());
        QueryWrapper<GiteeCommitEntity> queryWrapper =new QueryWrapper<GiteeCommitEntity>()
                        .eq("program_id",request.getId()).or()
                .eq(StringUtil.isNotEmpty(programEntity.getGiteeUrl()),"repository_url",programEntity.getGiteeUrl())
                .orderByDesc("create_time");
        List<GiteeCommitEntity> giteeCommitEntities = giteeCommitMapper.selectList(queryWrapper);
        List<JSONObject> list = new ArrayList<>();
        QueryWrapper<SystemConstantEntity> queryWrapper1
                = new QueryWrapper<SystemConstantEntity>()
                .eq("constant_key","program_schedule_url");
        SystemConstantEntity systemConstantEntity = systemConstantMapper.selectOne(queryWrapper1);
        if(giteeCommitEntities != null && giteeCommitEntities.size() >0){
            for(GiteeCommitEntity item : giteeCommitEntities){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("img",systemConstantEntity.getConstantValue());
                jsonObject.put("time",item.getCreateTime().getTime());
                jsonObject.put("content",item.getCommitMessage());
                list.add(jsonObject);
            }
        }
        return list;
    }

}
