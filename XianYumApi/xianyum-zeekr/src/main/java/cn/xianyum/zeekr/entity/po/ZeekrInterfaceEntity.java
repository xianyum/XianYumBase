package cn.xianyum.zeekr.entity.po;

import lombok.Data;

/**
 * @author zhangwei
 * @date 2022/8/7 18:22
 */
@Data
public class ZeekrInterfaceEntity {

    private String clientId;
    private String clientSecret;
    private String tokenUrl;
    private String queryZeekrUrl;
    private String executeZeekrUrl;
    private String employeeUrl;
    private String queryZeekrProjectUrl;
    private Long pageSize;
}
