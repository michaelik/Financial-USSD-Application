package com.decoded.ussd.repositories;

import com.decoded.ussd.data.Account;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AccountRepository extends CrudRepository<Account, Integer> {
    boolean existsByAccountNumber(String generatedAN);
    Optional<Account> findByPhoneNumber(String phoneNumber);
    Account findAccountByAccountNumber(String accountNumber);
    Optional<Account> findAccountByPhoneNumber(String phoneNumber);
}
