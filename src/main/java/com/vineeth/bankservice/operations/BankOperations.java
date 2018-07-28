package com.vineeth.bankservice.operations;

import com.vineeth.bankservice.datastore.IBankStore;
import com.vineeth.bankservice.user.User;
import org.springframework.stereotype.Service;

import java.util.List;

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
        if(!bankStore.removeBeneficiaryForUser(username, beneficiary)) {
            throw new Exception("Given Beneficiary does not exist to delete");
        }
    }

    public List<Transaction> getTransactions(String username) {
        return null;
    }
}
