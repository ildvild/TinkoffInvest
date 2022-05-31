package com.ildvild.tinkoffInvest.server.controllers;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ListenableFuture;
import com.ildvild.tinkoffInvest.server.TinkoffInvestConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.MarketDataService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static java.time.temporal.ChronoUnit.DAYS;
import static ru.tinkoff.piapi.contract.v1.CandleInterval.CANDLE_INTERVAL_DAY;

@Slf4j
@Component
@PropertySource(value = "classpath:server-config.properties")
public class HistoricCandlesController {

    private static final int DAY_IN_YEAR = 365;
    private final LoadingCache<String, List<HistoricCandle>> cacheDay;

    private MarketDataService marketDataService;

    @Value("${candleCacheLoadDays}")
    private int candleCacheLoadDays;

    public HistoricCandlesController(InvestApi investApi) {
        MarketDataService marketDataService = investApi.getMarketDataService();
        cacheDay = CacheBuilder.newBuilder()
                .expireAfterWrite(7, TimeUnit.DAYS)
                .build(
                        new CacheLoader<>() {
                            @Override
                            public List<HistoricCandle> load(String key) throws Exception {
                                log.info("Загрузка исторических свечей по инструменту {} за {} дней", key, candleCacheLoadDays);
                                List<HistoricCandle> result = new ArrayList<>(candleCacheLoadDays);
                                Instant to = Instant.now();
                                Instant from = to.minus(candleCacheLoadDays, DAYS);

                                Instant toPartition = to;
                                Instant fromPartition = to.minus(DAY_IN_YEAR, DAYS);
                                while (toPartition.isAfter(from)) {
                                    List<HistoricCandle> candles = marketDataService.getCandlesSync(key, fromPartition, toPartition, CANDLE_INTERVAL_DAY);
                                    if (candles.isEmpty()) {
                                        break;
                                    }
                                    result.addAll(0, candles);

                                    toPartition = toPartition.minus(DAY_IN_YEAR, DAYS);
                                    fromPartition = toPartition.minus(DAY_IN_YEAR, DAYS);
                                    if (fromPartition.isBefore(from)) {
                                        fromPartition = from;
                                    }
                                }

                                return result;
                            }

                            @Override
                            public ListenableFuture<List<HistoricCandle>> reload(String key, List<HistoricCandle> oldValue) throws Exception {
                                log.info("Обновление исторических свечей по инструменту {}", key);
                                return super.reload(key, oldValue);
                            }
                        });
    }

    public List<HistoricCandle> getDayCandles(String figi) {
        try {
            return cacheDay.get(figi);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
