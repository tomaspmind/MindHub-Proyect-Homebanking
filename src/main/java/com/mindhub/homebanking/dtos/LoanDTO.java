package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.ClientLoan;
import com.mindhub.homebanking.models.Loan;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LoanDTO {

    private long id;

    private String name;

    private int maxAmount;

    private List<Integer> payment;

    private Double fee;

    private List<Double> feePayments;

    public LoanDTO(){}
    public LoanDTO(Loan loan) {
        this.id = loan.getId();
        this.name = loan.getName();
        this.maxAmount = loan.getMaxAmount();
        this.payment = loan.getPayment();
        this.fee = loan.getFee();
        this.feePayments = loan.getFeePayments();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public List<Integer> getPayment() {
        return payment;
    }

    public Double getFee() {
        return fee;
    }
    public List<Double> getFeePayments() {
        return feePayments;
    }
}
