package com.vineeth.bankservice.operations.transactions;

public class TransactionRequest {
    private long amount;
    private String username;


    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
