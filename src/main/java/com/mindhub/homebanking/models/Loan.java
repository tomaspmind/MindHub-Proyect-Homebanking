package com.mindhub.homebanking.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private String name;

    private int maxAmount;
    @ElementCollection
    @Column (name = "payment")
    private List<Integer> payment = new ArrayList<>(); //constructor que se va a usar (arrayList)

    @OneToMany(mappedBy = "loan", fetch = FetchType.EAGER)
    private Set<ClientLoan> clientLoans = new HashSet<>();

    public Loan(){}

    public Loan(String name, int maxAmount, List<Integer> payment) {
        this.name = name;
        this.maxAmount = maxAmount;
        this.payment = payment;
    }

    public void addClientLoan(ClientLoan clientLoan){
        clientLoan.setLoan(this);
        clientLoans.add(clientLoan);
    }

    @JsonIgnore
    public List<Client> getClients(){
        return clientLoans.stream().map(cl -> cl.getClient()).collect(Collectors.toList());
    }

    public Set<ClientLoan> getClientLoans() {
        return clientLoans;
    }

    public void setClientLoans(Set<ClientLoan> clientLoans) {
        this.clientLoans = clientLoans;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

    public List<Integer> getPayment() {
        return payment;
    }

    public void setPayment(List<Integer> payment) {
        this.payment = payment;
    }

    public long getId() { return id; }

    public void addLoans(ClientLoan client){
        client.setLoan(this);
        clientLoans.add(client);
    }
}
