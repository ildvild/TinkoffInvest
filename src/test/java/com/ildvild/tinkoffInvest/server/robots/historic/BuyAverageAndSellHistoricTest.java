package com.ildvild.tinkoffInvest.server.robots.historic;

import com.google.common.collect.Iterables;
import com.ildvild.tinkoffInvest.server.controllers.HistoricCandlesController;
import com.ildvild.tinkoffInvest.server.controllers.OperationsController;
import com.ildvild.tinkoffInvest.server.controllers.OrdersController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.TestPropertySource;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static ru.tinkoff.piapi.core.utils.MapperUtils.quotationToBigDecimal;

@TestPropertySource(properties="buyAverageAndSellTest.enabled=true")
@SpringBootTest
class BuyAverageAndSellHistoricTest {

    @Autowired
    private HistoricCandlesController historicCandlesController;

    @SpyBean
    private OrdersController ordersController;

    @SpyBean
    private OperationsController serverOperationsController;

    @Autowired
    private BuyAverageAndSellHistoric buyAverageAndSell;

    @Test
    public void testBuyAverageAndSell() throws Exception {
        // figi = "BBG004S683W7"; // AFLT
        String figi = "BBG004S68CP5"; // MVID
        BigDecimal initMoney = BigDecimal.valueOf(10000);
        buyAverageAndSell.setOrdersController(ordersController);
        buyAverageAndSell.setServerOperationsController(serverOperationsController);

        doReturn(new ArrayList<>()).when(ordersController).getOrders(anyString());
        doNothing().when(ordersController).cancelAllOrders(anyString());

        buyAverageAndSell.initFreeRubMoney(initMoney);
        buyAverageAndSell.start();
        List<HistoricCandle> candles = historicCandlesController.getDayCandles(figi);
        if (candles.isEmpty()) {
            throw new Exception("Отсутствует история");
        }
        for (HistoricCandle candle : candles) {
            buyAverageAndSell.processCandle(figi, candle);
        }
        buyAverageAndSell.stop();

        HistoricCandle lastCandle = Iterables.getLast(candles);
        BigDecimal lastPrice = quotationToBigDecimal(lastCandle.hasClose() ? lastCandle.getClose() : lastCandle.getOpen());

        assertThat(buyAverageAndSell.isStopped());
        System.out.println(buyAverageAndSell.getPortfolioAmount(figi, lastPrice));
        assertThat(buyAverageAndSell.getPortfolioAmount(figi, lastPrice).compareTo(initMoney) > 0);
    }
}