package com.vineeth.bankservice.datastore;


import com.vineeth.bankservice.beneficiaries.Beneficiary;
import com.vineeth.bankservice.security.AuthenticationRequest;
import com.vineeth.bankservice.transactions.Transaction;
import com.vineeth.bankservice.user.UserRequest;

public interface IBankStore {
    boolean addUser(UserRequest userRequest);
    void addTransactionForUser(String username, Transaction transaction);
    void addBeneficiaryForUser(String username, Beneficiary beneficiary);
    Long getBalanceOfUser(String username);
    AuthenticationRequest getAuthenticationDetailsOfUser(String username);

}
