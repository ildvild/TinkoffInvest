package com.ildvild.tinkoffInvest.client.controllers.positions;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Position {
    private Instrument instrument;
    private BigDecimal quantity;
    private String currency;
    private BigDecimal averagePositionPrice;
    private BigDecimal currentPrice;
    private BigDecimal expectedYield;
    private String instrumentType;
}
