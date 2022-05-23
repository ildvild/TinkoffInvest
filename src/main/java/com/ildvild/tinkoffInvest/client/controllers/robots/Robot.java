package com.ildvild.tinkoffInvest.client.controllers.robots;

import lombok.Data;

import java.util.UUID;

@Data
public class Robot {
    private UUID id;
    private String accountId;
    private String name;
    private String description;
    private State state;

    public boolean isWorking() {
        return state == State.WORKING;
    }

    public boolean isStopped() {
        return state == State.STOP;
    }
}
