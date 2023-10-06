package cn.xianyum.analysis.entity.request;

import cn.xianyum.common.entity.BaseRequest;
import lombok.Data;

/**
 * @author zhangwei
 * @date 2023/10/6 22:21
 */
@Data
public class HaoKaLotProductRequest extends BaseRequest {

    private String productName;

    private String operator;
}
