package org.igirerwanda.igirepaywallet.lab1;

/**
 * Thrown when an account is locked due to multiple failed PIN attempts.
 * This prevents brute-force PIN attacks.
 */
public class AccountLockedException extends Exception {
    private int failedAttempts;
    private long lockedUntilMillis;

    /**
     * Constructor with failed attempts count
     */
    public AccountLockedException(String message, int failedAttempts, long lockedUntilMillis) {
        super(message);
        this.failedAttempts = failedAttempts;
        this.lockedUntilMillis = lockedUntilMillis;
    }

    /**
     * Constructor with message and cause
     */
    public AccountLockedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with message only
     */
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
