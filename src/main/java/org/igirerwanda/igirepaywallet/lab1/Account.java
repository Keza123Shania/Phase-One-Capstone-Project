package org.igirerwanda.igirepaywallet.lab1;

import java.time.LocalDateTime;

/**
 * Abstract base class for all account types in IgirePay.
 * Defines common fields and abstract methods that subclasses must implement.
 * 
 * ENHANCEMENTS (Lab 2 - Production-Grade Fintech):
 * - Failed PIN attempt tracking with account locking
 * - Account status lifecycle (ACTIVE, LOCKED, SUSPENDED, etc.)
 * - Balance holds for pending transfers
 * - Audit trail for compliance
 */
public abstract class Account {
    private int accountId;
    private int customerId;
    private double balance;
    private double balanceOnHold;  // Balance held for pending transfers (NEW)
    private String pin;
    private String accountType;  // WALLET, SAVINGS, etc.
    private LocalDateTime createdAt;
    private boolean isActive;
    
    // NEW: Security & Account Status Management
    private int failedPinAttempts;  // Counter for failed PIN attempts
    private String accountStatus;  // ACTIVE, LOCKED, SUSPENDED, DORMANT, CLOSED
    private LocalDateTime lockedUntil;  // Timestamp when account will be unlocked
    
    // Constants
    private static final int MAX_FAILED_PIN_ATTEMPTS = 3;
    private static final int ACCOUNT_LOCK_DURATION_MINUTES = 30;

    /**
     * Constructor for creating a new account.
     */
    public Account(int customerId, double initialBalance, String pin, String accountType) {
        this.customerId = customerId;
        this.balance = initialBalance;
        this.balanceOnHold = 0.0;
        this.pin = pin;
        this.accountType = accountType;
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
        
        // Initialize security fields
        this.failedPinAttempts = 0;
        this.accountStatus = "ACTIVE";
        this.lockedUntil = null;
    }

    /**
     * Constructor with account ID (for loading from database).
     */
    public Account(int accountId, int customerId, double balance, String pin, 
                   String accountType, LocalDateTime createdAt, boolean isActive) {
        this.accountId = accountId;
        this.customerId = customerId;
        this.balance = balance;
        this.balanceOnHold = 0.0;
        this.pin = pin;
        this.accountType = accountType;
        this.createdAt = createdAt;
        this.isActive = isActive;
        
        // Initialize security fields
        this.failedPinAttempts = 0;
        this.accountStatus = "ACTIVE";
        this.lockedUntil = null;
    }

