package com.ildvild.tinkoffInvest.client.controllers.operations;

import com.ildvild.tinkoffInvest.client.controllers.positions.Instrument;
import com.ildvild.tinkoffInvest.server.controllers.InstrumentsController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component("clientOperationsController")
@PropertySource(value = "classpath:client-config.properties")
public class OperationsController {

    private final com.ildvild.tinkoffInvest.server.controllers.OperationsController serverOperationsController;

    private final InstrumentsController instrumentsController;

    @Value("${tickerIconFormatUrl}")
    private String tickerIconFormatUrl;

    public OperationsController(com.ildvild.tinkoffInvest.server.controllers.OperationsController serverOperationsController,
                                InstrumentsController instrumentsController) {
        this.serverOperationsController = serverOperationsController;
        this.instrumentsController = instrumentsController;
    }

    private Operation convert(ru.tinkoff.piapi.contract.v1.Operation operation) {
        ru.tinkoff.piapi.contract.v1.Instrument instrument = instrumentsController.getByFigi(operation.getFigi());

        Operation o = new Operation();
        o.setInstrument(new Instrument(String.format(tickerIconFormatUrl, instrument.getIsin()),
                instrument.getTicker(), instrument.getFigi(), instrument.getName()));
        return o;
    }

    public List<Operation> getOperations(String accountId, Instant from) {
        return serverOperationsController.getOperations(accountId, from).stream()
                .map(this::convert).collect(Collectors.toList());
    }
}
