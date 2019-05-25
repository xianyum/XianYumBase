package com.base.entity.po;

import lombok.Data;

/**
 * 需要分页查询的，直接继承这个类
 */
@Data
public class PagePo {
    public Integer pageNum;
    public Integer pageSize;
}
