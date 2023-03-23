package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Loan;

import java.util.List;

public interface ClientService {

    List<Client> findAll();

    Client findById(Long id);

    Client findByEmail(String email);

    void save (Client client);
}
