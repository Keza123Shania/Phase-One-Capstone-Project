package org.igirerwanda.igirepaywallet.lab1;

/**
 * Thrown when an account is in an invalid status for the requested operation.
 * Examples: SUSPENDED, CLOSED, DORMANT accounts cannot perform transactions.
 */
public class AccountStatusException extends Exception {
    private String currentStatus;
    private String requiredStatus;

    /**
     * Constructor with current and required status
     */
    public AccountStatusException(String message, String currentStatus, String requiredStatus) {
        super(message);
        this.currentStatus = currentStatus;
        this.requiredStatus = requiredStatus;
    }

    /**
     * Constructor with message only
     */
    public AccountStatusException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause
     */
    public AccountStatusException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public String getRequiredStatus() {
        return requiredStatus;
    }
}
