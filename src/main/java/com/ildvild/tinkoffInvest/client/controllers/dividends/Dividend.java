package com.ildvild.tinkoffInvest.client.controllers.dividends;

import com.ildvild.tinkoffInvest.client.controllers.positions.Instrument;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Dividend {
    private Instrument instrument;
    private BigDecimal dividendNet;
    private String currency;
    private String lastBuyDate;
    private String dividendType;
}
