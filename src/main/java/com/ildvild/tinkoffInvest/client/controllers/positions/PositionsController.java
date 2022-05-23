package com.ildvild.tinkoffInvest.client.controllers.positions;

import com.ildvild.tinkoffInvest.server.controllers.InstrumentsController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Slf4j
@Component("clientPositionsController")
@PropertySource(value = "classpath:client-config.properties")
public class PositionsController {

    private final com.ildvild.tinkoffInvest.server.controllers.InstrumentsController instrumentsController;

    @Value("${tickerIconFormatUrl}")
    private String tickerIconFormatUrl;

    public PositionsController(InstrumentsController instrumentsController) {
        this.instrumentsController = instrumentsController;
    }

    public Position convertPosition(ru.tinkoff.piapi.core.models.Position position) {
        ru.tinkoff.piapi.contract.v1.Instrument instrument = instrumentsController.getByFigi(position.getFigi());

        Position p = new Position();
        p.setInstrument(new Instrument(String.format(tickerIconFormatUrl, instrument.getIsin()),
                instrument.getTicker(), instrument.getFigi(), instrument.getName()));
        p.setQuantity(position.getQuantity());
        p.setCurrency(position.getCurrentPrice().getCurrency().getCurrencyCode());
        p.setAveragePositionPrice(position.getAveragePositionPriceFifo().getValue());
        p.setCurrentPrice(position.getCurrentPrice().getValue());
        p.setExpectedYield(position.getExpectedYield());
        p.setInstrumentType(position.getInstrumentType());

        return p;
    }
}
