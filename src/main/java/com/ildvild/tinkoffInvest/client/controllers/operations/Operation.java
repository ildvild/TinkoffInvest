package com.ildvild.tinkoffInvest.client.controllers.operations;

import com.ildvild.tinkoffInvest.client.controllers.positions.Instrument;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Operation {
    private Instrument instrument;
    private String state;
    private String currency;
    private String lastBuyDate;
    private String dividendType;
}
