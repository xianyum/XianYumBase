package cn.xianyum.common.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author zhangwei
 * @date 2019/4/8 17:22
 */
public class MD5Utils {
    public static String stringToMD5(String plainText) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(
                    plainText.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有这个md5算法！");
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        String str= md5code.substring(0,md5code.length()-5);
        return str;
    }

    public static String getMd5(String plainText) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(
                    plainText.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有这个md5算法！");
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        md5code = md5code.substring(8,24).toUpperCase();
        return md5code;
    }

    public static String getPassword(String code,int count){
        String md5 =code;
        for(int i=0;i<count;i++){
            md5 = getMd5(md5);
        }
        return md5;
    }
    /**
     * 生成密码
     * @param args
     */
    public static void main(String[] args) {
        System.out.println(stringToMD5("admin"));
    }
}
