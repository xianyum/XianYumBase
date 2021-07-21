package cn.xianyum.common.utils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * @author zhangwei
 * @date 2019/11/1 13:21
 */
@Slf4j
public class DesUtils {

    private static Key key;
    // 设置密钥key
    private static String KEY_STR = "www.xianyum.cn";
    private static String CHARSETNAME = "UTF-8";
    private static String ALGORITHM = "DES";

    static {
        try {
            // 生成DES算法对象
            KeyGenerator generator = KeyGenerator.getInstance(ALGORITHM);
            // 运用SHA1安全策略
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            // 设置上密钥种子
            secureRandom.setSeed(KEY_STR.getBytes());
            // 初始化基于SHA1的算法对象
            generator.init(secureRandom);
            // 生成密钥对象
            key = generator.generateKey();
            generator = null;
        } catch (Exception e) {
            log.error("DESUtil 类加载 初始化加密对象异常");
            throw new RuntimeException(e);
        }
    }

    //加密
    public static String getEncryptString(String str) {
        // 基于BASE64编码，接收byte[]并转换成String
        try {
            // 按UTF8编码
            byte[] bytes = str.getBytes(CHARSETNAME);
            // 获取加密对象
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            // 初始化密码信息
            cipher.init(Cipher.ENCRYPT_MODE, key);
            // 加密
            byte[] doFinal = cipher.doFinal(bytes);
            // byte[]to encode好的String并返回
            return new String(Base64.getEncoder().encode(doFinal));
        } catch (Exception e) {
            log.error("DESUtil 加密 Exception ：",e);
            throw new RuntimeException(e);
        }
    }


    //解密
    public static String getDecryptString(String str) {
        // 基于BASE64编码，接收byte[]并转换成String
        try {
            // 将字符串decode成byte[]
            byte[] bytes = Base64.getDecoder().decode(str);
            // 获取解密对象
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            // 初始化解密信息
            cipher.init(Cipher.DECRYPT_MODE, key);
            // 解密
            byte[] doFinal = cipher.doFinal(bytes);
            // 返回解密之后的信息
            return new String(doFinal, CHARSETNAME);
        } catch (Exception e) {
            log.error("DESUtil 解密 Exception ：",e);
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {

        String miyao = DesUtils.getEncryptString("{\"sslJksPath\":\"xianyu.jks\",\"keyStorePassword\":\"123456\",\"serverAddress\":\"mstsc.xianyum.cn\",\"serverPort\":\"9101\",\"userKey\":\"d8331bd8fecf42d4919b1d230470fcbc\",\"sslEnable\":true,\"autoStart\":false}\n");
        System.out.println(miyao);
        System.out.println(DesUtils.getDecryptString(miyao));
    }
}
