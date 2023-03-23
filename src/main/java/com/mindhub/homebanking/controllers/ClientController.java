package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.AccountType;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.mindhub.homebanking.utils.Utils.number;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@RestController
public class ClientController {

    @Autowired
    ClientService clientService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AccountService accountService;

    @GetMapping("/api/clients")
    public List<ClientDTO> getAll() {
        return clientService.findAll().stream().map(ClientDTO::new).collect(toList());
    }

    @GetMapping("/api/clients/{id}")
    public ClientDTO getClient(@PathVariable Long id) {
        return new ClientDTO(clientService.findById(id));
    }

    @PostMapping("/api/clients")
    public ResponseEntity<Object> register(

            @RequestParam String first, @RequestParam String lastName,
            @RequestParam String email, @RequestParam String password) {

        if (first.isEmpty()) {
            return new ResponseEntity<>("Missing FirstName", HttpStatus.BAD_REQUEST);
        }
        if (lastName.isEmpty()) {
            return new ResponseEntity<>("Missing LastName", HttpStatus.BAD_REQUEST);
        }
        if (email.isEmpty()) {
            return new ResponseEntity<>("Missing Email", HttpStatus.BAD_REQUEST);
        }
        if (password.isEmpty()) {
            return new ResponseEntity<>("Missing Password", HttpStatus.BAD_REQUEST);
        }
        if (clientService.findByEmail(email) != null) {
            return new ResponseEntity<>("Email is already in use", HttpStatus.FORBIDDEN);
        }

        Client client = new Client(first, lastName, email, passwordEncoder.encode(password));
        Account account = new Account(number(accountService), LocalDateTime.now(), 0,true, AccountType.CHECKING);
        client.addAccount(account);
        clientService.save(client);
        accountService.save(account);

        return new ResponseEntity<>("Welcome"+first+" "+lastName +" have a good day in the bank",HttpStatus.CREATED);
    }

    @GetMapping("/api/clients/current")
    public ClientDTO getCurrentClient(Authentication authentication){
        String email = authentication.getName();
        Client client = clientService.findByEmail(email);

        Set<Card> visibleCards = client.getCards().stream().filter(card -> card.getShowCard() == true).collect(toSet());

        Set<Account> visibleAccounts = client.getAccounts().stream().filter(account -> account.getShowAccount() == true).collect(toSet());

        client.setAccounts(visibleAccounts);
        client.setCards(visibleCards);
        return new ClientDTO(client);
    }


}