package com.ildvild.tinkoffInvest.server.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.tinkoff.piapi.contract.v1.Account;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class AccountsControllerTest {

    @Autowired
    private AccountsController accountsController;

    @Test
    public void getInstrumentByFigi() {
        List<Account> accounts = accountsController.getAccounts();
        assertNotNull(accounts);
    }
}