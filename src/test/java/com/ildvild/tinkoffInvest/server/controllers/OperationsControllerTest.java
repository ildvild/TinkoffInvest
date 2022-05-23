package com.ildvild.tinkoffInvest.server.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.tinkoff.piapi.core.models.Portfolio;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OperationsControllerTest {

    @Autowired
    private OperationsController operationsController;

    @Test
    public void getInstrumentByFigi() {
        //Укажите ид счета
        String accountId = "2167248737";
        Portfolio portfolio = operationsController.getPortfolio(accountId);
        assertNotNull(portfolio);
    }
}