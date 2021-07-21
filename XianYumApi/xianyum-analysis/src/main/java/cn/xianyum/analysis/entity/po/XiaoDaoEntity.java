package cn.xianyum.analysis.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 爬取www.xiaodao.com每日更新内容
 * @author zhangwei
 * @date 2019/9/25 15:02
 */
@Data
@TableName(value = "xiao_dao")
public class XiaoDaoEntity {

    @TableId(type = IdType.INPUT)
    private String id;

    private String title;

    private String url;

    private String time;

    private Integer pushStatus;

    private Date pushTime;

    private Date createTime;

    private String pushId;
}
