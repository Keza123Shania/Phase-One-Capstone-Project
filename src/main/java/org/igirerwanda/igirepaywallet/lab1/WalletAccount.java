package org.igirerwanda.igirepaywallet.lab1;

import java.time.LocalDateTime;


public class WalletAccount extends Account {
    private static final double WITHDRAWAL_FEE = 0.0;
    private static final double MINIMUM_BALANCE = 0.0;  
    private static final String ACCOUNT_TYPE = "WALLET";


    public WalletAccount(int customerId, double initialBalance, String pin) {
        super(customerId, initialBalance, pin, ACCOUNT_TYPE);
    }


    public WalletAccount(int accountId, int customerId, double balance, String pin,
                        LocalDateTime createdAt, boolean isActive) {
        super(accountId, customerId, balance, pin, ACCOUNT_TYPE, createdAt, isActive);
    }

    @Override
    public boolean withdraw(double amount, String referenceId) 
        throws DuplicateTransactionException {
        

        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }


        double totalDebit = amount + WITHDRAWAL_FEE;
        if (getBalance() < totalDebit) {
            throw new IllegalArgumentException("Insufficient balance for withdrawal");
        }


        setBalance(getBalance() - totalDebit);
        return true;
    }

    @Override
    public boolean deposit(double amount, String referenceId) 
        throws DuplicateTransactionException {
        

        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }


        setBalance(getBalance() + amount);
        return true;
    }

    @Override
    public boolean processTransaction(String transactionType, double amount, 
                                     String referenceId) 
        throws DuplicateTransactionException {
        
        if (transactionType == null) {
            throw new IllegalArgumentException("Transaction type cannot be null");
        }

        switch (transactionType.toUpperCase()) {
            case "DEPOSIT":
                return deposit(amount, referenceId);
            case "WITHDRAW":
                return withdraw(amount, referenceId);
            case "TRANSFER":
                return withdraw(amount, referenceId);
            default:
                throw new IllegalArgumentException("Unknown transaction type: " + transactionType);
        }
    }

    @Override
    public String toString() {
        return "WalletAccount{" +
                "accountId=" + getAccountId() +
                ", customerId=" + getCustomerId() +
                ", balance=" + getBalance() +
                ", accountType='" + getAccountType() + '\'' +
                ", createdAt=" + getCreatedAt() +
                ", isActive=" + isActive() +
                '}';
    }
}
