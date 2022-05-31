package com.ildvild.tinkoffInvest.server.controllers;

import com.ildvild.tinkoffInvest.server.TinkoffInvestConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.tinkoff.piapi.contract.v1.Account;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.UsersService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component("serverAccountsController")
public class AccountsController {

    private final InvestApi investApi;
    private final SandboxController sandboxController;
    private final UsersService userService;

    public AccountsController(InvestApi investApi, SandboxController sandboxController) {
        this.investApi = investApi;
        this.sandboxController = sandboxController;
        this.userService = investApi.getUserService();
    }

    public List<Account> getAccounts() {
        log.info("Получение брокерских счетов");
        if (!sandboxController.isEnabled()) {
            return userService.getAccountsSync();
        } else {
            return sandboxController.getAccounts();
        }
    }
}
