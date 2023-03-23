package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.Card;

import java.util.List;

public interface CardService {

    Card findByNumber(String number);

    Boolean existsByNumber(String number);
    void save (Card card);

    List<Card> findAll();
}
