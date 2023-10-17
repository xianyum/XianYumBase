package cn.xianyum.system.service;

import cn.xianyum.common.entity.base.PageResponse;
import cn.xianyum.system.entity.po.ProgramEntity;
import cn.xianyum.system.entity.request.ProgramRequest;
import cn.xianyum.system.entity.response.ProgramResponse;
import com.alibaba.fastjson.JSONObject;
import java.util.List;


/**
 * @author zhangwei
 * @date 2020/11/20 20:29
 */
public interface ProgramService {
    PageResponse<ProgramResponse> getPage(ProgramRequest request);

    ProgramEntity selectOneById(ProgramRequest request);

    int save(ProgramRequest request);

    int update(ProgramRequest request);

    void deleteById(String[] ids);

    void complete(ProgramRequest request);

    List<JSONObject> schedule(ProgramRequest request);
}
