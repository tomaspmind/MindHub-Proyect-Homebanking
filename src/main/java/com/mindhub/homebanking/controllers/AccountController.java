package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.mindhub.homebanking.extras.Extras.number;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ClientRepository clientRepository;

    @RequestMapping("/accounts")
    public  List<AccountDTO> getAccounts() {
        return accountRepository.findAll().stream().map(AccountDTO::new).collect(toList());
    }

    @RequestMapping("/accounts/{id}")
    public AccountDTO getAccounts(@PathVariable Long id) {
        return new AccountDTO(accountRepository.findById(id).orElse(null));
    }

    @RequestMapping("/clients/current/accounts")
    public List<AccountDTO> getCurrentAccount(Authentication authentication){
        return clientRepository.findByEmail(authentication.getName()).getAccounts().stream().map(account -> new AccountDTO(account)).collect(toList());
    }

    @RequestMapping(path = "/clients/current/accounts", method = RequestMethod.POST)
    public ResponseEntity<Object> newAccounts (Authentication authentication){
        Client client = clientRepository.findByEmail(authentication.getName());
        if (client.getAccounts().size() >= 3){
            return new ResponseEntity<>("You can't get more accounts", HttpStatus.TOO_MANY_REQUESTS);
        }
        Account newAccount = new Account(number(accountRepository), LocalDateTime.now(), 0);
        client.addAccount(newAccount);
        accountRepository.save(newAccount);
        return new ResponseEntity<>("Account created successfully",HttpStatus.CREATED);
    }

}

