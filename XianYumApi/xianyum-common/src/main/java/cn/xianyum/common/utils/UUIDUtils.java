package cn.xianyum.common.utils;

import java.util.Random;
import java.util.UUID;

/**
 * @author zhangwei
 * @date 2019/3/12 17:27
 */
public class UUIDUtils {

    /**
     *  length：随机字符串+数字位数
     * @param length
     * @return
     */
    public static String getCodeChar(Integer length){
        char[] ss = new char[length];
        int i=0;
        while(i<length) {
            int f = (int) (Math.random()*3);
            if(f==0)
                ss[i] = (char) ('A'+Math.random()*26);
            else if(f==1)
                ss[i] = (char) ('a'+Math.random()*26);
            else
                ss[i] = (char) ('0'+Math.random()*10);
            i++;
        }
        String str=new String(ss);
        return str;
    }

    /**
     *  获取指定长度随机数字
     * @param length
     * @return
     */
    public static String getRandomNumber(Integer length){
        String code="";
        Random rand=new Random();//生成随机数
        for(int i=0;i<length;i++){
            code+=rand.nextInt(10);//生成6位验证码
        }
        return code;
    }
    /**
     *
     * @return
     */
    public static String getUUID(){
        return UUID.randomUUID().toString();
    }

    /**
     *
     * @return
     */
    public static String UUIDReplace(){
        return UUID.randomUUID().toString().replace("-", "");
    }

}
