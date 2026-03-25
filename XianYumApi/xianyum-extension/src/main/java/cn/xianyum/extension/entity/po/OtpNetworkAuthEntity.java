package cn.xianyum.extension.entity.po;

import cn.xianyum.common.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * OTP网络验证实体
 */
@Data
@TableName("otp_network_auth")
public class OtpNetworkAuthEntity extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.INPUT)
    private String id;

    /**
     * OTP认证URI
     */
    private String otpAuthUri;

}
