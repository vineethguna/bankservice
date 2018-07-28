package com.vineeth.bankservice.datastore.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import com.vineeth.bankservice.EnvironmentConstants;
import com.vineeth.bankservice.beneficiaries.Beneficiary;
import com.vineeth.bankservice.datastore.IBankStore;
import com.vineeth.bankservice.security.AuthenticationRequest;
import com.vineeth.bankservice.transactions.Transaction;
import com.vineeth.bankservice.user.UserRequest;
import org.bson.Document;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.ArrayList;

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
    public boolean addUser(UserRequest user) {
        Document newDocument = new Document()
                .append(SchemaFields.Account.USERNAME, user.getUsername())
                .append(SchemaFields.Account.PASSWORD, user.getPassword())
                .append(SchemaFields.Account.BALANCE, user.getBalance())
                .append(SchemaFields.Account.BENEFICIARIES, new ArrayList<Document>())
                .append(SchemaFields.Account.TRANSACTIONS, new ArrayList<Document>());
        Document update = new Document("$setOnInsert", newDocument);
        UpdateOptions options = new UpdateOptions();
        options.upsert(true);
        UpdateResult result = collection.updateOne(Filters.eq(SchemaFields.Account.USERNAME, user.getUsername()),
                update, options);
        return result.getUpsertedId() != null;
    }

    @Override
    public void addTransactionForUser(String username, Transaction transaction) {

    }

    @Override
    public void addBeneficiaryForUser(String username, Beneficiary beneficiary) {

    }

    @Override
    public Long getBalanceOfUser(String username) {
        return null;
    }

    @Override
    public AuthenticationRequest getAuthenticationDetailsOfUser(String username) {
        Document document = collection.find(new Document(SchemaFields.Account.USERNAME, username))
                .projection(Projections.include(SchemaFields.Account.USERNAME, SchemaFields.Account.PASSWORD)).first();
        if(document != null) {
            AuthenticationRequest authenticationRequest = new AuthenticationRequest();
            authenticationRequest.setUsername(document.getString(SchemaFields.Account.USERNAME));
            authenticationRequest.setPassword(document.getString(SchemaFields.Account.PASSWORD));
            return authenticationRequest;
        }
        return null;
    }

    @PreDestroy
    public void cleanUp() {
        mongoClient.close();
    }
}
