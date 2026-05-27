package org.igirerwanda.igirepaywallet.lab1;


public class InsufficientBalanceException extends Exception {
    private double requiredAmount;
    private double availableBalance;


    public InsufficientBalanceException(String message, double requiredAmount, double availableBalance) {
        super(message);
        this.requiredAmount = requiredAmount;
        this.availableBalance = availableBalance;
    }


    public InsufficientBalanceException(String message) {
        super(message);
    }


    public InsufficientBalanceException(String message, Throwable cause) {
        super(message, cause);
    }

    public double getRequiredAmount() {
        return requiredAmount;
    }

    public double getAvailableBalance() {
        return availableBalance;
    }

    public double getShortfall() {
        return requiredAmount - availableBalance;
    }
}
