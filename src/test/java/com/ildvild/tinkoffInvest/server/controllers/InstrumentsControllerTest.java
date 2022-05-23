package com.ildvild.tinkoffInvest.server.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.tinkoff.piapi.contract.v1.Instrument;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class InstrumentsControllerTest {

    @Autowired
    private InstrumentsController instrumentsController;

    @Test
    public void getInstrumentByFigi() {
        String figi = "BBG0100R9963";
        Instrument instrument = instrumentsController.getByFigi(figi);
        assertNotNull(instrument);
    }
}