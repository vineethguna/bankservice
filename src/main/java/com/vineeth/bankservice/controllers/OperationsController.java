package com.vineeth.bankservice.controllers;

import com.vineeth.bankservice.operations.BankOperations;
import com.vineeth.bankservice.operations.Beneficiary;
import com.vineeth.bankservice.operations.Transaction;
import com.vineeth.bankservice.security.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by guna on 28/07/18.
 */
@RestController
@RequestMapping("/account")
public class OperationsController {

    @Autowired
    private SecurityManager securityManager;

    @Autowired
    private BankOperations bankOperations;

    @RequestMapping("/balance/{username}")
    public long getBalanceForUser(@PathVariable("username") String username,
                                  @RequestHeader("Authorization") String accessToken) throws Exception {
        authorizeAndCheckUser(username, accessToken);
        return bankOperations.getBalanceForUser(username);
    }

    @RequestMapping(value = "/beneficiary/{username}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void addBeneficiary(@PathVariable("username") String username,
                               @RequestHeader("Authorization") String accessToken,
                               @RequestBody Beneficiary beneficiary) throws Exception {
        authorizeAndCheckUser(username, accessToken);
        bankOperations.addBeneficiary(username, beneficiary);
    }

    @RequestMapping(value = "/beneficiary/{username}", method = RequestMethod.GET)
    public List<Beneficiary> listBeneficiaries(@PathVariable("username") String username,
                                            @RequestHeader("Authorization") String accessToken) throws Exception {
        authorizeAndCheckUser(username, accessToken);
        return bankOperations.getBeneficiaries(username);
    }

    @RequestMapping(value = "/beneficiary/{username}", method = RequestMethod.DELETE)
    public void deleteBeneficiary(@PathVariable("username") String username,
                                  @RequestHeader("Authorization") String accessToken,
                                  @RequestBody Beneficiary beneficiary) throws Exception {
        authorizeAndCheckUser(username, accessToken);
        bankOperations.deleteBeneficiary(username, beneficiary);
    }

    @RequestMapping(value = "/transactions/{username}", method = RequestMethod.GET)
    public List<Transaction> listTransactionsForUser(@PathVariable("username") String username,
                                                     @RequestHeader("Authorization") String accessToken)
            throws Exception {
        authorizeAndCheckUser(username, accessToken);
        return bankOperations.getTransactions(username);
    }

    private void authorizeAndCheckUser(String username, String accessToken) throws Exception {
        String usernameFromAccessToken = securityManager.authorizeForUser(accessToken);
        if(!username.equals(usernameFromAccessToken)) {
            throw new Exception("Querying balance for other user not allowed");
        }
    }
}
