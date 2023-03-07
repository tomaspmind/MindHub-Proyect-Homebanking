package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.ClientLoan;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

public class LoanApplicationDTO {

    private long id;

    private Double amount;

    private Integer payments;

    private String accountNumber;

    public LoanApplicationDTO(){}

    public LoanApplicationDTO(long id, Double amount, Integer payments, String accountNumber) {
        this.id = id;
        this.amount = amount;
        this.payments = payments;
        this.accountNumber = accountNumber;
    }

    public long getId() {
        return id;
    }

    public Double getAmount() {
        return amount;
    }

    public Integer getPayments() {
        return payments;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}
