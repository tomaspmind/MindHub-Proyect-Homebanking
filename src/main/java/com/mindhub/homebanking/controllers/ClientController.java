package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.configurations.WebAuthentication;
import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.mindhub.homebanking.extras.Extras.number;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AccountRepository accountRepository;

    @RequestMapping("/clients")
    public List<ClientDTO> getAll() {
        return clientRepository.findAll().stream().map(ClientDTO::new).collect(toList());
    }

    @RequestMapping("/clients/{id}")
    public ClientDTO getClient(@PathVariable Long id) {
        return new ClientDTO(clientRepository.findById(id).orElse(null));
    }

    @RequestMapping(path = "/clients", method = RequestMethod.POST)
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
        if (clientRepository.findByEmail(email) != null) {
            return new ResponseEntity<>("Email is already in use", HttpStatus.FORBIDDEN);
        }

        Client client = new Client(first, lastName, email, passwordEncoder.encode(password));
        Account account = new Account(number(accountRepository), LocalDateTime.now(), 0);
        client.addAccount(account);
        clientRepository.save(client);
        accountRepository.save(account);

        return new ResponseEntity<>("Welcome"+first+" "+lastName +" have a good day in the bank",HttpStatus.CREATED);
    }

    @RequestMapping("/clients/current")
    public ClientDTO getCurrentClient(Authentication authentication){
        String email = authentication.getName();
        return new ClientDTO(clientRepository.findByEmail(email));
    }



}