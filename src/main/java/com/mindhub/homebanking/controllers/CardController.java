package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.dtos.CardTransactionDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.CardService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.mindhub.homebanking.utils.Utils.*;
import static java.util.stream.Collectors.toList;

@RestController
public class CardController {

    @Autowired
    ClientService clientService;
    @Autowired
    CardService cardService;
    @Autowired
    AccountService accountService;
    @Autowired
    TransactionService transactionService;

    @GetMapping("/api/clients/current/cards")
    public List<CardDTO> getCurrentCards(Authentication authentication){
        Client client = clientService.findByEmail(authentication.getName());
        List<Card> visibleCards = client.getCards().stream().filter(card -> card.getShowCard() == true).collect(Collectors.toList());
        return visibleCards.stream().map(card -> new CardDTO(card)).collect(toList());
    }

    @PostMapping("/api/clients/current/cards")
    public ResponseEntity<Object> newCards (Authentication authentication,

        @RequestParam CardType type, @RequestParam CardColor color){

        Client client = clientService.findByEmail(authentication.getName());

        if (client.getCards().size() > 6){
            return new ResponseEntity<>("You can't take more cards", HttpStatus.TOO_MANY_REQUESTS);
        }
        if (client.getCards().stream().anyMatch(card -> type == card.getType() && color == card.getColor() && card.getShowCard() == true)){
            return new ResponseEntity<>("This card "+type+" "+color+" is already created", HttpStatus.FORBIDDEN);
        }

        Card newCard = new Card(client.getFirstName()+" "+client.getLastName(), type, color, noDuplicatedNumberCard(cardService), cvv(), LocalDate.now(), LocalDate.now().plusYears(5),true);
        client.addCard(newCard);
        cardService.save(newCard);

        return new ResponseEntity<>("Congrats "+client.getFirstName()+" "+client.getLastName()+" your card "+type+" "+color+" was successfully created.",HttpStatus.CREATED);
    }

    @PatchMapping("/api/clients/current/cards")
    public ResponseEntity<Object> deleteCards (Authentication authentication, @RequestParam String number){

        Client authenticatedClient = clientService.findByEmail(authentication.getName());
        Card getCardToDelete = cardService.findByNumber(number);

        if(authenticatedClient.getCards().stream().noneMatch(card -> card == getCardToDelete)){
            return new ResponseEntity<>("You do not posses this card", HttpStatus.FORBIDDEN);
        }
        if( getCardToDelete.getCardholder().isEmpty()){
            return new  ResponseEntity<>("You must select Cardholder option", HttpStatus.BAD_REQUEST);
        }
        if( getCardToDelete.getType().toString().isEmpty()){
            return new  ResponseEntity<>("You must select a card type option", HttpStatus.BAD_REQUEST);
        }
        if( getCardToDelete.getColor().toString().isEmpty()){
            return new  ResponseEntity<>("You must select a card color option", HttpStatus.BAD_REQUEST);
        }
        if( getCardToDelete.getCvv().isEmpty()){
            return new  ResponseEntity<>("You must select CVV option", HttpStatus.BAD_REQUEST);
        }
        if( getCardToDelete.getFromDate().toString().isEmpty()){
            return new  ResponseEntity<>("You must select Date of Creation option", HttpStatus.BAD_REQUEST);
        }
        if( getCardToDelete.getThruDate().toString().isEmpty()){
            return new  ResponseEntity<>("You must select Date of Expiration option", HttpStatus.BAD_REQUEST);
        }
        if (getCardToDelete.getShowCard() == false){
            return new ResponseEntity<>("This card is already deleted",HttpStatus.BAD_REQUEST);
        }

        getCardToDelete.setShowCard(false);
        cardService.save(getCardToDelete);

        return new ResponseEntity<>("Card successfully deleted!", HttpStatus.ACCEPTED);
    }
    @CrossOrigin(origins = "http://127.0.0.1:5500")
    @Transactional
    @PostMapping("/api/cards/transactions")
    public ResponseEntity<Object> createCardTransaction(@RequestBody(required = false) CardTransactionDTO cardTransactionDTO){

        String number = cardTransactionDTO.getNumber();
        String cvv = cardTransactionDTO.getCvv();
        Double amount = cardTransactionDTO.getAmount();
        String description = cardTransactionDTO.getDescription();
        Short thruDateYear = cardTransactionDTO.getThruDateYear();
        String thruDateMonth = cardTransactionDTO.getThruDateMonth();

        Card clientCard = cardService.findByNumber(number);
        Client client = clientCard.getClient();
        Account accountSelected = client.getAccounts().stream().findFirst().get();
        String fusion = thruDateYear+"-"+thruDateMonth;
        LocalDate thruDate = clientCard.getThruDate();
        String[] thruDate2 = clientCard.getThruDate().toString().split("-");
        String thruDateFinal = thruDate2[0]+"-"+thruDate2[1];

        if (number.isEmpty()){
            return new ResponseEntity<>("Card number is empty", HttpStatus.BAD_REQUEST);
        }
        if (cvv.isEmpty()){
            return new ResponseEntity<>("Cvv number is empty",HttpStatus.BAD_REQUEST);
        }
        if (amount == null){
            return new ResponseEntity<>("Amount is empty",HttpStatus.BAD_REQUEST);
        }
        if (description.isEmpty()){
            return new ResponseEntity<>("Description is empty",HttpStatus.BAD_REQUEST);
        }
        if (thruDateYear == null || thruDateMonth == null){
            return new ResponseEntity<>("Expiration number is empty",HttpStatus.BAD_REQUEST);
        }
        if (clientCard == null){
            return new ResponseEntity<>("The card number doesn't exist",HttpStatus.BAD_REQUEST);
        }
        if (!client.getCards().contains(clientCard)){
            return new ResponseEntity<>("You don't have this card",HttpStatus.FORBIDDEN);
        }
        if (!thruDateFinal.equals(fusion)){
            return new ResponseEntity<>("The expiration day is not the same",HttpStatus.BAD_REQUEST);
        }
        if (thruDate.isBefore(LocalDate.now())){
            return new ResponseEntity<>("The card is expired",HttpStatus.FORBIDDEN);
        }
        if (!cvv.equals(clientCard.getCvv())){
            return new ResponseEntity<>("The cvv is not the same",HttpStatus.BAD_REQUEST);
        }
        if (amount > accountSelected.getBalance()){
            return new ResponseEntity<>("the amount is insufficient",HttpStatus.BAD_REQUEST);
        }

        Transaction transaction = new Transaction(TransactionType.DEBIT,amount,description, LocalDateTime.now(),currentBalanceDebit(accountService,accountSelected,amount));
        accountSelected.addTransaction(transaction);
        accountSelected.setBalance(accountSelected.getBalance() - amount);
        transactionService.save(transaction);

        return new ResponseEntity<>("Created",HttpStatus.CREATED);
    }

}
