package cn.xianyum.analysis.entity.response;

import cn.xianyum.common.entity.base.BaseResponse;
import lombok.Data;

import java.util.Date;

/**
 * @author zhangwei
 * @date 2023/10/17 20:09
 */
@Data
public class XiaoDaoResponse extends BaseResponse {

    private String id;

    private String title;

    private String url;

    private String time;

    private Integer pushStatus;

    private Date pushTime;
}
