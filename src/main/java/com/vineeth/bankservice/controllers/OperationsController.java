package com.vineeth.bankservice.controllers;

import com.vineeth.bankservice.operations.BankOperations;
import com.vineeth.bankservice.operations.Beneficiary;
import com.vineeth.bankservice.operations.InterestResponse;
import com.vineeth.bankservice.operations.transactions.Transaction;
import com.vineeth.bankservice.operations.transactions.TransactionRequest;
import com.vineeth.bankservice.security.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/account")
public class OperationsController {

    @Autowired
    private SecurityManager securityManager;

    @Autowired
    private BankOperations bankOperations;

    @RequestMapping("/balance")
    public long getBalanceForUser(@RequestHeader("Authorization") String accessToken) throws Exception {
        String username = securityManager.authorizeForUser(accessToken);
        return bankOperations.getBalanceForUser(username);
    }

    @RequestMapping(value = "/beneficiary", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void addBeneficiary(@RequestHeader("Authorization") String accessToken,
                               @RequestBody Beneficiary beneficiary) throws Exception {
        String username = securityManager.authorizeForUser(accessToken);
        bankOperations.addBeneficiary(username, beneficiary);
    }

    @RequestMapping(value = "/beneficiary", method = RequestMethod.GET)
    public List<Beneficiary> listBeneficiaries(@RequestHeader("Authorization") String accessToken) throws Exception {
        String username = securityManager.authorizeForUser(accessToken);
        return bankOperations.getBeneficiaries(username);
    }

    @RequestMapping(value = "/beneficiary", method = RequestMethod.DELETE)
    public void deleteBeneficiary(@RequestHeader("Authorization") String accessToken,
                                  @RequestBody Beneficiary beneficiary) throws Exception {
        String username = securityManager.authorizeForUser(accessToken);
        bankOperations.deleteBeneficiary(username, beneficiary);
    }

    @RequestMapping(value = "/transactions", method = RequestMethod.GET)
    public List<Transaction> listTransactionsForUser(@RequestHeader("Authorization") String accessToken)
            throws Exception {
        String username = securityManager.authorizeForUser(accessToken);
        return bankOperations.getTransactions(username);
    }

    @RequestMapping(value = "/transfer", method = RequestMethod.PUT)
    public Transaction transferFundToUser(@RequestHeader("Authorization") String accessToken,
                                                  @RequestBody TransactionRequest transactionRequest)
            throws Exception {
        String username = securityManager.authorizeForUser(accessToken);
        return bankOperations.transferFunds(username, transactionRequest);
    }

    @RequestMapping(value = "/interest", method = RequestMethod.GET)
    public InterestResponse calculateInterestOnBalanceForUser(@RequestHeader("Authorization") String accessToken,
                                                              @RequestParam("interestRate") Integer percentage,
                                                              @RequestParam("toDate")
                                                      @DateTimeFormat(pattern="dd-MM-yyyy") Date toDate) throws Exception {
        String username = securityManager.authorizeForUser(accessToken);
        return bankOperations.getInterestRateForBalance(username, toDate, percentage);
    }
}
