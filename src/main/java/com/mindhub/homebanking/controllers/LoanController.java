package com.mindhub.homebanking.controllers;


import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.mindhub.homebanking.extras.Extras.loanFess;

@RestController
public class LoanController {

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    LoanRepository loanRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    ClientLoanRepository clientLoanRepository;

    @RequestMapping("/api/loans")
    public List<LoanDTO> getLoans(){
        return loanRepository.findAll().stream().map(loan -> new LoanDTO(loan)).collect(Collectors.toList());
    }

    @Transactional
    @RequestMapping(path = "/api/loans", method = RequestMethod.POST)
    public ResponseEntity<Object> newLoan (@RequestBody(required = false) LoanApplicationDTO loanApplicationDTO, Authentication authentication){

        Long id = loanApplicationDTO.getId();
        Double amount = loanApplicationDTO.getAmount();
        Integer payments = loanApplicationDTO.getPayments();
        String accountNumber = loanApplicationDTO.getAccountNumber();

        Client clientAutenticado = clientRepository.findByEmail(authentication.getName());
        Account accountDestino = accountRepository.findByNumber(accountNumber);
        Loan typeLoan = loanRepository.getReferenceById(id);

        if (id == null || id <= 0){
            return new ResponseEntity<>("Loan id is Empty",HttpStatus.FORBIDDEN);
        }
        if (amount == null || amount <= 0 ){
            return new ResponseEntity<>("Amount is Empty",HttpStatus.FORBIDDEN);
        }
        if (payments == null || payments <= 0){
            return new ResponseEntity<>("Payiment is Empty",HttpStatus.FORBIDDEN);
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

        ClientLoan clientLoan = new ClientLoan(loanFess(amount), payments, clientAutenticado,typeLoan);
        Transaction loanTransaction = new Transaction(TransactionType.CREDIT,amount,typeLoan.getName()+" Loan approve", LocalDateTime.now());

        typeLoan.addLoans(clientLoan);
        accountDestino.addTransaction(loanTransaction);
        accountDestino.setBalance(accountDestino.getBalance()+amount);

        transactionRepository.save(loanTransaction);
        clientLoanRepository.save(clientLoan);

        return new ResponseEntity<>("Loan accepted", HttpStatus.CREATED);
    }

}
