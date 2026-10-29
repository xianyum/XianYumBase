package cn.xianyum.common.utils;

import cn.hutool.crypto.digest.BCrypt;

public class SecretUtils {


    /**
     * 密码加密
     * @param password
     * @return
     */
    public static String encryptPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * 密码校验
     * @param rawPassword
     * @param encodedPassword
     * @return
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }
}
