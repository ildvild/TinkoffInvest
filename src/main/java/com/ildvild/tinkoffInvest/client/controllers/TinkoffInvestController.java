package com.ildvild.tinkoffInvest.client.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class TinkoffInvestController {

    @Value("#{'${figies}'.split(',')}")
    private List<String> figies;

    public List<String> getFigies() {
        return figies;
    }
}
