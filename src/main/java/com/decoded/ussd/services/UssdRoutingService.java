package com.decoded.ussd.services;

import java.io.IOException;

public interface UssdRoutingService {
    String menuLevelRouter(String sessionId, String serviceCode, String phoneNumber, String text)
            throws IOException;
}
