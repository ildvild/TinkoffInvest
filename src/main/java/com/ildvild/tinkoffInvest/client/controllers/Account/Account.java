package com.ildvild.tinkoffInvest.client.controllers.Account;

import lombok.Data;

import java.time.Instant;

@Data
public class Account {
    private String name;
    private String id;
    private int status;
    private Instant openedDate;
}
