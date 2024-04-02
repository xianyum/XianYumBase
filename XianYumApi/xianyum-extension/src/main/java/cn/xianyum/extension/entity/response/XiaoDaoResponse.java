package cn.xianyum.extension.entity.response;

import cn.xianyum.common.entity.base.BaseResponse;
import lombok.Data;

/**
 * @author zhangwei
 * @date 2023/10/17 20:09
 */
@Data
public class XiaoDaoResponse extends BaseResponse {

    private String id;

    private String title;

    private String url;
}
