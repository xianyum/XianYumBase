package com.base;


import com.base.common.utils.UUIDUtils;
import com.base.config.PhoneConfig;
import com.base.dao.XiaoDaoMapper;
import com.base.entity.po.xiaodao.XiaoDaoEntity;
import com.geccocrawler.gecco.GeccoEngine;
import com.geccocrawler.gecco.pipeline.PipelineFactory;
import com.geccocrawler.gecco.request.HttpGetRequest;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ApplicationTests {

    @Autowired
    private PhoneConfig phoneConfig;

    @Autowired
    private XiaoDaoMapper xiaoDaoMapper;

    @Resource
    private PipelineFactory springPipelineFactory;

    @Test
    public void test() {
        // 需要发送短信的手机号码
        String[] phoneNumbers = {"17602912005"};

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
    @Test
    public void test2(){
        HttpGetRequest startUrl = new HttpGetRequest("https://www.xd0.com/");
        startUrl.setCharset("UTF-8");
        GeccoEngine.create()
                //Gecco搜索的包路径
                .classpath("com.base.analysis")
                .pipelineFactory(springPipelineFactory)
                //开始抓取的页面地址
                .start(startUrl)
                //开启几个爬虫线程
                .thread(1)
                //单个爬虫每次抓取完一个请求后的间隔时间
                .interval(2000)
                .run();
    }
}

