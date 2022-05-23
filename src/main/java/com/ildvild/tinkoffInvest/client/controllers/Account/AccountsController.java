package com.ildvild.tinkoffInvest.client.controllers.Account;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static ru.tinkoff.piapi.core.utils.DateUtils.timestampToInstant;

@Slf4j
@Component("clientAccountsController")
public class AccountsController {

    private final com.ildvild.tinkoffInvest.server.controllers.AccountsController serverAccountsController;

    public AccountsController(com.ildvild.tinkoffInvest.server.controllers.AccountsController serverAccountsController) {
        this.serverAccountsController = serverAccountsController;
    }

    private Account convert( ru.tinkoff.piapi.contract.v1.Account account) {
        Account a = new Account();
        a.setName(account.getName());
        a.setId(account.getId());
        a.setStatus(account.getStatusValue());
        a.setOpenedDate(timestampToInstant(account.getOpenedDate()));

        return a;
    }

    public List<Account> getAccounts() {
        return serverAccountsController.getAccounts().stream().map(this::convert).collect(Collectors.toList());
    }
}
