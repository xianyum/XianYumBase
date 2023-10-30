package cn.xianyum.system.service.impl;

import cn.xianyum.common.exception.SoException;
import cn.xianyum.common.utils.EncryptionUtils;
import cn.xianyum.common.utils.StringUtil;
import cn.xianyum.system.service.EncryptionService;
import cn.xianyum.system.service.SystemConstantService;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author zhangwei
 * @date 2022/7/17 22:41
 */
@Slf4j
@Service
public class EncryptionServiceImpl implements EncryptionService {

    @Autowired
    private SystemConstantService systemConstantService;

    private static final String ENCRYPT = "encrpt_secret";

    /**
     * AES加密
     * @param content
     * @return
     */
    @Override
    public String aesEncrypt(String content) {
        this.checkEmpty(content);
        String key = this.getAesKey();
        String encrypt = EncryptionUtils.AES.encrypt(content.trim(), key);
        return encrypt;
    }

    @Override
    public String getAesKey() {
        String valueKey = systemConstantService.getValueKey(ENCRYPT);
        String aesKey = JSONObject.parseObject(valueKey).getString("aes");
        return aesKey;
    }

    /**
     * AES解密
     * @param content
     * @return
     */
    @Override
    public String aesDecrypt(String content) {
        String key = this.getAesKey();
        String decrypt = EncryptionUtils.AES.decrypt(content.trim(), key);
        return decrypt;
    }

    @Override
    public void checkEmpty(String... content) {
        for(String item : content){
            if(StringUtil.isBlank(item)){
                throw new SoException("内容不能为空！");
            }
        }
    }
}
