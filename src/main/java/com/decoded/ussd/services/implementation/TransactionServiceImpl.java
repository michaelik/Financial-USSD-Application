package com.decoded.ussd.services.implementation;

import com.decoded.ussd.data.Account;
import com.decoded.ussd.data.Transaction;
import com.decoded.ussd.data.UssdSession;
import com.decoded.ussd.enums.TransactionType;
import com.decoded.ussd.repositories.AccountRepository;
import com.decoded.ussd.repositories.TransactionRepository;
import com.decoded.ussd.services.SessionService;
import com.decoded.ussd.services.TransactionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final SessionService sessionService;
    private Set<String> userInputs;

    @SuppressWarnings(value = "OptionalGetWithoutIsPresent")
    @Override
    public void makeDeposit(UssdSession session) throws IOException {
        ArrayList<String> list;
        this.userInputs.add(session.getText());
        if (this.userInputs.size() == 2) {
            list = new ArrayList<>(this.userInputs);
            String accountNumber = list.get(0);
            BigDecimal amount = BigDecimal.valueOf(Long.parseLong(list.get(1)));
            Account depositAccount = accountRepository.findAccountByAccountNumber(accountNumber);
            Account debitAccount = accountRepository.findAccountByPhoneNumber(session.getPhoneNumber()).get();
            if (!debitAccount.equals(depositAccount)) {
                saveTransaction(depositAccount, amount, TransactionType.DEPOSIT);
                setDepositAndDebitAmount(depositAccount, debitAccount, amount);
                this.userInputs.clear();
            }else {
                sessionService.delete(session.getId());
                this.userInputs.clear();
                throw new IOException("Can not deposit to self");
            }
        }
    }

    @Override
    public void withdraw(UssdSession session) throws IOException {
        ArrayList<String> list;
        this.userInputs.add(session.getText());
        if (this.userInputs.size() == 1) {
            log.info("input size = {}", this.userInputs.size());
            list = new ArrayList<>(this.userInputs);
            BigDecimal amount = BigDecimal.valueOf(Long.parseLong(list.get(0)));
            Account account = accountRepository.findAccountByPhoneNumber(session.getPhoneNumber())
                    .orElseThrow(() ->
                            new IOException("Please create an account"));
            if (account.getAccountBalance().compareTo(amount) <= 0) {
                sessionService.delete(session.getId());
                this.userInputs.clear();
                throw new IOException("Insufficient Funds");
            }
            saveTransaction(account, amount, TransactionType.WITHDRAW);
            BigDecimal totalAmount = account.getAccountBalance().subtract(amount);
            account.setAccountBalance(totalAmount);
            accountRepository.save(account);
            this.userInputs.clear();
        }
    }

    private void saveTransaction(Account account, BigDecimal amount, TransactionType type) {
        Transaction transaction = Transaction.builder()
                        .amount(amount)
                        .transactionType(type)
                        .account(account)
                        .build();
        transactionRepository.save(transaction);
    }

    private void setDepositAndDebitAmount(Account depositAccount, Account debitAccount, BigDecimal amount){
        BigDecimal depositAmount = depositAccount.getAccountBalance().add(amount);
        depositAccount.setAccountBalance(depositAmount);
        BigDecimal debitAmount = debitAccount.getAccountBalance().subtract(amount);
        debitAccount.setAccountBalance(debitAmount);
        accountRepository.save(debitAccount);
        accountRepository.save(depositAccount);
    }
}
