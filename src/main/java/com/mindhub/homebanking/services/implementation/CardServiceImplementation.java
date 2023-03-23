package com.mindhub.homebanking.services.implementation;

import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.services.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardServiceImplementation implements CardService {

    @Autowired
    CardRepository cardRepository;
    @Override
    public Card findByNumber(String number) {
        return cardRepository.findByNumber(number);
    }

    @Override
    public Boolean existsByNumber(String number) {
        return cardRepository.existsByNumber(number);
    }

    @Override
    public void save(Card card) {
        cardRepository.save(card);
    }

    @Override
    public List<Card> findAll() {
        return cardRepository.findAll();
    }
}
