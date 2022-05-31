package com.ildvild.tinkoffInvest.server.controllers;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ListenableFuture;
import com.ildvild.tinkoffInvest.server.TinkoffInvestConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.tinkoff.piapi.contract.v1.Instrument;
import ru.tinkoff.piapi.core.InvestApi;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class InstrumentsController {

    private final InvestApi investApi;

    private final LoadingCache<String, Instrument> cache;

    public InstrumentsController(InvestApi investApi) {
        this.investApi = investApi;
        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(30, TimeUnit.DAYS)
                .build(
                        new CacheLoader<>() {
                            @Override
                            public Instrument load(String key) throws Exception {
                                log.info("Загрузка информации об инструменте для {}", key);
                                return investApi.getInstrumentsService().getInstrumentByFigiSync(key);
                            }

                            @Override
                            public ListenableFuture<Instrument> reload(String key, Instrument oldValue) throws Exception {
                                log.info("Обновление информации об инструменте для {}", key);
                                return super.reload(key, oldValue);
                            }
                        });
    }

    public Instrument getByFigi(String figi) {
        try {
            return cache.get(figi);
        } catch (ExecutionException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
