package com.vineeth.bankservice.datastore.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import com.vineeth.bankservice.EnvironmentConstants;
import com.vineeth.bankservice.datastore.IBankStore;
import com.vineeth.bankservice.operations.Beneficiary;
import com.vineeth.bankservice.operations.transactions.Transaction;
import com.vineeth.bankservice.operations.transactions.TransactionResult;
import com.vineeth.bankservice.operations.transactions.TransactionStates;
import com.vineeth.bankservice.security.AuthenticationDetails;
import com.vineeth.bankservice.user.User;
import org.bson.Document;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;

@Service
public class MongoBankStore implements IBankStore {
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> collection;

    public MongoBankStore() throws Exception {
        mongoClient = new MongoClient();
        mongoDatabase = mongoClient.getDatabase(System.getenv(EnvironmentConstants.MONGO_DATABASE));
        collection = mongoDatabase.getCollection(System.getenv(EnvironmentConstants.MONGO_COLLECTION));
    }

    @Override
    public String addUser(String username, String password, Long initialBalance) {
        Document newDocument = new Document()
                .append(SchemaFields.Account.USERNAME, username)
                .append(SchemaFields.Account.PASSWORD, password)
                .append(SchemaFields.Account.BALANCE, initialBalance)
                .append(SchemaFields.Account.BENEFICIARIES, new ArrayList<Document>())
                .append(SchemaFields.Account.TRANSACTIONS, new ArrayList<Document>())
                .append(SchemaFields.Account.BENEFICIARY_LOCK, false);
        Document update = new Document("$setOnInsert", newDocument);
        UpdateOptions options = new UpdateOptions();
        options.upsert(true);
        UpdateResult result = collection.updateOne(Filters.eq(SchemaFields.Account.USERNAME, username),
                update, options);
        if(result.getUpsertedId() != null) {
            return result.getUpsertedId().asObjectId().toString();
        }
        return null;
    }

    @Override
    public boolean addBeneficiaryForUser(String username, Beneficiary beneficiary) {
        Document beneficiaryDocument = new Document()
                .append(SchemaFields.Beneficiary.USERNAME, beneficiary.getUsername())
                .append(SchemaFields.Beneficiary.ACCOUNT_ID, beneficiary.getAccountId());
        Document addToSetDocument = new Document("$addToSet",
                new Document(SchemaFields.Account.BENEFICIARIES, beneficiaryDocument));
        UpdateResult updateResult = collection.updateOne(Filters.eq(SchemaFields.Account.USERNAME, username),
                addToSetDocument);
        return updateResult.isModifiedCountAvailable() && updateResult.getModifiedCount() == 1;
    }

    @Override
    public boolean removeBeneficiaryForUser(String username, Beneficiary beneficiary) {
        Document removeBeneficiary = new Document("$pull",
                new Document(SchemaFields.Account.BENEFICIARIES,
                        new Document(SchemaFields.Beneficiary.USERNAME, beneficiary.getUsername())));
        UpdateResult updateResult = collection.updateOne(Filters.eq(SchemaFields.Account.USERNAME, username),
                removeBeneficiary);
        return updateResult.isModifiedCountAvailable() && updateResult.getModifiedCount() == 1;
    }

    @Override
    public List<Beneficiary> getBeneficiariesForUser(String username) {
        Document userDocument = collection.find(Filters.eq(SchemaFields.Account.USERNAME, username)).projection(
                Projections.include(SchemaFields.Account.BENEFICIARIES)).first();
        return (List<Beneficiary>) userDocument.get(SchemaFields.Account.BENEFICIARIES);
    }

    @Override
    public boolean checkBeneficiaryExistsForUser(String username, Beneficiary beneficiary) {
        Document document = collection.find(Filters.and(Filters.eq(SchemaFields.Account.USERNAME, username),
                Filters.eq(SchemaFields.Account.BENEFICIARIES + '.' + SchemaFields.Beneficiary.USERNAME,
                        beneficiary.getUsername()))).first();
        return document != null;
    }

    @Override
    public boolean acquireLockForUser(String username) {
        Document updateDocument = new Document("$set", new Document(SchemaFields.Account.BENEFICIARY_LOCK, true));
        Document filter = new Document(SchemaFields.Account.USERNAME, username)
                .append(SchemaFields.Account.BENEFICIARY_LOCK, false);
        UpdateResult updateResult = collection.updateOne(filter, updateDocument);
        return updateResult.isModifiedCountAvailable() && updateResult.getModifiedCount() == 1;
    }

    @Override
    public boolean releaseLockForUser(String username) {
        Document updateDocument = new Document("$set", new Document(SchemaFields.Account.BENEFICIARY_LOCK, false));
        UpdateResult updateResult = collection.updateOne(Filters.eq(SchemaFields.Account.USERNAME, username),
                updateDocument);
        return updateResult.isModifiedCountAvailable() && updateResult.getModifiedCount() == 1;
    }

    @Override
    public Long getBalanceOfUser(String username) {
        Document document = collection.find(new Document(SchemaFields.Account.USERNAME, username))
                .projection(Projections.include(SchemaFields.Account.BALANCE)).first();
        if(document != null) {
            return document.getLong(SchemaFields.Account.BALANCE);
        }
        return null;
    }

    @Override
    public AuthenticationDetails getAuthenticationDetailsOfUser(String username) {
        Document document = collection.find(new Document(SchemaFields.Account.USERNAME, username))
                .projection(Projections.include(SchemaFields.Account.USERNAME, SchemaFields.Account.PASSWORD)).first();
        if(document != null) {
            AuthenticationDetails authenticationDetails = new AuthenticationDetails();
            authenticationDetails.setUsername(document.getString(SchemaFields.Account.USERNAME));
            authenticationDetails.setPassword(document.getString(SchemaFields.Account.PASSWORD));
            return authenticationDetails;
        }
        return null;
    }

