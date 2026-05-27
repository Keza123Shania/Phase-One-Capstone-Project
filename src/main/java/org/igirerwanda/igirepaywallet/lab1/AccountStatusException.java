package org.igirerwanda.igirepaywallet.lab1;


public class AccountStatusException extends Exception {
    private String currentStatus;
    private String requiredStatus;


    public AccountStatusException(String message, String currentStatus, String requiredStatus) {
        super(message);
        this.currentStatus = currentStatus;
        this.requiredStatus = requiredStatus;
    }


    public AccountStatusException(String message) {
        super(message);
    }


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
