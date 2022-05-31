package com.ildvild.tinkoffInvest.server.controllers;

import com.ildvild.tinkoffInvest.server.TinkoffInvestConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.tinkoff.piapi.contract.v1.Operation;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.OperationsService;
import ru.tinkoff.piapi.core.models.Portfolio;
import ru.tinkoff.piapi.core.models.Positions;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component("serverOperationsController")
@Primary
public class OperationsController {

    private final InvestApi investApi;
    private final SandboxController sandboxController;
    private final OperationsService operationsService;

    public OperationsController(InvestApi investApi, SandboxController sandboxController) {
        this.investApi = investApi;
        this.sandboxController = sandboxController;
        this.operationsService = investApi.getOperationsService();
    }

    public Portfolio getPortfolio(String accountId) {
        log.info("Получение портфеля по счету {}", accountId);
        if (!sandboxController.isEnabled()) {
            return operationsService.getPortfolioSync(accountId);
        } else {
            return sandboxController.getPortfolioSync(accountId);
        }
    }

    public Positions getPositions(String accountId) {
        log.info("Получение списка позиций по счету {}", accountId);
        if (!sandboxController.isEnabled()) {
            return operationsService.getPositionsSync(accountId);
        } else {
            return sandboxController.getPositionsSync(accountId);
        }
    }

    public List<Operation> getOperations(String accountId, Instant from) {
        log.info("Получение операций по счету {}", accountId);
        Instant to = Instant.now();
        if (!sandboxController.isEnabled()) {
            return operationsService.getAllOperationsSync(accountId, from, to);
        } else {
            return sandboxController.getAllOperationsSync(accountId, from, to);
        }
    }
}
