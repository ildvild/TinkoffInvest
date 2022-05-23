package com.ildvild.tinkoffInvest.server.robots;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TradingPosition {
    private String figi;
    private long quantity;
    private BigDecimal averagePositionPrice ;
}
