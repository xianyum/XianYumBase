package com.base.analysis;

import com.geccocrawler.gecco.pipeline.PipelineFactory;
import com.geccocrawler.gecco.spring.SpringPipelineFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangwei
 * @date 2019/9/25 16:28
 */
@Configuration
public class PipelineFactoryConfig {
    @Bean
    public PipelineFactory springPipelineFactory(){
        return new SpringPipelineFactory();
    }
}
