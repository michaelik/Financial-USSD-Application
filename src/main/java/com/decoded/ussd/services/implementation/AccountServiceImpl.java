package com.decoded.ussd.services.implementation;

import com.decoded.ussd.data.Account;
import com.decoded.ussd.data.Transaction;
import com.decoded.ussd.data.UssdSession;
import com.decoded.ussd.repositories.AccountRepository;
import com.decoded.ussd.repositories.TransactionRepository;
import com.decoded.ussd.services.AccountService;
import com.decoded.ussd.services.SessionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final SessionService sessionService;
    private Set<String> userInputs;

    @Override
    public void createAccount(UssdSession session) throws IOException {
        ArrayList<String> list;
        this.userInputs.add(session.getText());
        if (this.userInputs.size() == 1) {
            list = new ArrayList<>(this.userInputs);
            if (Objects.equals(session.getCurrentMenuLevel(), "3") &&
                    Objects.equals(list.get(0), session.getText())) {
                firstNameNotEqualsLastName(session);
            }
        }
        if (this.userInputs.size() == 3) {
            list = new ArrayList<>(this.userInputs);
            String generatedAN = generateTenDigitAccountNumber();
            if (accountRepository.existsByAccountNumber(generatedAN))
                generatedAN = generateTenDigitAccountNumber();
            Account accountUser = Account.builder()
                    .firstname(list.get(0))
                    .lastname(list.get(1))
                    .email(list.get(2))
                    .phoneNumber(session.getPhoneNumber())
                    .accountNumber(generatedAN)
                    .build();
            Transaction transaction = Transaction.builder()
                    .account(accountUser)
                    .build();
            accountUser.setTransaction(transaction);
            accountRepository.save(accountUser);
            transactionRepository.save(transaction);
            this.userInputs.clear();
        }
    }

    @Override
    public boolean isUserRegistered(String phoneNumber){
        return accountRepository.findByPhoneNumber(phoneNumber).isPresent();
    }

    @Override
    public BigDecimal getAccountBalance(UssdSession session) throws IOException {
        Account user = accountRepository.findByPhoneNumber(session.getPhoneNumber())
                .orElseThrow(() ->
                        new IOException("--Please create an account"));
        return user.getAccountBalance();
    }

    @Override
    public String getAccountNumber(UssdSession session) throws IOException {
        Account user = accountRepository.findByPhoneNumber(session.getPhoneNumber())
                .orElseThrow(() ->
                new IOException("Please create an account"));
        return user.getAccountNumber();
    }

    private String generateTenDigitAccountNumber(){
        return IntStream.generate(() -> new Random().nextInt(10))
                .limit(10)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());
    }

    private void firstNameNotEqualsLastName(UssdSession session) throws IOException {
            sessionService.delete(session.getId());
            this.userInputs.clear();
            throw new IOException("first name and last name can not be the same");
    }
}
