package com.ildvild.tinkoffInvest.server.robots;

import com.ildvild.tinkoffInvest.server.controllers.InstrumentsController;
import com.ildvild.tinkoffInvest.server.controllers.OperationsController;
import com.ildvild.tinkoffInvest.server.controllers.OrdersController;
import com.ildvild.tinkoffInvest.server.robots.exceptions.RobotStateException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import ru.tinkoff.piapi.contract.v1.Candle;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;
import ru.tinkoff.piapi.core.models.Portfolio;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
public abstract class AbstractRobot implements Robot {
    private UUID id;
    @Getter
    protected String accountId;
    @Getter
    private String description;
    @Getter
    private State state;

    protected OrdersController ordersController;

    @Autowired
    public void setOrdersController(OrdersController ordersController) {
        this.ordersController = ordersController;
    }

    protected OperationsController serverOperationsController;

    @Autowired
    public void setServerOperationsController(OperationsController serverOperationsController) {
        this.serverOperationsController = serverOperationsController;
    }

    protected InstrumentsController instrumentsController;

    @Autowired
    public void setInstrumentsController(InstrumentsController instrumentsController) {
        this.instrumentsController = instrumentsController;
    }

    public AbstractRobot(String accountId) {
        assert (StringUtils.isNotEmpty(accountId));
        this.accountId = accountId;
        this.description = "";
        this.state = State.STOP;
        this.id = UUID.randomUUID();
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void start() {
        state = State.WORKING;
    }

    @Override
    public void stop() {
        ordersController.cancelAllOrders(accountId);
        state = State.STOP;
    }

    @Override
    public boolean isWorking() {
        return state == State.WORKING;
    }

    @Override
    public boolean isStopped() {
        return state == State.STOP;
    }

    @Override
    public BigDecimal getPortfolioExpectedYield(String figi, HistoricCandle candle) throws RobotStateException {
        Portfolio portfolio = serverOperationsController.getPortfolio(accountId);
        return portfolio.getExpectedYield();
    }

    @Override
    public void processCandle(String figi, Candle candle) throws RobotStateException {
        if (isStopped()) {
            throw new RobotStateException("Робот [" + getName() + "] не запущен. Для начала работы запустите робота.");
        }
    }

    @Override
    public void processCandle(String figi, HistoricCandle candle) throws RobotStateException {
        if (isStopped()) {
            throw new RobotStateException("Робот [" + getName() + "] не запущен. Для начала работы запустите робота.");
        }
    }

    protected void initFreeRubMoney(BigDecimal value) {
        //
    }

    protected BigDecimal getFreeRubMoney() {
        return BigDecimal.ZERO;
    }
}
