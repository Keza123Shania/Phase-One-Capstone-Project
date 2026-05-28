package org.igirerwanda.igirepaywallet.lab2;

import java.time.LocalDateTime;


public class AuditLog {
    private int id;
    private int accountId;
    private String action;
    private String details;
    private String oldValue;
    private String newValue;
    private String status;
    private LocalDateTime createdAt;


    public AuditLog(int accountId, String action, String details, String status) {
        this.accountId = accountId;
        this.action = action;
        this.details = details;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }


    public AuditLog(int id, int accountId, String action, String details, 
                   String oldValue, String newValue, String status, LocalDateTime createdAt) {
        this.id = id;
        this.accountId = accountId;
        this.action = action;
        this.details = details;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.status = status;
        this.createdAt = createdAt;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "AuditLog{" +
                "id=" + id +
                ", accountId=" + accountId +
                ", action='" + action + '\'' +
                ", details='" + details + '\'' +
                ", oldValue='" + oldValue + '\'' +
                ", newValue='" + newValue + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
