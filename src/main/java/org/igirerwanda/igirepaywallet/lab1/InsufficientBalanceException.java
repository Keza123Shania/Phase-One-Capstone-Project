package org.igirerwanda.igirepaywallet.lab1;

/**
 * Thrown when an operation cannot complete due to insufficient available balance.
 * This includes checking balance-on-hold from pending transactions.
 */
public class InsufficientBalanceException extends Exception {
    private double requiredAmount;
    private double availableBalance;

    /**
     * Constructor with required and available amounts
     */
    public InsufficientBalanceException(String message, double requiredAmount, double availableBalance) {
        super(message);
        this.requiredAmount = requiredAmount;
        this.availableBalance = availableBalance;
    }

    /**
     * Constructor with message only
     */
    public InsufficientBalanceException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause
     */
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
