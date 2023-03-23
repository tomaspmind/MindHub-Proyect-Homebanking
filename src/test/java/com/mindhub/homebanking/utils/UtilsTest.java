package com.mindhub.homebanking.utils;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.services.AccountService;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UtilsTest {

    @Test
    void genericNumber() {
        String number = Utils.genericNumber();
        assertThat(number, not(emptyString()));
    }

    @Test
    void number() {
        AccountRepository accountRepository = mock(AccountRepository.class); // Creamos un mock de AccountRepository
        String number = Utils.genericNumber();
        boolean verify = accountRepository.existsByNumber(number);
        assertFalse(verify);
        assertThat(number, is(notNullValue()));
    }

    @Test
    void loanFess() {
        Loan loan = new Loan("LoanTest",100000,List.of(10,15,20),1.10,List.of(1.0,1.05,1.10),true);
        Double loanFees = Utils.loanFess(500.0, loan);
        assertThat(loanFees, is(550.0));
    }

    @Test
    void cvv() {
        String number = Utils.cvv();
        assertThat(number, not(emptyString()));
    }

    @Test
    void numbers() {
        String number = Utils.numbers();
        assertThat(number, not(emptyString()));
    }

    @Test
    void noDuplicatedNumberCard(){
        CardRepository cardRepository = mock(CardRepository.class);
        String number = Utils.numbers();
        boolean verify = cardRepository.existsByNumber(number);
        assertFalse(verify);
        assertThat(number, is(notNullValue()));
    }

    @Test
    void currentBalanceCredit() {
        AccountService accountService = mock(AccountService.class);
        List<Account> accounts = accountService.findAll();
        Transaction transaction = new Transaction(TransactionType.CREDIT,500.0,"Transfer test",LocalDateTime.now(),500.0);
        for (Account account : accounts){
            Double currentBalance = Utils.currentBalanceCredit(accountService,account,transaction.getAmount());
            assertThat(currentBalance, is(1000.0));
        }
    }

    @Test
    void currentBalanceDebit() {
        AccountService accountService = mock(AccountService.class);
        List<Account> accounts = accountService.findAll();
        Transaction transaction = new Transaction(TransactionType.DEBIT,500.0,"Transfer test",LocalDateTime.now(),500.0);
        for (Account account : accounts){
            Double currentBalance = Utils.currentBalanceCredit(accountService,account,transaction.getAmount());
            assertThat(currentBalance, is(0.0));
        }
    }
}