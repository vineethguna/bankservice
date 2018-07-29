package com.vineeth.bankservice.operations;

import com.vineeth.bankservice.datastore.IBankStore;
import com.vineeth.bankservice.operations.transactions.Transaction;
import com.vineeth.bankservice.operations.transactions.TransactionRequest;
import com.vineeth.bankservice.operations.transactions.TransactionResult;
import com.vineeth.bankservice.operations.transactions.TransactionStates;
import com.vineeth.bankservice.user.User;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class BankOperations {
    private IBankStore bankStore;

    public BankOperations(IBankStore bankStore) {
        this.bankStore = bankStore;
    }

    public Long getBalanceForUser(String username) throws Exception {
        Long balance = bankStore.getBalanceOfUser(username);
        if(balance == null) {
            throw new Exception("Internal error occurred while querying balance");
        }
        return balance;
    }

    public void addBeneficiary(String username, Beneficiary beneficiary) throws Exception {
        User beneficiaryUser = bankStore.getUserDetails(beneficiary.getUsername());
        if(beneficiaryUser.getAccountId().equals(beneficiary.getAccountId())) {
            if(!bankStore.addBeneficiaryForUser(username, beneficiary)) {
                throw new Exception("Given Beneficiary already exists for the user");
            }
        } else {
           throw new Exception("Provided account Id for beneficiary is not correct");
        }
    }

    public List<Beneficiary> getBeneficiaries(String username) {
        return bankStore.getBeneficiariesForUser(username);
    }

    public void deleteBeneficiary(String username, Beneficiary beneficiary) throws Exception {
        if(bankStore.acquireLockForUser(username)) {
            if(!bankStore.removeBeneficiaryForUser(username, beneficiary)) {
                bankStore.releaseLockForUser(username);
                throw new Exception("Given Beneficiary does not exist to delete");
            }
        } else {
            throw new Exception("Internal Error occurred while deleting beneficiary");
        }
    }

    public List<Transaction> getTransactions(String username) throws Exception {
        List<Transaction> transactions = bankStore.listTransactionsForUser(username);
        if(transactions != null) {
            return transactions;
        } else {
            throw new Exception("No transactions found for given user");
        }
    }

    public Transaction transferFunds(String username, TransactionRequest transactionRequest) throws Exception {
        Transaction transaction = createDebitTransactionFromTransactionRequest(username, transactionRequest);
        Transaction response = transaction.clone();
        if(bankStore.acquireLockForUser(username)) {
            try {
                Beneficiary beneficiary = new Beneficiary();
                beneficiary.setUsername(transactionRequest.getUsername());
                if(bankStore.checkBeneficiaryExistsForUser(username, beneficiary)) {
                    transaction.setState(TransactionStates.IN_PROGRESS);
                    addDebitTransactionForFundsTransfer(username, transaction);
                    transaction.setDebitTransaction(false);
                    transaction.setState(TransactionStates.COMPLETED);
                    addCreditTransactionForFundsTransfer(transaction.getDestinationUsername(), transaction);
                    changeTransactionStateForFundsTransfer(username, transaction.getId(), TransactionStates.COMPLETED);
                    response.setState(TransactionStates.COMPLETED);
                } else {
                    response.setState(TransactionStates.FAILED);
                    response.setDescription("Given source username does not exists in list of beneficiaries");
                }
            } catch (Exception e) {
                response.setState(TransactionStates.FAILED);
                response.setDescription(e.toString());
            }
            bankStore.releaseLockForUser(username);
        } else {
            response.setState(TransactionStates.FAILED);
            response.setDescription("Fund Transfer Cancelled. Internal Error");

        }
        return response;
    }

    public InterestResponse getInterestRateForBalance(String username, Date toDate, int percentage) throws Exception {
        InterestResponse response = new InterestResponse();
        Long balance = getBalanceForUser(username);
        double noOfYears = getNoOfYears(new Date(), toDate);
        long futureBalance = (long) Math.ceil(balance * Math.pow((1 + (percentage / 100.0)), noOfYears));
        response.setCurrentBalance(balance);
        response.setFutureBalance(futureBalance);
        return response;
    }

    private double getNoOfYears(Date fromDate, Date toDate) throws Exception {
        long diffInMillies = toDate.getTime() - fromDate.getTime();
        if(diffInMillies < 0) {
            throw new Exception("Given date is not in future");
        }
        long noOfDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        return noOfDays / 365.0;
    }

    private Transaction createDebitTransactionFromTransactionRequest(String sourceUsername,
                                                                     TransactionRequest transactionRequest) {
        Transaction transaction = new Transaction();
        transaction.setSourceUsername(sourceUsername);
        transaction.setDestinationUsername(transactionRequest.getUsername());
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setState(TransactionStates.INITIALIZED);
        transaction.setDebitTransaction(true);
        transaction.setId(generateUniqueTransactionId());
        return transaction;
    }

    private String generateUniqueTransactionId() {
        return UUID.randomUUID().toString();
    }

    private void addDebitTransactionForFundsTransfer(String username, Transaction transaction) throws Exception {
        TransactionResult debitResult = bankStore.addDebitTransactionForUser(username, transaction);
        if(debitResult.getState().equals(TransactionStates.FAILED)) {
            throw new Exception(debitResult.getErrorDescription());
        }
    }

    private void addCreditTransactionForFundsTransfer(String username, Transaction transaction) throws Exception {
        TransactionResult creditResult =
                bankStore.addCreditTransactionForUser(username, transaction);
        if(creditResult.getState().equals(TransactionStates.FAILED)) {
            throw new Exception(creditResult.getErrorDescription());
        }
    }

    private void changeTransactionStateForFundsTransfer(String username, String transactionId, String state)
            throws Exception {
        TransactionResult transactionStateResult = bankStore.changeTransactionStateForUser(username,
                transactionId, TransactionStates.COMPLETED);
        if(transactionStateResult.getState().equals(TransactionStates.FAILED)) {
            throw new Exception(transactionStateResult.getErrorDescription());
        }
    }
}
