package org.igirerwanda.igirepaywallet.lab1;

import java.time.LocalDateTime;


public class WalletAccount extends Account {
    private static final double WITHDRAWAL_FEE = 0.0;  // No withdrawal fee for wallet
    private static final double MINIMUM_BALANCE = 0.0;  // Can go to zero
    private static final String ACCOUNT_TYPE = "WALLET";


    public WalletAccount(int customerId, double initialBalance, String pin) {
        super(customerId, initialBalance, pin, ACCOUNT_TYPE);
    }


    public WalletAccount(int accountId, int customerId, double balance, String pin,
                        LocalDateTime createdAt, boolean isActive) {
        super(accountId, customerId, balance, pin, ACCOUNT_TYPE, createdAt, isActive);
    }

    /**
     * Withdraw money from wallet account.
     * Wallet allows instant withdrawal with no minimum balance requirement.
     * 
     * @param amount The amount to withdraw
     * @param referenceId Unique transaction ID for idempotency
     * @return true if withdrawal successful
     * @throws DuplicateTransactionException if referenceId was already processed
     */
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

    /**
     * Deposit money into wallet account.
     * Wallet allows instant deposits.
     * 
     * @param amount The amount to deposit
     * @param referenceId Unique transaction ID for idempotency
     * @return true if deposit successful
     * @throws DuplicateTransactionException if referenceId was already processed
     */
    @Override
    public boolean deposit(double amount, String referenceId) 
        throws DuplicateTransactionException {
        

        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }


        setBalance(getBalance() + amount);
        return true;
    }

    /**
     * Process a generic transaction (DEPOSIT, WITHDRAW, TRANSFER).
     * Routes to appropriate method based on transaction type.
     * 
     * @param transactionType Type of transaction
     * @param amount Amount to process
     * @param referenceId Unique transaction ID
     * @return true if transaction successful
     * @throws DuplicateTransactionException if referenceId already processed
     */
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
                // Transfer is essentially a withdrawal from this account
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
