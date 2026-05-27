package org.igirerwanda.igirepaywallet.lab1;

/**
 * Thrown when a transaction operation is invalid.
 * Examples: Invalid amount, non-existent recipient account, expired transaction.
 */
public class InvalidTransactionException extends Exception {
    private String transactionType;
    private String errorCode;

    /**
     * Constructor with transaction type and error code
     */
    public InvalidTransactionException(String message, String transactionType, String errorCode) {
        super(message);
        this.transactionType = transactionType;
        this.errorCode = errorCode;
    }

    /**
     * Constructor with message only
     */
    public InvalidTransactionException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause
     */
    public InvalidTransactionException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
