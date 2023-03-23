package com.mindhub.homebanking.utils;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Loan;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.CardService;

public class Utils {

    public static String genericNumber() {
        int Numbers = (int) (Math.random() * 99999999);
        String fullNumber = String.format("VIN%08d",Numbers);
        return fullNumber;
    }
    public static String number(AccountService accountService){
        String Number;
        boolean verifyNumber;
        do {
            Number=genericNumber();
            verifyNumber=accountService.existsByNumber(Number);
        }while(verifyNumber);
        return Number;
    }

    public static Double loanFess(Double amount, Loan loan){
        Double fee = loan.getFee();
        Double amountPlus = amount * fee;
        return amountPlus;
    }

    public static String cvv(){
        int cvv = (int) (Math.random() * 999);
        String cvvCompletado = String.format("%03d", cvv);
        return cvvCompletado;
    }


    public static String numbers(){
        int first = (int) (Math.random()*(9999 - 1000)+1000);
        int second = (int) (Math.random()*(9999 - 1000)+1000);
        int tercero = (int) (Math.random()*(9999 - 1000)+1000);
        int cuarto = (int) (Math.random()*(9999 - 1000)+1000);
        String numbers = first + "-" + second + "-" + tercero + "-" + cuarto;
        return numbers;
    }

    public static String noDuplicatedNumberCard(CardService cardService){
        String Number;
        boolean verifityNumber;
        do {
            Number = numbers();
            verifityNumber = cardService.existsByNumber(Number);
        }while (verifityNumber);
        return Number;
    }

    public static Double currentBalanceCredit(AccountService accountService, Account account, double amount){
        Double getCurrentAmount = accountService.findByNumber(account.getNumber()).getBalance() + amount;
        return getCurrentAmount;
    }
    public static Double currentBalanceDebit(AccountService accountService, Account account, double amount){
        Double getCurrentAmount = accountService.findByNumber(account.getNumber()).getBalance() - amount;
        return getCurrentAmount;
    }

}
