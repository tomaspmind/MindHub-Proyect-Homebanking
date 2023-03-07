package com.mindhub.homebanking.controllers;

import antlr.StringUtils;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import static com.mindhub.homebanking.extras.Extras.number;

@RestController
public class TransactionController {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    AccountRepository accountRepository;

    @Transactional
    @RequestMapping(path = "/api/transactions", method = RequestMethod.POST)
    public ResponseEntity<Object> transactions(Authentication authentication,

            @RequestParam(required = false) Double amount, @RequestParam String description,
            @RequestParam String accountNumber , @RequestParam String accountNumberDestini) {

        Client clientAuthenticated = clientRepository.findByEmail(authentication.getName());
        Account originAccount = accountRepository.findByNumber(accountNumber);
        Account destinyAccount = accountRepository.findByNumber(accountNumberDestini);

        if (amount == null){
            return new ResponseEntity<>("Amount is Empty",HttpStatus.BAD_REQUEST);
        }
        if (description.isEmpty()){
            return new ResponseEntity<>("Description es Empty",HttpStatus.BAD_REQUEST);
        }
        if (accountNumber.isEmpty()){
            return new ResponseEntity<>("Account Number is Empty",HttpStatus.BAD_REQUEST);
        }
        if (accountNumberDestini.isEmpty()){
            return new ResponseEntity<>("Account Destiny is Empty",HttpStatus.BAD_REQUEST);
        }
        if (amount < 1){
            return new ResponseEntity<>("You cannot transfer an amount less than 1",HttpStatus.FORBIDDEN);
        }
        if (accountNumber == null){
            return new ResponseEntity<>("Account Number is not exist",HttpStatus.FORBIDDEN);
        }
        if (accountNumberDestini == null){
            return new ResponseEntity<>("Account Destiny is not exist",HttpStatus.FORBIDDEN);
        }
        if (accountNumber.equals(accountNumberDestini)){
            return new ResponseEntity<>("The number Accounts is the same",HttpStatus.BAD_REQUEST);
        }
        if (!accountRepository.existsByNumber(accountNumber)){
            return new ResponseEntity<>("Account is not exist",HttpStatus.FORBIDDEN);
        }
        if (!clientAuthenticated.getAccounts().stream().anyMatch(account -> account.getNumber().equals(accountNumber))){
            return new ResponseEntity<>("This account does not belong to the user",HttpStatus.FORBIDDEN);
        }
        if (accountRepository.findByNumber(accountNumberDestini) == null){
            return new ResponseEntity<>("The account destiny is does exist",HttpStatus.FORBIDDEN);
        }
        if (originAccount.getBalance() < amount){
            return new ResponseEntity<>("You dont have enough money for that",HttpStatus.BAD_REQUEST);
        }

        Transaction transactionOrigin = new Transaction(TransactionType.DEBIT, amount,description,LocalDateTime.now());
        Transaction transactionDestiny = new Transaction(TransactionType.CREDIT,amount,description,LocalDateTime.now());
        originAccount.addTransaction(transactionOrigin);
        destinyAccount.addTransaction(transactionDestiny);
        transactionRepository.save(transactionOrigin);
        transactionRepository.save(transactionDestiny);
        originAccount.setBalance(originAccount.getBalance() - transactionOrigin.getAmount());
        destinyAccount.setBalance(destinyAccount.getBalance() + transactionDestiny.getAmount());
        accountRepository.save(originAccount);
        accountRepository.save(destinyAccount);

        return new ResponseEntity<>("Transaction was successfully",HttpStatus.CREATED);
    }
}
