package org.igirerwanda.igirepaywallet.lab1;


public class InvalidTransactionException extends Exception {
    private String transactionType;
    private String errorCode;


    public InvalidTransactionException(String message, String transactionType, String errorCode) {
        super(message);
        this.transactionType = transactionType;
        this.errorCode = errorCode;
    }


    public InvalidTransactionException(String message) {
        super(message);
    }


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
