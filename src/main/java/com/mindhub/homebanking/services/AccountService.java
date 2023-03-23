package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.Account;

import java.util.List;

public interface AccountService {

    List<Account> findAll();

    Account getReferenceById(Long id);

    Account findByNumber(String number);

    Boolean existsByNumber(String number);

    void save(Account account);
}
