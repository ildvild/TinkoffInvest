package com.ildvild.tinkoffInvest.server.controllers;

import com.ildvild.tinkoffInvest.server.TinkoffInvestConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import ru.tinkoff.piapi.contract.v1.*;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.SandboxService;
import ru.tinkoff.piapi.core.models.Portfolio;
import ru.tinkoff.piapi.core.models.Positions;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static ru.tinkoff.piapi.contract.v1.OperationState.OPERATION_STATE_EXECUTED;

@Slf4j
@Component
@PropertySource(value = "classpath:server-config.properties")
public class SandboxController {

    private final SandboxService sandboxService;

    @Value("${sandbox.enabled:false}")
    private boolean enabled;

    public SandboxController(InvestApi investApi) {
        sandboxService = investApi.getSandboxService();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String createAccount() {
        String accountId = sandboxService.openAccountSync();
        log.info("Открыт счет в песочнице: {}", accountId);
        return accountId;
    }

    public void closeAccount(String accountId) {
        sandboxService.closeAccountSync(accountId);
        log.info("Закрыт счет в песочнице: {}", accountId);
    }

    public List<Account> getAccounts() {
        return sandboxService.getAccountsSync();
    }

    public Portfolio getPortfolioSync(String accountId) {
        return Portfolio.fromResponse(sandboxService.getPortfolioSync(accountId));
    }

    public Positions getPositionsSync(String accountId) {
        return Positions.fromResponse(sandboxService.getPositionsSync(accountId));
    }

    public List<OrderState> getOrdersSync(String accountId) {
        return sandboxService.getOrdersSync(accountId);
    }

    public Instant cancelOrderSync(String accountId, String orderId) {
        return sandboxService.cancelOrderSync(accountId, orderId);
    }

    public List<Operation> getAllOperationsSync(String accountId, Instant from, Instant to) {
        return sandboxService.getOperationsSync(accountId, from, to, OPERATION_STATE_EXECUTED, null);
    }

    public void payIn(String accountId, long amount) {
        log.info("Пополнение счета {} на {}", accountId, amount);
        sandboxService.payIn(accountId, MoneyValue.newBuilder().setCurrency("rub").setUnits(amount).build());
    }
}
