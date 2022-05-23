package com.ildvild.tinkoffInvest;

import com.ildvild.tinkoffInvest.server.controllers.HistoricCandlesController;
import com.ildvild.tinkoffInvest.server.controllers.TinkoffInvestController;
import com.ildvild.tinkoffInvest.server.robots.BazaarRobot;
import com.ildvild.tinkoffInvest.server.robots.BuyAverageAndSell;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class TinkoffInvestApplicationTests {

    @Autowired
    private TinkoffInvestController tinkoffInvestController;

    @Autowired
    private HistoricCandlesController historicCandlesController;

    @Autowired
    private BazaarRobot bazaarRobot;

    @Autowired
    private BuyAverageAndSell buyAverageAndSell;

    @Test
    void contextLoads() {
        assertThat(tinkoffInvestController).isNotNull();
        assertThat(historicCandlesController).isNotNull();
        assertThat(buyAverageAndSell).isNotNull();
        assertThat(bazaarRobot).isNotNull();
    }

}
