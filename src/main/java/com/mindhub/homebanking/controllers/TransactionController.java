package com.mindhub.homebanking.controllers;

//import com.mindhub.homebanking.Services.CreatePDF;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.CreatePDF;
import com.mindhub.homebanking.dtos.TransactionDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;

import com.mindhub.homebanking.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mindhub.homebanking.utils.Utils.*;

@RestController
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @Autowired
    ClientService clientService;

    @Autowired
    AccountService accountService;

    @Autowired
    CreatePDF createPDF;

    @Transactional
    @PostMapping("/api/transactions")
    public ResponseEntity<Object> transactions(Authentication authentication,

            @RequestParam(required = false) Double amount, @RequestParam String description,
            @RequestParam String accountNumber , @RequestParam String accountNumberDestini) {

        Client clientAuthenticated = clientService.findByEmail(authentication.getName());
        Account originAccount = accountService.findByNumber(accountNumber);
        Account destinyAccount = accountService.findByNumber(accountNumberDestini);

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
        if (!accountService.existsByNumber(accountNumber)){
            return new ResponseEntity<>("Account is not exist",HttpStatus.FORBIDDEN);
        }
        if (!clientAuthenticated.getAccounts().stream().anyMatch(account -> account.getNumber().equals(accountNumber))){
            return new ResponseEntity<>("This account does not belong to the user",HttpStatus.FORBIDDEN);
        }
        if (accountService.findByNumber(accountNumberDestini) == null){
            return new ResponseEntity<>("The account destiny is does exist",HttpStatus.FORBIDDEN);
        }
        if (originAccount.getBalance() < amount){
            return new ResponseEntity<>("You dont have enough money for that",HttpStatus.BAD_REQUEST);
        }

        Transaction transactionOrigin = new Transaction(TransactionType.DEBIT, amount,description+" "+ originAccount.getNumber(),LocalDateTime.now(),currentBalanceDebit(accountService,originAccount,amount));
        Transaction transactionDestiny = new Transaction(TransactionType.CREDIT,amount,description+" "+ destinyAccount.getNumber(),LocalDateTime.now(),currentBalanceCredit(accountService,destinyAccount,amount));
        originAccount.addTransaction(transactionOrigin);
        destinyAccount.addTransaction(transactionDestiny);
        transactionService.save(transactionOrigin);
        transactionService.save(transactionDestiny);
        originAccount.setBalance(originAccount.getBalance() - transactionOrigin.getAmount());
        destinyAccount.setBalance(destinyAccount.getBalance() + transactionDestiny.getAmount());
        accountService.save(originAccount);
        accountService.save(destinyAccount);

        return new ResponseEntity<>("Transaction was successfully",HttpStatus.CREATED);
    }

    @GetMapping("/api/clients/current/transactions")
    public ResponseEntity<?> listOfTransactionsPerDate(HttpServletResponse httpServletResponse,
                                                       @RequestParam String fromAccount,
                                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                       @RequestParam (required = false) String description,
                                                       @RequestParam (required = false) Double maxAmount,
                                                       @RequestParam (required = false) Double minAmount,
                                                       @RequestParam (required = false) TransactionType type,
                                                       Authentication authentication) throws Exception{

        Account currentAccount = accountService.findByNumber(fromAccount);
        Set<Transaction> transaccionesFiltradas = currentAccount.getTransactions();

        if (startDate == null && endDate != null){
            transaccionesFiltradas = transaccionesFiltradas.stream().filter(transaction -> transaction.getDate().isBefore(endDate) || transaction.getDate().equals(endDate) ).collect(Collectors.toSet());
        }
        if (startDate != null && endDate == null){
            transaccionesFiltradas = transaccionesFiltradas.stream().filter(transaction -> transaction.getDate().isAfter(startDate) || transaction.getDate().equals(startDate)).collect(Collectors.toSet());
        }
        if (startDate != null && endDate != null && startDate.isBefore(endDate)){
            transaccionesFiltradas = transaccionesFiltradas.stream().filter(transaction -> transaction.getDate().isAfter(startDate)  && transaction.getDate().isBefore(endDate) || transaction.getDate().equals(startDate) || transaction.getDate().equals(endDate)).collect(Collectors.toSet());
        }
        if (description != null && !description.isEmpty()){
            transaccionesFiltradas = transaccionesFiltradas.stream().filter(transaction -> transaction.getDescription().contains(description)).collect(Collectors.toSet());
        }
        if(maxAmount != null && !maxAmount.isNaN() && minAmount == null){
            transaccionesFiltradas = transaccionesFiltradas.stream().filter(transaction -> transaction.getAmount() <= maxAmount).collect(Collectors.toSet());
        }
        if(minAmount != null && !minAmount.isNaN() && maxAmount == null){
            transaccionesFiltradas = transaccionesFiltradas.stream().filter(transaction -> transaction.getAmount() >= minAmount).collect(Collectors.toSet());
        }
        if(maxAmount != null && !maxAmount.isNaN() && minAmount != null && !minAmount.isNaN()){
            transaccionesFiltradas = transaccionesFiltradas.stream().filter(transaction -> transaction.getAmount() >= minAmount && transaction.getAmount() <= maxAmount).collect(Collectors.toSet());
        }
        if(type != null && type == TransactionType.CREDIT){
            transaccionesFiltradas = transaccionesFiltradas.stream().filter(transaction -> transaction.getType() == TransactionType.CREDIT).collect(Collectors.toSet());
        }
        if (type !=null && type == TransactionType.DEBIT){
            transaccionesFiltradas =transaccionesFiltradas.stream().filter(transaction -> transaction.getType() == TransactionType.DEBIT).collect(Collectors.toSet());
        }

        Set<TransactionDTO> guardado = transaccionesFiltradas.stream().map(TransactionDTO::new).sorted((b, a)-> b.getDate().compareTo(a.getDate())).collect(Collectors.toSet());

        httpServletResponse.setContentType("application/pdf");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateTime = dateFormat.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=pdf_" + currentDateTime + ".pdf";
        httpServletResponse.setHeader(headerKey, headerValue);

        Set<TransactionDTO> pdfTransactions = guardado;

        this.createPDF.export(httpServletResponse);

        return ResponseEntity.ok(transaccionesFiltradas.stream().map(TransactionDTO::new).sorted((b, a)-> b.getDate().compareTo(a.getDate())).collect(Collectors.toSet()));
    }



}

