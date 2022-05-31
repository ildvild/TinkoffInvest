package com.ildvild.tinkoffInvest.server.controllers;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ListenableFuture;
import com.ildvild.tinkoffInvest.server.TinkoffInvestConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.tinkoff.piapi.contract.v1.Dividend;
import ru.tinkoff.piapi.core.InvestApi;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static java.time.temporal.ChronoUnit.DAYS;

@Slf4j
@Component("serverDividendsController")
public class DividendsController {

    @Autowired
    private InvestApi investApi;

    private final LoadingCache<String, List<Dividend>> cache;

    public DividendsController() {
        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(7, TimeUnit.DAYS)
                .build(
                        new CacheLoader<>() {
                            @Override
                            public List<Dividend> load(String key) throws Exception {
                                log.info("Загрузка информации о дивидендах для {}", key);
                                List<Dividend> result = new ArrayList<>();
                                Instant from = Instant.now();
                                Instant to = from.plus(100, DAYS);
                                return investApi.getInstrumentsService().getDividendsSync(key, from, to);
                            }

                            @Override
                            public ListenableFuture<List<Dividend>> reload(String key, List<Dividend> oldValue) throws Exception {
                                log.info("Обновление информации о дивидендах для {}", key);
                                return super.reload(key, oldValue);
                            }
                        });
    }

    public List<Dividend> getByFigi(String figi) {
        try {
            return cache.get(figi);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
