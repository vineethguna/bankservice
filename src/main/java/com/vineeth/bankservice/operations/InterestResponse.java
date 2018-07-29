package com.vineeth.bankservice.operations;

public class InterestResponse {
    private Long currentBalance;
    private Long futureBalance;


    public Long getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(Long currentBalance) {
        this.currentBalance = currentBalance;
    }

    public Long getFutureBalance() {
        return futureBalance;
    }

    public void setFutureBalance(Long futureBalance) {
        this.futureBalance = futureBalance;
    }
}
