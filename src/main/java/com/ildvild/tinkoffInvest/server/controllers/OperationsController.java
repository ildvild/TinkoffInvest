package com.ildvild.tinkoffInvest.server.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.tinkoff.piapi.contract.v1.Operation;
import ru.tinkoff.piapi.core.OperationsService;
import ru.tinkoff.piapi.core.models.Portfolio;
import ru.tinkoff.piapi.core.models.Positions;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component("serverOperationsController")
@Primary
public class OperationsController {

    private OperationsService operationsService;

    public OperationsController(TinkoffInvestController investController) {
         this.operationsService = investController.getInvestApi().getOperationsService();
    }

    public Portfolio getPortfolio(String accountId) {
        log.info("Получение портфеля по счету {}", accountId);
        return operationsService.getPortfolioSync(accountId);
    }

    public Positions getPositions(String accountId) {
        log.info("Получение списка позиций по счету {}", accountId);
        return operationsService.getPositionsSync(accountId);
    }

    public List<Operation> getOperations(String accountId, Instant from) {
        log.info("Получение операций по счету {}", accountId);
        Instant to = Instant.now();
        return operationsService.getAllOperationsSync(accountId, from, to);
    }
}