    @Override
    public TransactionResult addDebitTransactionForUser(String username, Transaction transaction) {
        TransactionResult result = new TransactionResult();
        Document transactionDebitDocument = new Document()
                .append(SchemaFields.Transaction.AMOUNT, transaction.getAmount())
                .append(SchemaFields.Transaction.DEBIT, true)
                .append(SchemaFields.Transaction.STATE, transaction.getState())
                .append(SchemaFields.Transaction.TRANSACTION_ID, transaction.getId())
                .append(SchemaFields.Transaction.USERNAME, transaction.getDestinationUsername());
        Document updateDebitDocument = new Document("$inc",
                new Document(SchemaFields.Account.BALANCE, -1 * transaction.getAmount()))
                .append("$push", new Document(SchemaFields.Account.TRANSACTIONS, transactionDebitDocument));
        UpdateResult updateDebitResult = collection.updateOne(Filters.and(Filters.eq(SchemaFields.Account.USERNAME,
                username),
                Filters.gte(SchemaFields.Account.BALANCE, transaction.getAmount())), updateDebitDocument);
        if(updateDebitResult.isModifiedCountAvailable() && updateDebitResult.getModifiedCount() == 0) {
            result.setState(TransactionStates.FAILED);
            result.setErrorDescription("Not enough amount available to transfer");
        } else {
            result.setState(TransactionStates.COMPLETED);
        }
        return result;
    }

    @Override
    public TransactionResult addCreditTransactionForUser(String username, Transaction transaction) {
        TransactionResult result = new TransactionResult();
        Document transactionCreditDocument = new Document()
                .append(SchemaFields.Transaction.AMOUNT, transaction.getAmount())
                .append(SchemaFields.Transaction.DEBIT, false)
                .append(SchemaFields.Transaction.STATE, transaction.getState())
                .append(SchemaFields.Transaction.TRANSACTION_ID, transaction.getId())
                .append(SchemaFields.Transaction.USERNAME, transaction.getSourceUsername());
        Document updateCreditDocument = new Document("$inc",
                new Document(SchemaFields.Account.BALANCE, transaction.getAmount()))
                .append("$push", new Document(SchemaFields.Account.TRANSACTIONS, transactionCreditDocument));
        UpdateResult updateCreditResult = collection.updateOne(
                Filters.eq(SchemaFields.Account.USERNAME, username), updateCreditDocument);
        if(updateCreditResult.isModifiedCountAvailable() && updateCreditResult.getModifiedCount() == 0) {
            result.setState(TransactionStates.FAILED);
            result.setErrorDescription("Failed to credit destination user account");
        } else {
            result.setState(TransactionStates.COMPLETED);
        }
        return result;
    }

    @Override
    public TransactionResult changeTransactionStateForUser(String username, String transactionId,
                                                           String state) {
        TransactionResult result = new TransactionResult();
        Document updateState = new Document("$set",
                new Document(SchemaFields.Account.TRANSACTIONS + ".$." + SchemaFields.Transaction.STATE, state));
        UpdateResult updateStateResult = collection.updateOne(
                Filters.and(Filters.eq(SchemaFields.Account.USERNAME, username), Filters.eq(
                        SchemaFields.Account.TRANSACTIONS + "." + SchemaFields.Transaction.TRANSACTION_ID,
                        transactionId)), updateState);
        if(updateStateResult.isModifiedCountAvailable() && updateStateResult.getModifiedCount() == 0) {
            result.setState(TransactionStates.FAILED);
            result.setErrorDescription("Failed to change transaction state to completed for source user account");
        } else {
            result.setState(TransactionStates.COMPLETED);
        }
        return result;
    }

    @Override
    public List<Transaction> listTransactionsForUser(String username) {
        Document document = collection.find(new Document(SchemaFields.Account.USERNAME, username))
                .projection(Projections.include(SchemaFields.Account.TRANSACTIONS)).first();
        if(document != null) {
            List<Transaction> transactions = new ArrayList<>();
            List<Document> transactionDocuments = (List<Document>) document.get(SchemaFields.Account.TRANSACTIONS);
            for(Document transactionDocument: transactionDocuments) {
                Transaction newTransaction = new Transaction();
                newTransaction.setAmount(transactionDocument.getLong(SchemaFields.Transaction.AMOUNT));
                newTransaction.setId(transactionDocument.getString(SchemaFields.Transaction.TRANSACTION_ID));
                newTransaction.setDebitTransaction(transactionDocument.getBoolean(SchemaFields.Transaction.DEBIT));
                if(newTransaction.isDebitTransaction()) {
                    newTransaction.setSourceUsername(username);
                    newTransaction.setDestinationUsername(
                            transactionDocument.getString(SchemaFields.Transaction.USERNAME));
                } else {
                    newTransaction.setSourceUsername(transactionDocument.getString(SchemaFields.Transaction.USERNAME));
                    newTransaction.setDestinationUsername(username);
                }
                newTransaction.setState(transactionDocument.getString(SchemaFields.Transaction.STATE));
                transactions.add(newTransaction);
            }
            return transactions;
        }
        return null;
    }

    @Override
    public User getUserDetails(String username) {
        Document document = collection.find(new Document(SchemaFields.Account.USERNAME, username))
                .projection(Projections.include(SchemaFields.Account.USERNAME)).first();
        if(document != null) {
            User user = new User();
            user.setUsername(username);
            user.setAccountId(document.getObjectId(SchemaFields.Account.ACCOUNT_ID).toString());
            return user;
        }
        return null;
    }

    @PreDestroy
    public void cleanUp() {
        mongoClient.close();
    }
}
