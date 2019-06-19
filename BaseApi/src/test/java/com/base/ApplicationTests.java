package com.base;


import com.base.common.utils.UUIDUtils;
import com.base.config.PhoneConfig;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ApplicationTests {

    @Autowired
    private PhoneConfig phoneConfig;

    @Test
    public void test() {
        // 需要发送短信的手机号码
        String[] phoneNumbers = {"17791291739"};

        try {
            String[] params = {UUIDUtils.getRandomNumber(6),"1"};
            SmsSingleSender ssender = new SmsSingleSender(phoneConfig.getAppid(), phoneConfig.getAppkey());
            SmsSingleSenderResult result = ssender.sendWithParam("86", phoneNumbers[0],
                    phoneConfig.getTemplateId(), params, phoneConfig.getSmsSign(), "", "");  // 签名参数未提供或者为空时，会使用默认签名发送短信
            log.error(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

