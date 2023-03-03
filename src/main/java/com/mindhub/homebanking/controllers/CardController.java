package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
public class CardController {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private CardRepository cardRepository;

    @RequestMapping("/api/clients/current/cards")
    public List<CardDTO> getCurrentCards(Authentication authentication){
        return clientRepository.findByEmail(authentication.getName()).getCards().stream().map(card -> new CardDTO(card)).collect(toList());
    }

    @RequestMapping(path = "/api/clients/current/cards", method = RequestMethod.POST)
    public ResponseEntity<Object> newCards (Authentication authentication,

        @RequestParam CardType type, @RequestParam CardColor color){

        Client client = clientRepository.findByEmail(authentication.getName());

        if (client.getCards().size() > 6){
            return new ResponseEntity<>("You can't take more cards", HttpStatus.TOO_MANY_REQUESTS);
        }
        if (client.getCards().stream().anyMatch(card -> type == card.getType() && color == card.getColor())){
            return new ResponseEntity<>("This card "+type+" "+color+" is already created", HttpStatus.FORBIDDEN);
        }

        Card newCard = new Card(client.getFirstName()+" "+client.getLastName(), type, color, noDuplicatedNumber(), cvv(), LocalDate.now(), LocalDate.now().plusYears(5));
        client.addCard(newCard);
        cardRepository.save(newCard);

        return new ResponseEntity<>("Congrats "+client.getFirstName()+" "+client.getLastName()+" your card "+type+" "+color+" was successfully created.",HttpStatus.CREATED);
    }

    public String cvv() {
        int cvv = (int) (Math.random() * 999);
        String cvvCompletado = String.format("%03d", cvv);
        return cvvCompletado;
    }

    public static String numbers(){
        int first = (int) (Math.random()*(9999 - 1000)+1000);
        int second = (int) (Math.random()*(9999 - 1000)+1000);
        int third = (int) (Math.random()*(9999 - 1000)+1000);
        int four = (int) (Math.random()*(9999 - 1000)+1000);
        String numbers = first + "-" + second + "-" + third + "-" + four;
        return numbers;
    }

    public String noDuplicatedNumber(){
        String Number;
        boolean verifityNumber;
        do {
            Number = numbers();
            verifityNumber = cardRepository.existsByNumber(Number);
        }while (verifityNumber);
        return Number;
    }
}
