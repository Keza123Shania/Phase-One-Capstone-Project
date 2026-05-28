package org.igirerwanda.igirepaywallet.lab1;

import java.time.LocalDateTime;


public abstract class Account {
    private int accountId;
    private int customerId;
    private double balance;
    private double balanceOnHold;
    private String pin;
    private String accountType;
    private LocalDateTime createdAt;
    private boolean isActive;
    

    private int failedPinAttempts;
    private String accountStatus;
    private LocalDateTime lockedUntil;
    

    private static final int MAX_FAILED_PIN_ATTEMPTS = 3;
    private static final int ACCOUNT_LOCK_DURATION_MINUTES = 30;


    public Account(int customerId, double initialBalance, String pin, String accountType) {
        this.customerId = customerId;
        this.balance = initialBalance;
        this.balanceOnHold = 0.0;
        this.pin = pin;
        this.accountType = accountType;
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
        

        this.failedPinAttempts = 0;
        this.accountStatus = "ACTIVE";
        this.lockedUntil = null;
    }


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
        

        this.failedPinAttempts = 0;
        this.accountStatus = "ACTIVE";
        this.lockedUntil = null;
    }


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


    public abstract boolean withdraw(double amount, String referenceId) 
        throws DuplicateTransactionException;


    public abstract boolean deposit(double amount, String referenceId) 
        throws DuplicateTransactionException;


    public abstract boolean processTransaction(String transactionType, double amount, 
                                              String referenceId) 
        throws DuplicateTransactionException;



    public boolean validatePin(String enteredPin) {
        return this.pin.equals(enteredPin);
    }

    public boolean validatePinWithAttemptTracking(String enteredPin) 
            throws AccountLockedException, AccountStatusException {
        

        if (!accountStatus.equals("ACTIVE")) {
            throw new AccountStatusException(
                "Account is " + accountStatus + " and cannot be accessed",
                accountStatus, "ACTIVE"
            );
        }
        

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
        

        if (lockedUntil != null && LocalDateTime.now().isAfter(lockedUntil)) {
            this.lockedUntil = null;
            this.failedPinAttempts = 0;
        }
        

        if (!validatePin(enteredPin)) {
            this.failedPinAttempts++;
            

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
        

        this.failedPinAttempts = 0;
        return true;
    }


    public void changePin(String oldPin, String newPin) throws IllegalArgumentException {
        if (!validatePin(oldPin)) {
            throw new IllegalArgumentException("Current PIN is incorrect");
        }
        if (newPin == null || newPin.length() < 4) {
            throw new IllegalArgumentException("New PIN must be at least 4 characters");
        }
        this.pin = newPin;
    }


    public void lockAccount(int durationMinutes) throws IllegalArgumentException {
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Lock duration must be positive");
        }
        this.accountStatus = "LOCKED";
        this.lockedUntil = LocalDateTime.now().plusMinutes(durationMinutes);
    }


    public void unlockAccount() {
        this.accountStatus = "ACTIVE";
        this.lockedUntil = null;
        this.failedPinAttempts = 0;
    }


    public void resetFailedAttempts() {
        this.failedPinAttempts = 0;
    }


    public double getAvailableBalance() {
        return this.balance - this.balanceOnHold;
    }


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
