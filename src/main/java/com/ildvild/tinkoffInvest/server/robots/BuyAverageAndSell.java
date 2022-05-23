package com.ildvild.tinkoffInvest.server.robots;

import com.ildvild.tinkoffInvest.server.robots.exceptions.RobotStateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import ru.tinkoff.piapi.contract.v1.Candle;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;
import ru.tinkoff.piapi.contract.v1.Instrument;
import ru.tinkoff.piapi.core.models.Portfolio;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static ru.tinkoff.piapi.core.utils.MapperUtils.quotationToBigDecimal;

@Slf4j
@Component
@PropertySource(value = "classpath:robots-config.properties")
@ConditionalOnProperty(prefix = "buyAverageAndSell", name = "enabled", havingValue = "true", matchIfMissing = true)
public class BuyAverageAndSell extends AbstractRobot {

    public static final String ROBOT_ID = "9219a100-aebd-4fb1-a944-edf7ce7067db";

    @Value("${buyAverageAndSell.buy-quantity:1}")
    protected long buyQuantity;
    @Value("${buyAverageAndSell.buy-percent:10}")
    private int buyPercent;
    @Value("${buyAverageAndSell.sell-percent:5}")
    private int sellPercent;

    private Map<String, TradingPosition> tradingPositions = new HashMap<>();

    public BuyAverageAndSell(@Value("${buyAverageAndSell.accountId}") String accountId) {
        super(accountId);
    }

    protected void loadTradingPosition() {
        Portfolio portfolio = serverOperationsController.getPortfolio(accountId);
        portfolio.getPositions().forEach(p -> tradingPositions.put(p.getFigi(),
                new TradingPosition(p.getFigi(), p.getQuantity().longValue(), p.getAveragePositionPrice().getValue())));
    }

    @Override
    public String getName() {
        return "Купить, усреднить, продать";
    }

    @Override
    public UUID getId() {
        return UUID.fromString(ROBOT_ID);
    }

    @Override
    public void processCandle(String figi, Candle candle) throws RobotStateException {
        super.processCandle(figi, candle);

        Instrument instrument = instrumentsController.getByFigi(figi);
        if (checkFigi(figi, instrument)) {
            boolean orderIsExist = orderIsExist(figi);
            if (!orderIsExist) {
                TradingPosition tradingPosition = getTradingPosition(figi);
                if (tradingPosition != null) {
                    BigDecimal averagePosition = tradingPosition.getAveragePositionPrice();
                    BigDecimal maxValue = quotationToBigDecimal(candle.getHigh());
                    BigDecimal minValue = quotationToBigDecimal(candle.getLow());
                    BigDecimal priceToBuy = averagePosition.multiply(BigDecimal.valueOf((100 - buyPercent) / 100.0));
                    BigDecimal priceToSell = averagePosition.multiply(BigDecimal.valueOf((100 + sellPercent) / 100.0));

                    if (maxValue.compareTo(priceToSell) > 0) {
                        sell(figi, priceToSell, tradingPosition.getQuantity());
                    } else if (minValue.compareTo(priceToBuy) < 0) {
                        buy(figi, priceToBuy);
                    }

                } else {
                    BigDecimal price = quotationToBigDecimal(candle.getOpen());
                    buyMarket(figi, price);
                }
            }
        } else {
            log.warn("Операции с {} не поддерживаются роботом {}", figi, getName());
        }
    }


    @Override
    public void processCandle(String figi, HistoricCandle candle) throws RobotStateException {
        super.processCandle(figi, candle);
        if (!checkHistoricCandle(candle)) {
            log.warn("Неполные данные в свече: {}", candle);
            return;
        }

        Instrument instrument = instrumentsController.getByFigi(figi);
        if (checkFigi(figi, instrument)) {
            boolean orderIsExist = orderIsExist(figi);
            if (!orderIsExist) {
                TradingPosition tradingPosition = getTradingPosition(figi);
                if (tradingPosition != null) {
                    BigDecimal averagePosition = tradingPosition.getAveragePositionPrice();
                    BigDecimal maxValue = quotationToBigDecimal(candle.getHigh());
                    BigDecimal minValue = quotationToBigDecimal(candle.getLow());
                    BigDecimal priceToBuy = averagePosition.multiply(BigDecimal.valueOf((100 - buyPercent) / 100.0));
                    BigDecimal priceToSell = averagePosition.multiply(BigDecimal.valueOf((100 + sellPercent) / 100.0));

                    if (maxValue.compareTo(priceToSell) > 0) {
                        sell(figi, priceToSell, tradingPosition.getQuantity());
                    } else if (minValue.compareTo(priceToBuy) < 0) {
                        buy(figi, priceToBuy);
                    }

                } else {
                    BigDecimal price = quotationToBigDecimal(candle.getOpen());
                    buyMarket(figi, price);
                }
            }
        } else {
            log.warn("Операции с {} не поддерживаются роботом {}", figi, getName());
        }
    }

    private boolean checkHistoricCandle(HistoricCandle candle) {
        return candle.hasTime() && candle.hasOpen() && candle.hasClose()
                && candle.hasHigh() && candle.hasLow();
    }

    private boolean checkFigi(String figi, Instrument instrument) {
        return (instrument != null && "share".equals(instrument.getInstrumentType()) && "rub".equals(instrument.getCurrency()));
    }

    protected TradingPosition getTradingPosition(String figi) {
        loadTradingPosition();
        return tradingPositions.get(figi);
    }

    private boolean orderIsExist(String figi) {
        return ordersController.getOrders(accountId).stream().anyMatch(o -> figi.equals(o.getFigi()));
    }

    protected void buy(String figi, BigDecimal price) {
        int lot = instrumentsController.getByFigi(figi).getLot();
        ordersController.postBuyOrder(accountId, figi, buyQuantity * lot,
                price.multiply(BigDecimal.valueOf(lot)));
    }

    protected void buyMarket(String figi, BigDecimal price) {
        int lot = instrumentsController.getByFigi(figi).getLot();
        ordersController.postBuyMarketOrder(accountId, figi, buyQuantity * lot,
                price.multiply(BigDecimal.valueOf(lot)));
    }

    protected void sell(String figi, BigDecimal price, long quantity) {
        int lot = instrumentsController.getByFigi(figi).getLot();
        ordersController.postSellOrder(accountId, figi, quantity * lot,
                price.multiply(BigDecimal.valueOf(lot)));
    }

    protected BigDecimal getPortfolioAmount(String figi, BigDecimal lastPrice) {
        return null;
    }
}
