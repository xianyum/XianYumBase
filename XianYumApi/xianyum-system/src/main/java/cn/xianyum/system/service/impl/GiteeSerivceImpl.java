package cn.xianyum.system.service.impl;

import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.UUIDUtils;
import cn.xianyum.system.dao.GiteeCommitMapper;
import cn.xianyum.system.entity.po.GiteeCommitEntity;
import cn.xianyum.system.service.GiteeSerivce;
import com.alibaba.fastjson2.JSONObject;
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
        giteeCommitEntity.setRepositoryName(repositoryName);
        giteeCommitEntity.setCommitMessage(commitMessage);
        giteeCommitEntity.setRepositoryUrl(url);
        giteeCommitMapper.insert(giteeCommitEntity);

    }
}
