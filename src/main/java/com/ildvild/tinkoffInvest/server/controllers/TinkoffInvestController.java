package com.ildvild.tinkoffInvest.server.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import ru.tinkoff.piapi.core.InvestApi;

import java.util.List;

@Slf4j
@Component
@PropertySource(value = "classpath:server-config.properties")
public class TinkoffInvestController {

    @Value("${token}")
    private String token;

    @Value("${sandbox-token}")
    private String sandboxToken;

    @Value("${app-name}")
    private String appName;

    @Value("#{'${figies}'.split(',')}")
    private List<String> figies;

    private static InvestApi api;
    private static InvestApi sandboxApi;

    public InvestApi getInvestApi() {
        if (api == null) {
            api = InvestApi.create(token, appName);
        }
        return api;
    }

    public InvestApi getSandboxInvestApi() {
        if (sandboxApi == null) {
            sandboxApi = InvestApi.createSandbox(sandboxToken, appName);
        }
        return sandboxApi;
    }

    public List<String> getFigies() {
        return figies;
    }
}
