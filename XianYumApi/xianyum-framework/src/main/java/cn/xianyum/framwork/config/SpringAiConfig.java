package cn.xianyum.framwork.config;

import org.springframework.ai.chat.client.ChatClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring AI 配置类
 * @author xianyum
 * @date 2026/4/10
 */
@Configuration
public class SpringAiConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder.build();
    }

}
