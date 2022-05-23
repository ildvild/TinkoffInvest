package com.ildvild.tinkoffInvest.client.controllers.dividends;

import com.ildvild.tinkoffInvest.client.controllers.positions.Instrument;
import com.ildvild.tinkoffInvest.server.controllers.InstrumentsController;
import com.ildvild.tinkoffInvest.server.controllers.TinkoffInvestController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.ildvild.tinkoffInvest.client.views.common.FormatHelper.getDateFormat;
import static ru.tinkoff.piapi.core.utils.DateUtils.timestampToInstant;
import static ru.tinkoff.piapi.core.utils.MapperUtils.moneyValueToBigDecimal;

@Slf4j
@Component("clientDividendsController")
@PropertySource(value = "classpath:client-config.properties")
public class DividendsController {

    private final com.ildvild.tinkoffInvest.server.controllers.DividendsController serverDividendsController;
    private final com.ildvild.tinkoffInvest.server.controllers.InstrumentsController instrumentsController;
    private final TinkoffInvestController tinkoffInvestController;

    @Value("${tickerIconFormatUrl}")
    private String tickerIconFormatUrl;

    public DividendsController(com.ildvild.tinkoffInvest.server.controllers.DividendsController serverDividendsController,
                               InstrumentsController instrumentsController,
                               TinkoffInvestController tinkoffInvestController) {
        this.serverDividendsController = serverDividendsController;
        this.instrumentsController = instrumentsController;
        this.tinkoffInvestController = tinkoffInvestController;
    }

    private Dividend convert(ru.tinkoff.piapi.contract.v1.Instrument instrument, ru.tinkoff.piapi.contract.v1.Dividend dividend) {
        Dividend d = new Dividend();
        d.setInstrument(new Instrument(String.format(tickerIconFormatUrl, instrument.getIsin()),
                instrument.getTicker(), instrument.getFigi(), instrument.getName()));
        d.setDividendNet(moneyValueToBigDecimal(dividend.getDividendNet()));
        d.setCurrency(dividend.getDividendNet().getCurrency());
        d.setDividendType(dividend.getDividendType());
        d.setLastBuyDate(getDateFormat().format(Date.from(timestampToInstant(dividend.getLastBuyDate()))));

        return d;
    }

    public List<Dividend> getDividendsByDefaultFigies() {
        List<Dividend> result = new ArrayList<>();
        tinkoffInvestController.getFigies().forEach(figi ->
                {
                    ru.tinkoff.piapi.contract.v1.Instrument instrument = instrumentsController.getByFigi(figi);
                    List<ru.tinkoff.piapi.contract.v1.Dividend> dividendList = serverDividendsController.getByFigi(figi);
                    for (ru.tinkoff.piapi.contract.v1.Dividend dividend : dividendList) {
                        result.add(convert(instrument, dividend));
                    }
                }
        );
        return result;
    }
}
