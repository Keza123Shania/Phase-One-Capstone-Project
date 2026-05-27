package org.igirerwanda.igirepaywallet.lab1;

import java.time.LocalDateTime;


public class Transaction {
    private int transactionId;
    private int accountId;
    private String referenceId;
    private String transactionType;  // DEPOSIT, WITHDRAW, TRANSFER
    private double amount;
    private LocalDateTime timestamp;
    private String status;  // SUCCESS, FAILED, PENDING
    private String description;  // Optional notes about the transaction


    public Transaction(int accountId, String referenceId, String transactionType, 
                      double amount, String description) {
        this.accountId = accountId;
        this.referenceId = referenceId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.status = "PENDING";
        this.description = description;
    }


    public Transaction(int transactionId, int accountId, String referenceId, 
                      String transactionType, double amount, LocalDateTime timestamp,
                      String status, String description) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.referenceId = referenceId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.timestamp = timestamp;
        this.status = status;
        this.description = description;
    }



    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", accountId=" + accountId +
                ", referenceId='" + referenceId + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                ", status='" + status + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
