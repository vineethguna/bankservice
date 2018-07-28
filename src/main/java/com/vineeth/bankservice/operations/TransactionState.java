package com.vineeth.bankservice.operations;

/**
 * Created by guna on 28/07/18.
 */
public enum TransactionState {
    INITIALIZED("INITIALIZED"),
    STARTED("STARTED"),
    IN_PROGRESS("IN_PROGRESS"),
    COMPLETED("COMPLETED"),
    FAILED("FAILED");

    private String state;
    private String description;

    TransactionState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
