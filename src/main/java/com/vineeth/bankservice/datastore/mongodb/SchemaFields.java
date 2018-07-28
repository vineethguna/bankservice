package com.vineeth.bankservice.datastore.mongodb;


class SchemaFields {
    static class Account {
        static final String ACCOUNT_ID = "_id";
        static final String USERNAME = "username";
        static final String PASSWORD = "password";
        static final String BALANCE = "balance";
        static final String BENEFICIARIES = "beneficiaries";
        static final String TRANSACTIONS = "transactions";
        static final String BENEFICIARY_LOCK = "beneficiary_lock";
    }

    static class Beneficiary {
        static final String USERNAME = "username";
        static final String ACCOUNT_ID = "account_id";
    }

    static class Transaction {
        static final String STATE = "state";
        static final String TRANSACTION_ID = "transaction_id";
        static final String DEBIT = "debit";
        static final String AMOUNT = "amount";
        static final String USERNAME = "username";
    }
}
