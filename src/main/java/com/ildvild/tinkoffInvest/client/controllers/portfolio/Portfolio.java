package com.ildvild.tinkoffInvest.client.controllers.portfolio;

import com.ildvild.tinkoffInvest.client.controllers.positions.Position;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class Portfolio {
    private BigDecimal expectedYield;
    private List<Position> positions;
}
