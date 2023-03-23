package com.mindhub.homebanking.services.implementation;

import com.mindhub.homebanking.models.Loan;
import com.mindhub.homebanking.repositories.LoanRepository;
import com.mindhub.homebanking.services.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoanServiceImplementation implements LoanService {

    @Autowired
    LoanRepository loanRepository;
    @Override
    public List<Loan> findAll() {
        return loanRepository.findAll();
    }

    @Override
    public Loan getReferenceById(Long id) {
        return loanRepository.getReferenceById(id);
    }

    @Override
    public void save(Loan loan) {
        loanRepository.save(loan);
    }
}
