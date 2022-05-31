package com.ildvild.tinkoffInvest.server.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.tinkoff.piapi.contract.v1.OrderState;
import ru.tinkoff.piapi.contract.v1.OrderTrades;
import ru.tinkoff.piapi.contract.v1.Quotation;
import ru.tinkoff.piapi.contract.v1.TradesStreamResponse;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.OrdersService;
import ru.tinkoff.piapi.core.stream.OrdersStreamService;
import ru.tinkoff.piapi.core.stream.StreamProcessor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static ru.tinkoff.piapi.contract.v1.OrderDirection.ORDER_DIRECTION_BUY;
import static ru.tinkoff.piapi.contract.v1.OrderDirection.ORDER_DIRECTION_SELL;
import static ru.tinkoff.piapi.contract.v1.OrderType.ORDER_TYPE_LIMIT;
import static ru.tinkoff.piapi.contract.v1.OrderType.ORDER_TYPE_MARKET;
import static ru.tinkoff.piapi.core.utils.MapperUtils.bigDecimalToQuotation;

@Slf4j
@Component
public class OrdersController {

    private final InvestApi investApi;
    private final SandboxController sandboxController;
    private final OrdersService ordersService;
    private final OrdersStreamService ordersStreamService;

    public OrdersController(InvestApi investApi, SandboxController sandboxController) {
        this.investApi = investApi;
        this.sandboxController = sandboxController;
        this.ordersService = investApi.getOrdersService();
        this.ordersStreamService = investApi.getOrdersStreamService();
    }

    public List<OrderState> getOrders(String accountId) {
        log.info("Получение заявок по счету {}", accountId);
        if (!sandboxController.isEnabled()) {
            return ordersService.getOrdersSync(accountId);
        } else {
            return new ArrayList<>();
        }
    }

    public void postBuyOrder(String accountId, String figi, long quantity, BigDecimal price) {
        log.info("Выставление лимитной заявки по счету {} на покупку {} в количестве {} по цене {}",
                accountId, figi, quantity, price);
        String orderId = UUID.randomUUID().toString();
        Quotation quotationPrice = bigDecimalToQuotation(price);
        ordersService.postOrderSync(figi, quantity, quotationPrice, ORDER_DIRECTION_BUY, accountId, ORDER_TYPE_LIMIT, orderId);
    }


    public void postSellOrder(String accountId, String figi, long quantity, BigDecimal price) {
        log.info("Выставление лимитной заявки по счету {} на продажу {} в количестве {} по цене {}",
                accountId, figi, quantity, price);
        String orderId = UUID.randomUUID().toString();
        Quotation quotationPrice = bigDecimalToQuotation(price);
        ordersService.postOrderSync(figi, quantity, quotationPrice, ORDER_DIRECTION_SELL, accountId, ORDER_TYPE_LIMIT, orderId);
    }

    public void postBuyMarketOrder(String accountId, String figi, long quantity, BigDecimal price) {
        log.info("Выставление рыночной заявки по счету {} на покупку {} в количестве {} по цене {}",
                accountId, figi, quantity, price);
        String orderId = UUID.randomUUID().toString();
        Quotation quotationPrice = bigDecimalToQuotation(price);
        ordersService.postOrderSync(figi, quantity, quotationPrice, ORDER_DIRECTION_BUY, accountId, ORDER_TYPE_MARKET, orderId);
    }

    public void cancelOrder(String accountId, String orderId) {
        log.info("Отмена заявки {} по счету {}", accountId);
        Instant time = null;
        if (!sandboxController.isEnabled()) {
            time = ordersService.cancelOrderSync(accountId, orderId);
        } else {
            time = sandboxController.cancelOrderSync(accountId, orderId);
        }
        log.info("Заявка {} отменена в {}", orderId, time);
    }

    public void cancelAllOrders(String accountId) {
        log.info("Отмена всех заявок по счету {}", accountId);
        List<OrderState> orders = null;
        if (!sandboxController.isEnabled()) {
            orders = ordersService.getOrdersSync(accountId);
        } else {
            orders = sandboxController.getOrdersSync(accountId);
        }
        orders.forEach(o -> cancelOrder(accountId, o.getOrderId()));
    }

    public void subscribeOrders(List<String> accountIds) {
        Consumer<Throwable> onErrorCallback = error -> log.error(error.toString());
        StreamProcessor<TradesStreamResponse> processor = response -> {
            if (response.hasOrderTrades()) {
                OrderTrades orderTrades = response.getOrderTrades();
                log.info("Исполнено торговое поручение {} для счета {} по инструменту {}",
                        orderTrades.getOrderId(), orderTrades.getAccountId(), orderTrades.getFigi());
            }
        };

        ordersStreamService.subscribeTrades(processor, onErrorCallback, accountIds);
    }
}
