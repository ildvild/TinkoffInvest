package com.ildvild.tinkoffInvest.client.controllers.robots;

import lombok.Getter;

public enum State {
    WORKING("В работе"),
    STOP("Остановлен");

    @Getter
    private final String name;

    State(String name) {
        this.name = name;
    }
}
