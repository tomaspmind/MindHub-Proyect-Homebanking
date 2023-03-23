package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.Loan;

import java.util.List;

public interface LoanService {

    List<Loan> findAll();

    Loan getReferenceById(Long id);

    void save(Loan loan);
}
