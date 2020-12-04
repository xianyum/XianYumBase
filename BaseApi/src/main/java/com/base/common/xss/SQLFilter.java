package com.base.common.xss;

import com.base.common.exception.SoException;
import com.base.common.utils.StringUtil;


/**
 * @author zhangwei
 * @date 2019/1/31 14:15
 */
public class SQLFilter {

    /**
     * SQL注入过滤
     * @param str  待验证的字符串
     */
    public static String sqlInject(String str){
        if(StringUtil.isBlank(str)){
            return null;
        }
        //去掉'|"|;|\字符
        str = StringUtil.replace(str, "'", "");
        str = StringUtil.replace(str, "\"", "");
        str = StringUtil.replace(str, ";", "");
        str = StringUtil.replace(str, "\\", "");

        //转换成小写
        str = str.toLowerCase();

        //非法字符
        String[] keywords = {"master", "truncate", "insert", "select", "delete", "update", "declare", "alter", "drop"};

        //判断是否包含非法字符
        for(String keyword : keywords){
            if(str.indexOf(keyword) != -1){
                throw new SoException("包含非法字符");
            }
        }

        return str;
    }
}
