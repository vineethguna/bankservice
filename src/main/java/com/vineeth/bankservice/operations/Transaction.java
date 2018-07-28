package com.vineeth.bankservice.operations;


import com.fasterxml.jackson.annotation.JsonIgnore;

public class Transaction {
    private long amount;
    private boolean isDebitTransaction;
    private String username;
    private String id;
    private TransactionState state;
    private String description;

    public Transaction() {
        setState(TransactionState.STARTED);
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public boolean isDebitTransaction() {
        return isDebitTransaction;
    }

    public void setDebitTransaction(boolean debitTransaction) {
        isDebitTransaction = debitTransaction;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonIgnore
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonIgnore
    public String getDescription() {
        return description;
    }

    public String getState() {
        return state.getState();
    }

    public void setState(TransactionState state) {
        this.state = state;
    }
}
