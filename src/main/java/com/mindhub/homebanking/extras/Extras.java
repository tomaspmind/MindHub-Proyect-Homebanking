package com.mindhub.homebanking.extras;

import com.mindhub.homebanking.repositories.AccountRepository;

public class Extras {

    public static String genericNumber() {
        int Numbers = (int) (Math.random() * 99999999);
        String fullNumber = String.format("VIN%08d",Numbers);
        return fullNumber;
    }
    public static String number(AccountRepository accountRepository){
        String Number;
        boolean verifyNumber;
        do {
            Number=genericNumber();
            verifyNumber=accountRepository.existsByNumber(Number);
        }while(verifyNumber);
        return Number;
    }
}
