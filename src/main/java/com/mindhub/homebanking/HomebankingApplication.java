package com.mindhub.homebanking;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.rest.webmvc.json.JacksonMappingAwareSortTranslator;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class HomebankingApplication {

	@Autowired
	private PasswordEncoder passwordEncoder;
	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository, TransactionRepository transactionRepository, LoanRepository loanRepository, ClientLoanRepository clientLoanRepository, CardRepository cardRepository) {
		return (args) -> {

			Client melba = new Client("Melba", "Morel", "melba@mindhub.com", passwordEncoder.encode("asd123"));
			Client tomas = new Client("Tomas", "Perez", "tomipp6@gmail.com", passwordEncoder.encode("asd123"));
			Client rodrigo = new Client("Rodrigo", "asd", "asdad.@gmail.com", passwordEncoder.encode("asd123"));
			Client admin = new Client("admin", "admin", "admin@mindhub.com", passwordEncoder.encode("admin"));

			LocalDateTime now = LocalDateTime.now();
			LocalDateTime tomorrow = now.plusDays(1);

			LocalDate nowOnlyDay = LocalDate.now();
			LocalDate expirationDay = LocalDate.now().plusYears(5);

			Account account1 = new Account("VIN001", now, 5000.0);
			Account account2 = new Account("VIN002", tomorrow, 7500.0);
			Account account3 = new Account("VIN003",now,10000.0);
			Account account4 = new Account("VIN004", tomorrow, 15000.0);

			Transaction transaction1 = new Transaction(TransactionType.CREDIT, 2000.0,"Sale of jacket",LocalDateTime.now().minusHours(3));
			Transaction transaction2 = new Transaction(TransactionType.CREDIT, 1500.0,"Sale of footwear",LocalDateTime.now().minusDays(2));
			Transaction transaction3 = new Transaction(TransactionType.CREDIT, 3000.0,"Sale of jeweler's",LocalDateTime.now().minusMinutes(30));
			Transaction transaction4 = new Transaction(TransactionType.DEBIT, 1000.0,"Purchase of papers",LocalDateTime.now().minusHours(10));
			Transaction transaction5 = new Transaction(TransactionType.DEBIT, 500.0,"Purchase of snacks",LocalDateTime.now().minusSeconds(40));

			Transaction transaction6 = new Transaction(TransactionType.CREDIT, 7000.0,"Sale of jacket",LocalDateTime.now().minusDays(4));
			Transaction transaction7 = new Transaction(TransactionType.CREDIT, 8500.0,"Sale of footwear",LocalDateTime.now().minusDays(1));
			Transaction transaction8 = new Transaction(TransactionType.DEBIT, 2500.0,"Purchase of keyboard",LocalDateTime.now().minusHours(7));
			Transaction transaction9 = new Transaction(TransactionType.DEBIT, 3500.0,"Purchase of microphone",LocalDateTime.now().minusMinutes(50));
			Transaction transaction10 = new Transaction(TransactionType.DEBIT, 2500.0,"Purchase of mouse",LocalDateTime.now().minusHours(1));

			Loan loan1 = new Loan("Mortgage", 500000, List.of(12,24,36,48,60));
			Loan loan2 = new Loan("Personal", 100000, List.of(6,12,24));
			Loan loan3 = new Loan("Automotive", 300000, List.of(6,12,24,36));

			ClientLoan clientLoan1 = new ClientLoan(400000, 60, melba, loan1);
			ClientLoan clientLoan2 = new ClientLoan(50000,12,melba, loan2);

			ClientLoan clientLoan3 = new ClientLoan(100000,24,tomas,loan2);
			ClientLoan clientLoan4 = new ClientLoan(200000, 36, tomas, loan3);

			Card card1 = new Card(melba.getFirstName()+" "+melba.getLastName(),CardType.DEBIT, CardColor.GOLD,numbers(),cvv(),nowOnlyDay,expirationDay);
			Card card2 = new Card(melba.getFirstName()+" "+melba.getLastName(),CardType.CREDIT, CardColor.TITANIUM,numbers(),cvv(),nowOnlyDay,expirationDay);

			Card card3 = new Card(tomas.getFirstName()+" "+tomas.getLastName(),CardType.CREDIT,CardColor.SILVER,numbers(),cvv(),nowOnlyDay,expirationDay);

			melba.addAccount(account1);
			melba.addAccount(account2);
			tomas.addAccount(account3);
			tomas.addAccount(account4);

			account1.addTransaction(transaction1);
			account1.addTransaction(transaction2);
			account1.addTransaction(transaction3);
			account1.addTransaction(transaction4);
			account1.addTransaction(transaction5);

			account2.addTransaction(transaction6);
			account2.addTransaction(transaction7);
			account2.addTransaction(transaction8);
			account2.addTransaction(transaction9);
			account2.addTransaction(transaction10);

			card1.setClient(melba);
			card2.setClient(melba);
			card3.setClient(tomas);

			melba.addCard(card1);
			melba.addCard(card2);
			tomas.addCard(card3);

			clientRepository.save(melba);
			clientRepository.save(tomas);
			clientRepository.save(rodrigo);
			clientRepository.save(admin);

			accountRepository.save(account1);
			accountRepository.save(account2);

			transactionRepository.save(transaction1);
			transactionRepository.save(transaction2);
			transactionRepository.save(transaction3);
			transactionRepository.save(transaction4);
			transactionRepository.save(transaction5);
			transactionRepository.save(transaction6);
			transactionRepository.save(transaction7);
			transactionRepository.save(transaction8);
			transactionRepository.save(transaction9);
			transactionRepository.save(transaction10);

			loanRepository.save(loan1);
			loanRepository.save(loan2);
			loanRepository.save(loan3);

			clientLoanRepository.save(clientLoan1);
			clientLoanRepository.save(clientLoan2);
			clientLoanRepository.save(clientLoan3);
			clientLoanRepository.save(clientLoan4);

			cardRepository.save(card1);
			cardRepository.save(card2);
			cardRepository.save(card3);

		};
	}

	public String cvv() {
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


}
