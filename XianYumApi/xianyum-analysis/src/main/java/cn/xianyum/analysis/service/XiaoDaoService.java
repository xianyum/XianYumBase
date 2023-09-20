package cn.xianyum.analysis.service;


import cn.xianyum.analysis.entity.po.XiaoDaoEntity;
import cn.xianyum.analysis.entity.request.XiaoDaoRequest;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * @author zhangwei
 * @date 2019/9/25 16:58
 */
public interface XiaoDaoService {

    void push();

    IPage<XiaoDaoEntity> getPage(XiaoDaoRequest request);
}
