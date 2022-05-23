package com.ildvild.tinkoffInvest.server.controllers;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.tinkoff.piapi.contract.v1.Instrument;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class InstrumentsController {

    private final TinkoffInvestController investController;

    private final LoadingCache<String, Instrument> cache;

    public InstrumentsController(TinkoffInvestController investController) {
        this.investController = investController;
        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(30, TimeUnit.DAYS)
                .build(
                        new CacheLoader<>() {
                            @Override
                            public Instrument load(String key) throws Exception {
                                log.info("Загрузка информации об инструменте для {}", key);
                                return investController.getInvestApi().getInstrumentsService().getInstrumentByFigiSync(key);
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
