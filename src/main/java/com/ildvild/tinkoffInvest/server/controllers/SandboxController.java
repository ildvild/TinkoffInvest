package com.ildvild.tinkoffInvest.server.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import ru.tinkoff.piapi.contract.v1.Account;
import ru.tinkoff.piapi.contract.v1.MoneyValue;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.SandboxService;

import java.util.List;

@Slf4j
@Component
@PropertySource(value= "classpath:server-config.properties")
public class SandboxController {

    private InvestApi api;

    private SandboxService sandboxService;

    @Value("${sandbox-account}")
    private String accountId;

    public SandboxController(TinkoffInvestController investController) {
        this.api = investController.getSandboxInvestApi();
        sandboxService = api.getSandboxService();
    }

    public String createAccount() {
        String accountId = sandboxService.openAccountSync();
        log.info("Открыт счет в песочнице: {}", accountId);
        return accountId;
    }

    public void closeAccount(String accountId) {
        sandboxService.closeAccountSync(accountId);
        log.info("Закрыт счет в песочнице: {}", accountId);
    }

    public List<Account> getAccounts() {
        log.info("Получение брокерских счетов в песочнице");
        return sandboxService.getAccountsSync();
    }

    public void payIn(String accountId, long amount){
        log.info("Пополнение счета {} на {}", accountId, amount);
        sandboxService.payIn(accountId, MoneyValue.newBuilder().setCurrency("rub").setUnits(amount).build());
    }
}
