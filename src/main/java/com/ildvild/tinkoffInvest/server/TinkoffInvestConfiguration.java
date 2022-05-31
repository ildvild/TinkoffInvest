package com.ildvild.tinkoffInvest.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import ru.tinkoff.piapi.core.InvestApi;

import java.util.List;

@Slf4j
@Configuration
@PropertySource(value = "classpath:server-config.properties")
public class TinkoffInvestConfiguration {

    @Value("${token}")
    private String token;

    @Value("${sandbox-token}")
    private String sandboxToken;

    @Value("${app-name}")
    private String appName;

    private static InvestApi api;

    @Bean
    @ConditionalOnProperty(prefix = "sandbox", name = "enabled", havingValue = "false")
    public InvestApi getInvestApi() {
        if (api == null) {
            log.info("Получениe API для торговли");
            api = InvestApi.create(token, appName);
        }
        return api;
    }

    @Bean
    @ConditionalOnProperty(prefix = "sandbox", name = "enabled", havingValue = "true")
    public InvestApi getSandboxInvestApi() {
        if (api == null) {
            log.info("Получениe API для торговли в режиме песочницы");
            api = InvestApi.createSandbox(sandboxToken, appName);
        }
        return api;
    }
}
