package cn.xianyum.extension.service.impl;

import cn.xianyum.common.entity.ai.OpenAiLogResponse;
import cn.xianyum.common.entity.ai.OpenAiModelResponse;
import cn.xianyum.common.entity.ai.OpenAiTokenUsageResponse;
import cn.xianyum.common.enums.SystemConstantKeyEnum;
import cn.xianyum.common.utils.SystemConstantUtils;
import cn.xianyum.common.utils.ai.OpenAiUtils;
import cn.xianyum.extension.service.OpenAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xianyum
 * @date 2026/5/8 21:03
 */
@Service
@Slf4j
public class OpenAiServiceImpl implements OpenAiService {

    /**
     * 获取模型列表
     *
     * @return
     */
    @Override
    public List<OpenAiModelResponse> getModels() {
        return OpenAiUtils.getModels();
    }

    /**
     * 获取令牌使用情况
     *
     * @return
     */
    @Override
    public OpenAiTokenUsageResponse getTokenUsage() {
        return OpenAiUtils.getTokenUsage();
    }

    /**
     * 获取令牌日志
     *
     * @return
     */
    @Override
    public List<OpenAiLogResponse> getTokenLog() {
        List<OpenAiLogResponse> logList = OpenAiUtils.getTokenLog();
        if (logList == null || logList.isEmpty()) {
            return new ArrayList<>();
        }
        return logList.stream().limit(20).toList();
    }

    /**
     * 获取当前模型
     *
     * @return
     */
    @Override
    public String getCurrentModel() {
        return OpenAiUtils.getCurrentModel();
    }

    /**
     * 更新当前系统模型
     *
     * @param modelName
     * @return
     */
    @Override
    public boolean updateCurrentModel(String modelName) {
        return SystemConstantUtils.saveSystemConstant(SystemConstantKeyEnum.OPEN_AI_MODEL,modelName,0);
    }
}
