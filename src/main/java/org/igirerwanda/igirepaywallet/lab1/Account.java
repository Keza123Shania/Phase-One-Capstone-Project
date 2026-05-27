package org.igirerwanda.igirepaywallet.lab1;

import java.time.LocalDateTime;
public abstract class Account {
    private int accountId;
    private int customerId;
    private double balance;
    private String pin;
    private String accountType;  // WALLET, SAVINGS, etc.
    private LocalDateTime createdAt;
    private boolean isActive;

    /**
     * Constructor for creating a new account.
     */
    public Account(int customerId, double initialBalance, String pin, String accountType) {
        this.customerId = customerId;
        this.balance = initialBalance;
        this.pin = pin;
        this.accountType = accountType;
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }

    /**
     * Constructor with account ID (for loading from database).
     */
    public Account(int accountId, int customerId, double balance, String pin, 
                   String accountType, LocalDateTime createdAt, boolean isActive) {
        this.accountId = accountId;
        this.customerId = customerId;
        this.balance = balance;
        this.pin = pin;
        this.accountType = accountType;
        this.createdAt = createdAt;
        this.isActive = isActive;
    }

    // ============================================
    // Abstract Methods (to be overridden by subclasses)
    // ============================================

    /**
     * Withdraw money from the account.
     * Subclasses can override to add specific rules (e.g., fees, minimum balance).
     */
    public abstract boolean withdraw(double amount, String referenceId) 
        throws DuplicateTransactionException;

    /**
     * Deposit money into the account.
     * Subclasses can override to add specific rules if needed.
     */
    public abstract boolean deposit(double amount, String referenceId) 
        throws DuplicateTransactionException;

    /**
     * Process a generic transaction.
     * Can be overridden by subclasses for custom logic.
     */
    public abstract boolean processTransaction(String transactionType, double amount, 
                                              String referenceId) 
        throws DuplicateTransactionException;

    // ============================================
    // Concrete Methods (shared by all accounts)
    // ============================================

    /**
     * Validate PIN for security.
     */
    public boolean validatePin(String enteredPin) {

        return this.pin.equals(enteredPin);
    }

    /**
     * Change the account PIN.
     */
    public void changePin(String oldPin, String newPin) throws IllegalArgumentException {
        if (!validatePin(oldPin)) {
            throw new IllegalArgumentException("Current PIN is incorrect");
        }
        if (newPin == null || newPin.length() < 4) {
            throw new IllegalArgumentException("New PIN must be at least 4 characters");
        }
        this.pin = newPin;
    }

    // ============================================
    // Getters and Setters
    // ============================================

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "accountId=" + accountId +
                ", customerId=" + customerId +
                ", balance=" + balance +
                ", accountType='" + accountType + '\'' +
                ", createdAt=" + createdAt +
                ", isActive=" + isActive +
                '}';
    }
}
