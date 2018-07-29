package com.vineeth.bankservice.operations.transactions;

public class TransactionResult {
    private String state;
    private String errorDescription;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }
}
