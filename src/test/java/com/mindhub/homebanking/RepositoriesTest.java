package com.mindhub.homebanking;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.util.AssertionErrors.assertTrue;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import com.mindhub.homebanking.services.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest
public class RepositoriesTest {

    @Autowired
    AccountService accountService;
    @Autowired
    CardService cardService;
    @Autowired
    ClientService clientService;
    @Autowired
    LoanService loanService;
    @Autowired
    TransactionService transactionService;

    @Test
    public void existLoans(){
        List<Loan> loans = loanService.findAll();
        assertThat(loans,is(not(empty())));
    }

    @Test
    public void existPersonalLoan(){
        List<Loan> loans = loanService.findAll();
        assertThat(loans, hasItem(hasProperty("name", is("Personal"))));
    }

    @Test
    public void startWithVIN(){
        List<Account> accounts = accountService.findAll();
        for (Account account : accounts) {
            assertThat(account.getNumber(), startsWith("VIN"));
        }
    }

    @Test
    public void accountsHasAClient(){
        List<Account> accounts = accountService.findAll();
        for (Account account : accounts){
            assertThat(account.getClient(),is(not(nullValue())));
        }
    }

    @Test
    public void allClientHasAtLeastOneAccount(){
        List<Client> clients = clientService.findAll().stream().filter(client -> !client.getEmail().equals("admin@mindhub.com")).collect(Collectors.toList());
        for (Client client : clients){
            assertTrue("Todos tienen al menos una cuenta", !client.getAccounts().isEmpty());
            System.out.println(client);
        }

    }

    @Test
    public void adminHasNoAccount(){
        Client admin = clientService.findByEmail("admin@mindhub.com");
        assertThat(admin.getAccounts(), is(empty()));
    }

    @Test
    public void ifCardTypeHaveAEnum(){
        List<Card> cards = cardService.findAll();
        assertThat(cards,hasItem(hasProperty("type", isA(Enum.class))));
    }

    @Test
    public void cvvHaveThreeDigits(){
        List<Card> cards = cardService.findAll();
        for (Card card : cards){
            assertThat(card.getCvv(), hasLength(3));
        }
    }

    @Test
    public void allTransactionsHaveOneAccount(){
        List<Transaction> transactions = transactionService.findAll();
        assertThat(transactions, hasItem(hasProperty("account", is(notNullValue()))));
    }

    @Test
    public void allTransactionsHaveADescription(){
        List<Transaction> transactions = transactionService.findAll();
        for (Transaction transaction : transactions){
            assertThat(transaction.getDescription(), not(emptyString()));
        }
    }
}
