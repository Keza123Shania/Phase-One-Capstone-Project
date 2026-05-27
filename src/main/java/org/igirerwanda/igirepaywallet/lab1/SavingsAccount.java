package org.igirerwanda.igirepaywallet.lab1;

import java.time.LocalDateTime;


public class SavingsAccount extends Account {
    private static final double WITHDRAWAL_FEE_PERCENTAGE = 0.01;  // 1% withdrawal fee
    private static final double MINIMUM_SAVINGS_BALANCE = 10000.0;  // Minimum balance required
    private static final String ACCOUNT_TYPE = "SAVINGS";


    public SavingsAccount(int customerId, double initialBalance, String pin) {
        super(customerId, initialBalance, pin, ACCOUNT_TYPE);
    }


    public SavingsAccount(int accountId, int customerId, double balance, String pin,
                         LocalDateTime createdAt, boolean isActive) {
        super(accountId, customerId, balance, pin, ACCOUNT_TYPE, createdAt, isActive);
    }

    /**
     * Withdraw money from savings account.
     * Savings account charges 1% fee and enforces minimum balance.
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

        // Deduct from balance
        setBalance(balanceAfterWithdrawal);
        return true;
    }

    /**
     * Deposit money into savings account.
     * Savings account allows free deposits to encourage saving.
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
                // Transfer from savings charges the withdrawal fee
                return withdraw(amount, referenceId);
            default:
                throw new IllegalArgumentException("Unknown transaction type: " + transactionType);
        }
    }


    public double getMinimumBalance() {
        return MINIMUM_SAVINGS_BALANCE;
    }


    public double getWithdrawalFeePercentage() {
        return WITHDRAWAL_FEE_PERCENTAGE * 100;  // Return as percentage (e.g., 1.0 for 1%)
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
