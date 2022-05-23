package com.ildvild.tinkoffInvest.client.controllers.portfolio;

import com.ildvild.tinkoffInvest.client.controllers.positions.PositionsController;
import com.ildvild.tinkoffInvest.server.controllers.OperationsController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Slf4j
@Component("clientPortfolioController")
@PropertySource(value = "classpath:client-config.properties")
public class PortfolioController {

    private final OperationsController serverOperationsController;

    private final PositionsController clientPositionsController;

    public PortfolioController(OperationsController serverOperationsController,
                               PositionsController clientPositionsController) {
        this.serverOperationsController = serverOperationsController;
        this.clientPositionsController = clientPositionsController;
    }

    private Portfolio convert(ru.tinkoff.piapi.core.models.Portfolio portfolio) {
        Portfolio p = new Portfolio();
        p.setExpectedYield(portfolio.getExpectedYield());
        p.setPositions(portfolio.getPositions().stream().map(clientPositionsController::convertPosition).collect(Collectors.toList()));

        return p;
    }

    public Portfolio getPortfolio(String accountId) {
        return convert(serverOperationsController.getPortfolio(accountId));
    }
}
