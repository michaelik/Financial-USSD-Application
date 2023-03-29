package com.decoded.ussd.services.implementation;

import com.decoded.ussd.data.UssdSession;
import com.decoded.ussd.repositories.UssdSessionRepository;
import com.decoded.ussd.services.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final UssdSessionRepository ussdSessionRepository;

    @Override
    public UssdSession createUssdSession(UssdSession session) {
        return ussdSessionRepository.save(session);
    }

    @Override
    public UssdSession get(String id) {
        return ussdSessionRepository.findById(id).orElse(null);
    }

    @Override
    public UssdSession update(UssdSession session) {
        if (ussdSessionRepository.existsById(session.getId())) {
            return ussdSessionRepository.save(session);
        }
        throw new IllegalArgumentException("A session must have an id to be updated");
    }

    @Override
    public void delete(String id) {
        ussdSessionRepository.deleteById(id);
    }
}
