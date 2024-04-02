package cn.xianyum.analysis.entity.po;

import cn.xianyum.common.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 爬取www.xiaodao.com每日更新内容
 * @author zhangwei
 * @date 2019/9/25 15:02
 */
@Data
@TableName(value = "xiao_dao")
public class XiaoDaoEntity extends BaseEntity {

    @TableId(type = IdType.INPUT)
    private String id;

    private String title;

    private String url;
}
