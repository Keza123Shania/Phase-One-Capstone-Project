package org.igirerwanda.igirepaywallet.lab1;


public class AccountLockedException extends Exception {
    private int failedAttempts;
    private long lockedUntilMillis;


    public AccountLockedException(String message, int failedAttempts, long lockedUntilMillis) {
        super(message);
        this.failedAttempts = failedAttempts;
        this.lockedUntilMillis = lockedUntilMillis;
    }


    public AccountLockedException(String message, Throwable cause) {
        super(message, cause);
    }


    public AccountLockedException(String message) {
        super(message);
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public long getLockedUntilMillis() {
        return lockedUntilMillis;
    }
}
