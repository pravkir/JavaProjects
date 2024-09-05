package com.dws.challenge.domain;

import java.math.BigDecimal;

public class TransferRequest {
    private String accountFromId;
    private String accountToId;
    private BigDecimal amount;

    // Getters and Setters

    public String getAccountFromId() {
        return accountFromId;
    }

    public void setAccountFromId(String accountFromId) {
        this.accountFromId = accountFromId;
    }

    public String getAccountToId() {
        return accountToId;
    }

    public void setAccountToId(String accountToId) {
        this.accountToId = accountToId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}