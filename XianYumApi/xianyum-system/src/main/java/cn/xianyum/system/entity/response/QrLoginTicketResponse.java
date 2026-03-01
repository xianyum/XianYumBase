package cn.xianyum.system.entity.response;

import cn.xianyum.common.enums.QrCodeLoginStatus;
import lombok.Data;

/**
 * @author xianyum
 * @date 2026/2/28 20:16
 */
@Data
public class QrLoginTicketResponse {

    // 唯一凭证（二维码ID）
    private String ticket;

    // 状态：0-未扫码 1-已扫码 2-已确认 3-已过期
    private QrCodeLoginStatus loginStatus;

    // 绑定的用户ID（扫码后赋值）
    private String userName;

}
