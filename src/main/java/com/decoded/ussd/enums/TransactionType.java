package com.decoded.ussd.enums;

public enum TransactionType {
    DEPOSIT("DEPOSIT"),
    WITHDRAW("WITHDRAW");

    private final String action;
    TransactionType(String action) {
        this.action = action;
    }
}
