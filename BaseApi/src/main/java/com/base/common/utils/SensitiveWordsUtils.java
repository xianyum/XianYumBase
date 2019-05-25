package com.base.common.utils;

import java.util.HashMap;
import java.util.Map;

/***
 * 敏感词校验
 */
public class SensitiveWordsUtils {

    public static boolean checkWords(String words){
        Map map = new HashMap<>();
        map.put("c","你麻痹");
        String resp = HttpUtils.doPostForm("http://www.jinyongci.com/search.php",map);
        boolean span = resp.contains("<span");
        return span;
    }
}
