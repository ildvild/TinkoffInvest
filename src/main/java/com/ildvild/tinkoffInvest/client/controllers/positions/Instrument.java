package com.ildvild.tinkoffInvest.client.controllers.positions;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class Instrument {
    private String icon;
    private String ticker;
    private String name;
    private String figi;
}
