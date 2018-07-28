package com.vineeth.bankservice.datastore.mongodb;


class SchemaFields {
    static class Account {
        static final String USERNAME = "username";
        static final String PASSWORD = "password";
        static final String BALANCE = "balance";
        static final String BENEFICIARIES = "beneficiaries";
        static final String TRANSACTIONS = "transactions";
    }
}
