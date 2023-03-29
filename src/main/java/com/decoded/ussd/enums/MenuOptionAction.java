package com.decoded.ussd.enums;

public enum MenuOptionAction {
    PROCESS_USER_ACCOUNT_CREATION("PROCESS_USER_ACCOUNT_CREATION"),
    PROCESS_USER_DEPOSIT_AMOUNT("PROCESS_USER_DEPOSIT_AMOUNT"),
    PROCESS_USER_WITHDRAW_AMOUNT("PROCESS_USER_WITHDRAW_AMOUNT"),
    PROCESS_USER_ACCOUNT_BALANCE("PROCESS_USER_ACCOUNT_BALANCE");

    private final String action;
    MenuOptionAction(String action) {
        this.action = action;
    }
}
