package com.ildvild.tinkoffInvest.server.robots.historic;

import com.ildvild.tinkoffInvest.server.robots.BuyAverageAndSell;
import com.ildvild.tinkoffInvest.server.robots.TradingPosition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "buyAverageAndSellTest", name = "enabled", havingValue = "true", matchIfMissing = false)
public class BuyAverageAndSellHistoric extends BuyAverageAndSell {

    private BigDecimal money;
    private Map<String, TradingPosition> tradingPositions = new HashMap<>();

    public BuyAverageAndSellHistoric(@Value("empty") String accountId) {
        super(accountId);
    }

    @Override
    public String getName() {
        return super.getName() + "Test";
    }

    @Override
    protected TradingPosition getTradingPosition(String figi) {
        return tradingPositions.get(figi);
    }

    @Override
    protected void buy(String figi, BigDecimal price) {
        log.info("Выставление лимитной заявки по счету {} на покупку {} в количестве {} по цене {}",
                accountId, figi, buyQuantity, price);
        TradingPosition position = tradingPositions.getOrDefault(figi,
                new TradingPosition(figi, 0, BigDecimal.ZERO));

        long newQuantity = position.getQuantity() + buyQuantity;
        position.setAveragePositionPrice(position.getAveragePositionPrice().multiply(BigDecimal.valueOf(position.getQuantity()))
                .add(price.multiply(BigDecimal.valueOf(buyQuantity)))
                .divide(BigDecimal.valueOf(newQuantity), 6, RoundingMode.HALF_DOWN));
        position.setQuantity(newQuantity);

        money = money.subtract(price.multiply(BigDecimal.valueOf(buyQuantity)));
        tradingPositions.put(figi, position);
    }

    @Override
    protected void buyMarket(String figi, BigDecimal price) {
        log.info("Выставление рыночной заявки по счету {} на покупку {} в количестве {} по цене {}",
                accountId, figi, buyQuantity, price);
        TradingPosition position = tradingPositions.getOrDefault(figi,
                new TradingPosition(figi, 0, BigDecimal.ZERO));

        long newQuantity = position.getQuantity() + buyQuantity;
        position.setAveragePositionPrice(position.getAveragePositionPrice().multiply(BigDecimal.valueOf(position.getQuantity()))
                .add(price.multiply(BigDecimal.valueOf(buyQuantity)))
                .divide(BigDecimal.valueOf(newQuantity), 6, RoundingMode.HALF_DOWN));
        position.setQuantity(newQuantity);

        money = money.subtract(price.multiply(BigDecimal.valueOf(buyQuantity)));
        tradingPositions.put(figi, position);
    }

    @Override
    protected void sell(String figi, BigDecimal price, long quantity) {
        if (tradingPositions.containsKey(figi)) {
            log.info("Выставление рыночной заявки по счету {} на продажу {} в количестве {} по цене {}",
                    accountId, figi, buyQuantity, price);

            TradingPosition position = tradingPositions.get(figi);
            money = money.add(price.multiply(BigDecimal.valueOf(position.getQuantity())));
            tradingPositions.remove(figi);
        }
    }


    @Override
    protected BigDecimal getPortfolioAmount(String figi, BigDecimal lastPrice) {
        BigDecimal result = BigDecimal.ZERO;
        for (TradingPosition tradingPosition : tradingPositions.values()) {
            result.add(lastPrice.multiply(BigDecimal.valueOf(tradingPosition.getQuantity())));
        }

        return result.add(money);
    }

    @Override
    public void initFreeRubMoney(BigDecimal value) {
        money = value;
    }

    @Override
    protected BigDecimal getFreeRubMoney() {
        return money;
    }
}
