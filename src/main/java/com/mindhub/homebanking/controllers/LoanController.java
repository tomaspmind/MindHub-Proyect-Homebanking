package com.mindhub.homebanking.controllers;


import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.mindhub.homebanking.utils.Utils.currentBalanceCredit;
import static com.mindhub.homebanking.utils.Utils.loanFess;

@RestController
public class LoanController {

    @Autowired
    ClientService clientService;

    @Autowired
    LoanService loanService;

    @Autowired
    AccountService accountService;

    @Autowired
    TransactionService transactionService;

    @Autowired
    ClientLoanService clientLoanService;

    @GetMapping("/api/loans")
    public List<LoanDTO> getLoans(){
        return loanService.findAll().stream().map(loan -> new LoanDTO(loan)).collect(Collectors.toList());
    }

    @Transactional
    @PostMapping("/api/loans")
    public ResponseEntity<Object> newLoan (@RequestBody(required = false) LoanApplicationDTO loanApplicationDTO, Authentication authentication){

        Long id = loanApplicationDTO.getId();
        Double amount = loanApplicationDTO.getAmount();
        Integer payments = loanApplicationDTO.getPayments();
        String accountNumber = loanApplicationDTO.getAccountNumber();

        Client clientAutenticado = clientService.findByEmail(authentication.getName());
        Account accountDestino = accountService.findByNumber(accountNumber);
        Loan typeLoan = loanService.getReferenceById(id);

        if (id == null || id <= 0){
            return new ResponseEntity<>("Loan id is Empty",HttpStatus.FORBIDDEN);
        }
        if (amount == null || amount <= 0 ){
            return new ResponseEntity<>("Amount is Empty",HttpStatus.FORBIDDEN);
        }
        if (payments == null || payments <= 0){
            return new ResponseEntity<>("Payment is Empty",HttpStatus.FORBIDDEN);
        }
        if (accountNumber.isEmpty()){
            return new ResponseEntity<>("Account Destiny is Empty",HttpStatus.FORBIDDEN);
        }
        if (typeLoan == null){
            return new ResponseEntity<>("The loan is not exist",HttpStatus.FORBIDDEN);
        }
        if (typeLoan.getMaxAmount() < amount){
            return new ResponseEntity<>("Exceeded the max amount of the Loan",HttpStatus.FORBIDDEN);
        }
        if (!typeLoan.getPayment().contains(payments)){
            return new ResponseEntity<>("The opcion of payment is not exist",HttpStatus.FORBIDDEN);
        }
        if (accountDestino == null){
            return new ResponseEntity<>("The account Destiny is not exist",HttpStatus.FORBIDDEN);
        }
        if (!clientAutenticado.getAccounts().contains(accountDestino)){
            return new ResponseEntity<>("The account Destiny is not your account",HttpStatus.FORBIDDEN);
        }
        if (clientAutenticado.getClientLoans().stream().anyMatch(clientLoan -> clientLoan.getLoan() == typeLoan)){
            return new ResponseEntity<>("You cannot have another similar loan",HttpStatus.BAD_REQUEST);
        }

        ClientLoan clientLoan = new ClientLoan(loanFess(amount,typeLoan), payments, clientAutenticado,typeLoan);
        Transaction loanTransaction = new Transaction(TransactionType.CREDIT,amount,typeLoan.getName()+" Loan approve", LocalDateTime.now(),currentBalanceCredit(accountService,accountDestino,amount));

        typeLoan.addLoans(clientLoan);
        accountDestino.addTransaction(loanTransaction);
        accountDestino.setBalance(accountDestino.getBalance()+amount);

        transactionService.save(loanTransaction);
        clientLoanService.save(clientLoan);

        return new ResponseEntity<>("Loan accepted", HttpStatus.CREATED);
    }

    @PostMapping("/api/admin/loans")
    public ResponseEntity<Object> createLoans(@RequestBody Loan loan){

        String name = loan.getName();
        int maxAmount = loan.getMaxAmount();
        List<Integer> payment = loan.getPayment();
        Double fee = loan.getFee();
        List<Double> feePayments = loan.getFeePayments();
        Boolean show = true;

        Loan loans = new Loan(name,maxAmount,payment,fee,feePayments,show);

        loanService.save(loans);

        return new ResponseEntity<>("Created",HttpStatus.CREATED);

    }

}
