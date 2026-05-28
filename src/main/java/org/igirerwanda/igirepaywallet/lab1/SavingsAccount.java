package org.igirerwanda.igirepaywallet.lab1;

import java.time.LocalDateTime;


public class SavingsAccount extends Account {
    private static final double WITHDRAWAL_FEE_PERCENTAGE = 0.01;
    private static final double MINIMUM_SAVINGS_BALANCE = 10000.0;
    private static final String ACCOUNT_TYPE = "SAVINGS";


    public SavingsAccount(int customerId, double initialBalance, String pin) {
        super(customerId, initialBalance, pin, ACCOUNT_TYPE);
    }


    public SavingsAccount(int accountId, int customerId, double balance, String pin,
                         LocalDateTime createdAt, boolean isActive) {
        super(accountId, customerId, balance, pin, ACCOUNT_TYPE, createdAt, isActive);
    }

    @Override
    public boolean withdraw(double amount, String referenceId) 
        throws DuplicateTransactionException {
        

        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }


        double fee = amount * WITHDRAWAL_FEE_PERCENTAGE;
        double totalDebit = amount + fee;


        if (getBalance() < totalDebit) {
            throw new IllegalArgumentException("Insufficient balance for withdrawal (including 1% fee)");
        }


        double balanceAfterWithdrawal = getBalance() - totalDebit;
        if (balanceAfterWithdrawal < MINIMUM_SAVINGS_BALANCE) {
            throw new IllegalArgumentException(
                "Withdrawal denied: Balance cannot go below " + MINIMUM_SAVINGS_BALANCE +
                ". Your savings must be protected!"
            );
        }

        setBalance(balanceAfterWithdrawal);
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


    public double getMinimumBalance() {
        return MINIMUM_SAVINGS_BALANCE;
    }


    public double getWithdrawalFeePercentage() {
        return WITHDRAWAL_FEE_PERCENTAGE * 100;
    }

    @Override
    public String toString() {
        return "SavingsAccount{" +
                "accountId=" + getAccountId() +
                ", customerId=" + getCustomerId() +
                ", balance=" + getBalance() +
                ", minimumBalance=" + MINIMUM_SAVINGS_BALANCE +
                ", withdrawalFee=" + getWithdrawalFeePercentage() + "%" +
                ", accountType='" + getAccountType() + '\'' +
                ", createdAt=" + getCreatedAt() +
                ", isActive=" + isActive() +
                '}';
    }
}
