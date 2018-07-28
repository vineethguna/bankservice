package com.vineeth.bankservice.datastore;


import com.vineeth.bankservice.operations.Beneficiary;
import com.vineeth.bankservice.operations.Transaction;
import com.vineeth.bankservice.operations.TransactionState;
import com.vineeth.bankservice.security.AuthenticationDetails;
import com.vineeth.bankservice.user.User;

import java.util.List;

public interface IBankStore {
    String addUser(String username, String password, Long initialBalance);
    AuthenticationDetails getAuthenticationDetailsOfUser(String username);

    // Transaction Data Store APIs
    TransactionState addDebitTransactionForUser(String username, Transaction transaction);
    TransactionState addCreditTransactionForUser(String username, Transaction transaction);
    TransactionState changeTransactionStateForUser(String username, String transactionId, TransactionState state);

    // Beneficiary Data Store APIs
    boolean addBeneficiaryForUser(String username, Beneficiary beneficiary);
    boolean removeBeneficiaryForUser(String username, Beneficiary beneficiary);
    List<Beneficiary> getBeneficiariesForUser(String username);
    boolean checkBeneficiaryExistsForUser(String username, Beneficiary beneficiary);

    // Lock APIs on data store to avoid un expected behaviour
    boolean acquireLockForUser(String username);
    boolean releaseLockForUser(String username);

    Long getBalanceOfUser(String username);
    User getUserDetails(String username);
}