    /**
     * Full constructor with all fields (for loading from database with security info).
     */
    public Account(int accountId, int customerId, double balance, double balanceOnHold, String pin, 
                   String accountType, LocalDateTime createdAt, boolean isActive,
                   int failedPinAttempts, String accountStatus, LocalDateTime lockedUntil) {
        this.accountId = accountId;
        this.customerId = customerId;
        this.balance = balance;
        this.balanceOnHold = balanceOnHold;
        this.pin = pin;
        this.accountType = accountType;
        this.createdAt = createdAt;
        this.isActive = isActive;
        this.failedPinAttempts = failedPinAttempts;
        this.accountStatus = accountStatus;
        this.lockedUntil = lockedUntil;
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
     * WARNING: This method does NOT track failed attempts. Use validatePinWithAttemptTracking() instead.
     */
    public boolean validatePin(String enteredPin) {
        return this.pin.equals(enteredPin);
    }

    /**
     * Validate PIN with attempt tracking and account locking.
     * This is the production-grade method that should be used in real transactions.
     * 
     * @param enteredPin The PIN entered by user
     * @return true if PIN is correct
     * @throws AccountLockedException if account is locked or will be locked after this attempt
     * @throws AccountStatusException if account is not in ACTIVE status
     */
    public boolean validatePinWithAttemptTracking(String enteredPin) 
            throws AccountLockedException, AccountStatusException {
        
        // Check account status
        if (!accountStatus.equals("ACTIVE")) {
            throw new AccountStatusException(
                "Account is " + accountStatus + " and cannot be accessed",
                accountStatus, "ACTIVE"
            );
        }
        
        // Check if account is currently locked
        if (lockedUntil != null && LocalDateTime.now().isBefore(lockedUntil)) {
            long minutesRemaining = java.time.temporal.ChronoUnit.MINUTES.between(
                LocalDateTime.now(), lockedUntil
            );
            throw new AccountLockedException(
                "Account is locked. Try again in " + minutesRemaining + " minutes.",
                failedPinAttempts,
                lockedUntil.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
            );
        }
        
        // Unlock if lock period has expired
        if (lockedUntil != null && LocalDateTime.now().isAfter(lockedUntil)) {
            this.lockedUntil = null;
            this.failedPinAttempts = 0;
        }
        
        // Check PIN
        if (!validatePin(enteredPin)) {
            this.failedPinAttempts++;
            
            // Lock account after MAX_FAILED_PIN_ATTEMPTS
            if (failedPinAttempts >= MAX_FAILED_PIN_ATTEMPTS) {
                this.lockedUntil = LocalDateTime.now().plusMinutes(ACCOUNT_LOCK_DURATION_MINUTES);
                this.accountStatus = "LOCKED";
                throw new AccountLockedException(
                    "Account locked after " + failedPinAttempts + " failed attempts. Try again in " + 
                    ACCOUNT_LOCK_DURATION_MINUTES + " minutes.",
                    failedPinAttempts,
                    lockedUntil.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                );
            }
            
            throw new IllegalArgumentException(
                "Incorrect PIN. Attempts remaining: " + (MAX_FAILED_PIN_ATTEMPTS - failedPinAttempts)
            );
        }
        
        // Reset failed attempts on successful authentication
        this.failedPinAttempts = 0;
        return true;
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

    /**
     * Lock this account for a specified duration (in minutes).
     * Called when failed PIN attempts exceed limit.
     */
    public void lockAccount(int durationMinutes) throws IllegalArgumentException {
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Lock duration must be positive");
        }
        this.accountStatus = "LOCKED";
        this.lockedUntil = LocalDateTime.now().plusMinutes(durationMinutes);
    }

    /**
     * Unlock this account immediately (admin operation).
     */
    public void unlockAccount() {
        this.accountStatus = "ACTIVE";
        this.lockedUntil = null;
        this.failedPinAttempts = 0;
    }

    /**
     * Reset failed PIN attempts counter (after successful login).
     */
    public void resetFailedAttempts() {
        this.failedPinAttempts = 0;
    }

    /**
     * Get available balance (balance minus holds for pending transfers).
     */
    public double getAvailableBalance() {
        return this.balance - this.balanceOnHold;
    }

    /**
     * Update account status with validation.
     */
    public void updateAccountStatus(String newStatus) throws IllegalArgumentException {
        String[] validStatuses = {"ACTIVE", "LOCKED", "SUSPENDED", "DORMANT", "CLOSED"};
        boolean isValid = false;
        for (String status : validStatuses) {
            if (status.equals(newStatus)) {
                isValid = true;
                break;
            }
        }
        if (!isValid) {
            throw new IllegalArgumentException("Invalid account status: " + newStatus);
        }
        this.accountStatus = newStatus;
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

    // NEW: Getters and Setters for Security Fields
    
    public double getBalanceOnHold() {
        return balanceOnHold;
    }

    public void setBalanceOnHold(double balanceOnHold) {
        if (balanceOnHold < 0) {
            throw new IllegalArgumentException("Balance on hold cannot be negative");
        }
        this.balanceOnHold = balanceOnHold;
    }

    public int getFailedPinAttempts() {
        return failedPinAttempts;
    }

    public void setFailedPinAttempts(int failedPinAttempts) {
        if (failedPinAttempts < 0) {
            throw new IllegalArgumentException("Failed attempts cannot be negative");
        }
        this.failedPinAttempts = failedPinAttempts;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public LocalDateTime getLockedUntil() {
        return lockedUntil;
    }

    public void setLockedUntil(LocalDateTime lockedUntil) {
        this.lockedUntil = lockedUntil;
    }

    public boolean isLocked() {
        return lockedUntil != null && LocalDateTime.now().isBefore(lockedUntil);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "accountId=" + accountId +
                ", customerId=" + customerId +
                ", balance=" + balance +
                ", balanceOnHold=" + balanceOnHold +
                ", accountType='" + accountType + '\'' +
                ", accountStatus='" + accountStatus + '\'' +
                ", createdAt=" + createdAt +
                ", isActive=" + isActive +
                ", failedAttempts=" + failedPinAttempts +
                ", lockedUntil=" + lockedUntil +
                '}';
    }
}
