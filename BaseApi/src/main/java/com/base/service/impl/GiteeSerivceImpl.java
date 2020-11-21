package com.base.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.base.common.exception.SoException;
import com.base.common.utils.UUIDUtils;
import com.base.dao.GiteeCommitMapper;
import com.base.entity.po.program.GiteeCommitEntity;
import com.base.service.iservice.GiteeSerivce;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author zhangwei
 * @date 2020/11/21 16:24
 */
@Slf4j
@Service
public class GiteeSerivceImpl implements GiteeSerivce {

    private final static String SECRET = "xianyu";

    @Autowired
    private GiteeCommitMapper giteeCommitMapper;

    @Override
    public void push(String json) {

        JSONObject requestObj = JSONObject.parseObject(json);
        if(!SECRET.equals(requestObj.getString("password"))){
            throw new SoException("密钥不正确！");
        }
        String url = JSONObject.parseObject(requestObj.getString("repository")).getString("url");
        String commitMessage = JSONObject.parseObject(requestObj.getString("head_commit")).getString("message");
        String repositoryName = JSONObject.parseObject(requestObj.getString("repository")).getString("name");

        GiteeCommitEntity giteeCommitEntity = new GiteeCommitEntity();
        giteeCommitEntity.setId(UUIDUtils.UUIDReplace());
        giteeCommitEntity.setCreateTime(new Date());
        giteeCommitEntity.setRepositoryName(repositoryName);
        giteeCommitEntity.setCommitMessage(commitMessage);
        giteeCommitEntity.setRepositoryUrl(url);
        giteeCommitMapper.insert(giteeCommitEntity);

    }
}
