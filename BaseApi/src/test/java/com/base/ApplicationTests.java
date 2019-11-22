package com.base;


import com.base.common.utils.UUIDUtils;
import com.base.config.PhoneConfig;
import com.base.service.iservice.QqNetService;
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

    @Autowired
    private QqNetService qqNetService;


    @Test
    public void test() {
        qqNetService.getAccessToken("","");
    }

}

