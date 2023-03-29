package com.decoded.ussd.repositories;

import com.decoded.ussd.data.Transaction;
import org.springframework.data.repository.CrudRepository;


public interface TransactionRepository extends CrudRepository<Transaction, Integer> {
}
