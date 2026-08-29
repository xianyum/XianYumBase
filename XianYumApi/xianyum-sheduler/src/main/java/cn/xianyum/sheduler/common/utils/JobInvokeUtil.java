package cn.xianyum.sheduler.common.utils;

import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.handler.IJobHandler;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import java.util.Objects;


/**
 * @author zhangwei
 * @description
 * @date 2022/5/13 11:44
 */
@Slf4j
public class JobInvokeUtil {

    public static IJobHandler invokeJobHandler(String jobHandler) throws Exception {
        if(StrUtil.isEmpty(jobHandler)){
            throw new SoException("jobHandler is null.");
        }
        Object iJobHandler = SpringUtil.getBean(jobHandler);
        if(Objects.isNull(iJobHandler)){
            throw new SoException("jobHandler invoke is null.");
        }
        if(!(iJobHandler instanceof IJobHandler)){
            throw new SoException("jobHandler not implements IJobHandler interface methods.");
        }
        return (IJobHandler)iJobHandler;
    }
}
