package com.decoded.ussd.services;

import com.decoded.ussd.data.UssdSession;

import java.io.IOException;

public interface TransactionService {
    void makeDeposit(UssdSession session) throws IOException;

    void withdraw(UssdSession session) throws IOException;
}
