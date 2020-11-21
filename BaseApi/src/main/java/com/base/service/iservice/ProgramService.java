package com.base.service.iservice;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.base.entity.po.program.ProgramEntity;
import com.base.entity.request.ProgramRequest;
import netscape.javascript.JSObject;

import java.util.List;

/**
 * @author zhangwei
 * @date 2020/11/20 20:29
 */
public interface ProgramService {
    IPage<ProgramEntity> queryAll(ProgramRequest request);

    ProgramEntity selectOneById(ProgramRequest request);

    int save(ProgramRequest request);

    int update(ProgramRequest request);

    void deleteById(String[] ids);

    void complete(ProgramRequest request);

    List<JSONObject> schedule(ProgramRequest request);
}
