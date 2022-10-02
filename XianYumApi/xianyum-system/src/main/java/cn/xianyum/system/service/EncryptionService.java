package cn.xianyum.system.service;

/**
 * @author zhangwei
 * @date 2022/7/17 22:40
 */
public interface EncryptionService {
    String aesEncrypt(String content);

    /**
     * 获取aes加密key
     * @return
     */
    String getAesKey();

    String aesDecrypt(String content);

    /**
     * 校验内容是否为空
     * @param content
     */
    void checkEmpty(String... content);
}
