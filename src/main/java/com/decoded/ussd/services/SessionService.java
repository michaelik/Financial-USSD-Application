package com.decoded.ussd.services;

import com.decoded.ussd.data.UssdSession;

public interface SessionService {
    UssdSession createUssdSession(UssdSession session);
    UssdSession get(String id);
    UssdSession update(UssdSession session);
    void delete(String id);
}
