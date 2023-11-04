package cn.xianyum.common.utils;

import cn.xianyum.common.exception.SoException;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author zhangwei
 * @date 2019/4/8 17:22
 */
public class MD5Utils {

    public static String getMd5(String content) {
        byte[] secretBytes;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(
                    content.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有这个md5算法！");
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        md5code = md5code.substring(8, 24).toUpperCase();
        return md5code;
    }


    public static String getMd5(String content,String secret) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] md5 = md.digest(content.concat(secret).getBytes(StandardCharsets.UTF_8));
            // 将处理后的字节转成 16 进制，得到最终 32 个字符
            StringBuilder sb = new StringBuilder();
            for (byte b : md5) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        }catch (Exception e){
            throw new SoException("sign md5 error.");
        }
    }
}
