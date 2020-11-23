package com.base;

import com.base.service.iservice.OssService;
import com.base.service.iservice.QqNetService;
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
    private OssService ossService;


    @Test
    public void test() {

        //System.out.println(qqNetService.getAccessToken("61FF7A86412AD0352F8B9BAB53EF4F56"));
//        System.out.println("获取的TOken："+ossService.getWebUpToken());
    }

}

