package com.vineeth.bankservice.operations.transactions;


public class Transaction implements Cloneable {
    private long amount;
    private boolean isDebitTransaction;
    private String sourceUsername;
    private String destinationUsername;
    private String id;
    private String state;
    private String description;

    public Transaction() {
        setState(TransactionStates.STARTED);
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSourceUsername() {
        return sourceUsername;
    }

    public void setSourceUsername(String sourceUsername) {
        this.sourceUsername = sourceUsername;
    }

    public String getDestinationUsername() {
        return destinationUsername;
    }

    public void setDestinationUsername(String destinationUsername) {
        this.destinationUsername = destinationUsername;
    }

    public Transaction clone() throws CloneNotSupportedException {
        return (Transaction) super.clone();
    }
}
