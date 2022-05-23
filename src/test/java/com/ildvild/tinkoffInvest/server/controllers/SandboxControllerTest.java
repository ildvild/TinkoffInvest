package com.ildvild.tinkoffInvest.server.controllers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.tinkoff.piapi.contract.v1.Account;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SandboxControllerTest {

    @Autowired
    private SandboxController sandboxController;

    private String accountId;

    @BeforeEach
    public void before() {
        accountId = sandboxController.createAccount();
    }

    @AfterEach
    public void after() {
        sandboxController.closeAccount(accountId);
    }

    @Test
    public void getAccount() {
        assertTrue(StringUtils.isNotBlank(accountId));
    }

    @Test
    public void getAccounts() {
        List<Account> accounts = sandboxController.getAccounts();
        assertNotNull(accounts);
        assertTrue(accounts.stream().anyMatch(a -> accountId.equals(a.getId())));
    }
}