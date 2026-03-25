package cn.xianyum.extension.service;

import cn.xianyum.extension.entity.request.OtpNetworkAuthRequest;
import cn.xianyum.extension.entity.response.OtpNetworkAuthResponse;
import java.util.List;

/**
 * @author xianyum
 * @date 2026/3/25 21:16
 */
public interface OtpNetworkAuthService{

    /**
     * 保存OTP网络验证
     * @param request 请求参数
     * @return 是否成功
     */
    boolean save(OtpNetworkAuthRequest request);

    /**
     * 获取OTP网络验证列表
     * @return OTP网络验证列表
     */
    List<OtpNetworkAuthResponse> getList();

    /**
     * 根据主键id删除OTP网络验证
     * @param id 主键id
     * @return 是否成功
     */
    boolean deleteById(String id);
}
