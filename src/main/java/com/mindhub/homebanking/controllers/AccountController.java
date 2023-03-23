package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.AccountType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.mindhub.homebanking.utils.Utils.number;
import static java.util.stream.Collectors.toList;

@RestController
public class AccountController {

    @Autowired
    AccountService accountService;

    @Autowired
    ClientService clientService;

    @GetMapping("/api/accounts")
    public  List<AccountDTO> getAccounts() {
        return accountService.findAll().stream().map(AccountDTO::new).collect(toList());
    }

    @GetMapping("/api/accounts/{id}")
    public AccountDTO getAccounts(@PathVariable Long id, Authentication authentication) {
        Client client = clientService.findByEmail(authentication.getName());
        Account account = accountService.getReferenceById(id);
        if (!client.getAccounts().contains(account)){
            return new AccountDTO(null);
        }
        return new AccountDTO(account);
    }

    @GetMapping("/api/clients/current/accounts")
    public List<AccountDTO> getCurrentAccount(Authentication authentication){
        Client client = clientService.findByEmail(authentication.getName());
        List<Account> visibleAccounts = client.getAccounts().stream().filter(account -> account.getShowAccount() == true).collect(toList());
        return visibleAccounts.stream().map(account -> new AccountDTO(account)).collect(toList());
    }

    @PostMapping(path = "/api/clients/current/accounts")
        public ResponseEntity<Object> newAccounts (Authentication authentication, @RequestParam(required = false) AccountType accountType){
        Client client = clientService.findByEmail(authentication.getName());

        if (client.getAccounts().size() >= 3 && client.getAccounts().stream().allMatch(account -> account.getShowAccount() == true)){
            return new ResponseEntity<>("You can't get more accounts", HttpStatus.TOO_MANY_REQUESTS);
        }
        if (accountType == null){
            return new ResponseEntity<>("Account Type is empty",HttpStatus.BAD_REQUEST);
        }

        Account newAccount = new Account(number(accountService), LocalDateTime.now(), 0,true, accountType);
        client.addAccount(newAccount);
        accountService.save(newAccount);
        return new ResponseEntity<>("Account created successfully",HttpStatus.CREATED);
    }

    @PatchMapping("/api/clients/current/accounts")
    public ResponseEntity<Object> deleteCards (Authentication authentication,
                                               @RequestParam String accountNumber,
                                               @RequestParam String accountDestiny){

        Client authenticatedClient = clientService.findByEmail(authentication.getName());
        Account accountToDelete = accountService.findByNumber(accountNumber);
        Account accountDestini = accountService.findByNumber(accountDestiny);

        if (accountNumber.isEmpty()){
            return new ResponseEntity<>("Account number is empty",HttpStatus.BAD_REQUEST);
        }
        if (accountDestiny.isEmpty()){
            return new ResponseEntity<>("Account destiny is empty",HttpStatus.BAD_REQUEST);
        }
        if (accountToDelete == accountDestini){
            return new ResponseEntity<>("Account number and account destiny are the same",HttpStatus.BAD_REQUEST);
        }
        if (authenticatedClient.getAccounts().size() < 2){
            return new ResponseEntity<>("You need another account to delete one account",HttpStatus.FORBIDDEN);
        }
        if (accountToDelete == null){
            return new ResponseEntity<>("The account number doesn't exist",HttpStatus.BAD_REQUEST);
        }
        if (!authenticatedClient.getAccounts().contains(accountToDelete)){
            return new ResponseEntity<>("This account number does not belong to your accounts",HttpStatus.FORBIDDEN);
        }
        if (accountDestini == null){
            return new ResponseEntity<>("The account destiny doesn't exist", HttpStatus.BAD_REQUEST);
        }
        if (!authenticatedClient.getAccounts().contains(accountDestini)){
            return new ResponseEntity<>("This account destiny does not belong to your accounts",HttpStatus.FORBIDDEN);
        }
        if (accountToDelete.getShowAccount() == false || accountDestini.getShowAccount() == false){
            return new ResponseEntity<>("The account is already deleted",HttpStatus.BAD_REQUEST);
        }

        accountDestini.setBalance(accountDestini.getBalance()+accountToDelete.getBalance());
        accountToDelete.setBalance(0);
        accountToDelete.setShowAccount(false);
        accountService.save(accountToDelete);
        accountService.save(accountDestini);

        return new ResponseEntity<>("Successfully deleted", HttpStatus.ACCEPTED);
    }

}

