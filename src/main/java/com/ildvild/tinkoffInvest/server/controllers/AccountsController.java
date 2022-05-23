package com.ildvild.tinkoffInvest.server.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.tinkoff.piapi.contract.v1.Account;
import ru.tinkoff.piapi.core.UsersService;

import java.util.List;

@Slf4j
@Component("serverAccountsController")
public class AccountsController {

    private UsersService userService;

    public AccountsController(TinkoffInvestController investController) {
         this.userService = investController.getInvestApi().getUserService();
    }

    public List<Account> getAccounts() {
        log.info("Получение брокерских счетов");
        return userService.getAccountsSync();
    }
}
