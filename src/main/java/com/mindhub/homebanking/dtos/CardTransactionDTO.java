package com.mindhub.homebanking.dtos;

import java.time.LocalDate;

public class CardTransactionDTO {

    private Long id;
    private String number;
    private String cvv;
    private Double amount;
    private String description;
    private Short thruDateYear;
    private String thruDateMonth;

    public CardTransactionDTO(){}
    public CardTransactionDTO(Long id, String number, String cvv, Double amount, String description, Short thruDateYear, String thruDateMonth) {
        this.id = id;
        this.number = number;
        this.cvv = cvv;
        this.amount = amount;
        this.description = description;
        this.thruDateYear = thruDateYear;
        this.thruDateMonth = thruDateMonth;
    }

    public Long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public String getCvv() {
        return cvv;
    }

    public Double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public Short getThruDateYear() {
        return thruDateYear;
    }

    public String getThruDateMonth() {
        return thruDateMonth;
    }
}
