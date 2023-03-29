package com.decoded.ussd.services;

import com.decoded.ussd.data.UssdSession;

import java.io.IOException;
import java.math.BigDecimal;

public interface AccountService {
    void createAccount(UssdSession session) throws IOException;
    boolean isUserRegistered(String phoneNumber);
    BigDecimal getAccountBalance(UssdSession session) throws IOException;
    String getAccountNumber(UssdSession session) throws IOException;
}
