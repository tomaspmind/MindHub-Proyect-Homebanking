package com.mindhub.homebanking.services.implementation;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountServiceImplementation implements AccountService {

    @Autowired
    AccountRepository accountRepository;

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public Account getReferenceById(Long id) {
        return accountRepository.getReferenceById(id);
    }

    @Override
    public Account findByNumber(String number) {
        return accountRepository.findByNumber(number);
    }

    @Override
    public Boolean existsByNumber(String number) {
        return accountRepository.existsByNumber(number);
    }

    @Override
    public void save(Account account) {
        accountRepository.save(account);
    }
}
