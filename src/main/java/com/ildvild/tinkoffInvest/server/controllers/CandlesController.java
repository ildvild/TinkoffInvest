package com.ildvild.tinkoffInvest.server.controllers;

import com.ildvild.tinkoffInvest.server.robots.AbstractRobot;
import com.ildvild.tinkoffInvest.server.robots.exceptions.RobotStateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import ru.tinkoff.piapi.contract.v1.Candle;
import ru.tinkoff.piapi.contract.v1.MarketDataResponse;
import ru.tinkoff.piapi.contract.v1.SubscriptionInterval;
import ru.tinkoff.piapi.core.stream.MarketDataStreamService;
import ru.tinkoff.piapi.core.stream.MarketDataSubscriptionService;
import ru.tinkoff.piapi.core.stream.StreamProcessor;

import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Component
@PropertySource(value = "classpath:server-config.properties")
public class CandlesController {

    private static final String STREAM_ID = "a806b056-99b4-496e-83e0-70e8483aa6d3";

    private MarketDataStreamService marketDataStreamService;
    private MarketDataSubscriptionService marketDataSubscriptionService;

    private final List<AbstractRobot> robots;

    public CandlesController(TinkoffInvestController investController, List<AbstractRobot> robots) {
        this.marketDataStreamService = investController.getInvestApi().getMarketDataStreamService();
        this.robots = robots;
        createStream(investController.getFigies(), robots);
    }

    private void createStream(List<String> figies, List<AbstractRobot> robots) {
        Consumer<Throwable> onErrorCallback = error -> log.error(error.toString());
        StreamProcessor<MarketDataResponse> processor = response -> {
            if (response.hasCandle()) {
                Candle candle = response.getCandle();
                log.debug("Получена новая свеча для {}", candle.getFigi());

                robots.forEach(r -> {
                    try {
                        if (r.isWorking()) {
                            r.processCandle(candle.getFigi(), candle);
                        }
                    } catch (RobotStateException e) {
                        //log.warn(e.getMessage());
                    }
                });
            }
        };

        MarketDataSubscriptionService stream = marketDataStreamService.newStream(STREAM_ID, processor, onErrorCallback);
        stream.subscribeCandles(figies, SubscriptionInterval.SUBSCRIPTION_INTERVAL_FIVE_MINUTES);
    }

}
