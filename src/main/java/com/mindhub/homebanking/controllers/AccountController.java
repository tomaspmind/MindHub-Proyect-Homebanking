package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.mindhub.homebanking.extras.Extras.number;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private TransactionRepository transactionRepository;

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
        Client client = clientRepository.findByEmail(authentication.getName());
        List<Account> visibleAccounts = client.getAccounts().stream().filter(account -> account.getShowAccount() == true).collect(toList());
        return visibleAccounts.stream().map(account -> new AccountDTO(account)).collect(toList());
    }

    @RequestMapping(path = "/clients/current/accounts", method = RequestMethod.POST)
    public ResponseEntity<Object> newAccounts (Authentication authentication){
        Client client = clientRepository.findByEmail(authentication.getName());

        if (client.getAccounts().size() >= 3 && client.getAccounts().stream().allMatch(account -> account.getShowAccount() == true)){
            return new ResponseEntity<>("You can't get more accounts", HttpStatus.TOO_MANY_REQUESTS);
        }

        Account newAccount = new Account(number(accountRepository), LocalDateTime.now(), 0,true);
        client.addAccount(newAccount);
        accountRepository.save(newAccount);
        return new ResponseEntity<>("Account created successfully",HttpStatus.CREATED);
    }

    @RequestMapping(path = "/clients/current/accounts", method = RequestMethod.PATCH)
    public ResponseEntity<Object> deleteCards (Authentication authentication,
                                               @RequestParam String accountNumber,
                                               @RequestParam String accountDestiny){

        Client authenticatedClient = clientRepository.findByEmail(authentication.getName());
        Account accountToDelete = accountRepository.findByNumber(accountNumber);
        Account accountDestini = accountRepository.findByNumber(accountDestiny);

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
        accountRepository.save(accountToDelete);
        accountRepository.save(accountDestini);

        return new ResponseEntity<>("Successfully deleted", HttpStatus.ACCEPTED);
    }

}

