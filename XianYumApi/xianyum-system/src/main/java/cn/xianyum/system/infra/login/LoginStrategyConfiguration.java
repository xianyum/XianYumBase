package cn.xianyum.system.infra.login;

import cn.xianyum.common.enums.LoginTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 登录策略配置类
 * 将所有 LoginStrategy 实现按 getLoginType() 构建为 Map，便于按类型快速查找
 */
@Slf4j
@Configuration
public class LoginStrategyConfiguration {

    @Bean
    public Map<LoginTypeEnum, LoginStrategy> loginStrategyMap(List<LoginStrategy> loginStrategyList) {
        Map<LoginTypeEnum, LoginStrategy> strategyMap = loginStrategyList.stream()
                .collect(Collectors.toMap(LoginStrategy::getLoginType, Function.identity()));
        strategyMap.forEach((type, strategy) -> log.info("初始化登录策略 [{}] → {}", type, strategy.getClass().getSimpleName()));
        return strategyMap;
    }
}
