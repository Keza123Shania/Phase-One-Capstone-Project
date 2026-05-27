package org.igirerwanda.igirepaywallet.lab2;

import java.time.LocalDateTime;

/**
 * Represents an audit log entry for account and transaction operations.
 * Used for compliance, debugging, and security monitoring.
 * 
 * Every significant operation should create an AuditLog entry:
 * - PIN validations (success/failure)
 * - Account status changes
 * - Transaction processing
 * - Failed transactions
 * - Account lock/unlock events
 */
public class AuditLog {
    private int id;
    private int accountId;
    private String action;  // e.g., "PIN_VALIDATION", "TRANSACTION_DEPOSIT", "ACCOUNT_LOCKED"
    private String details;  // Descriptive details of the action
    private String oldValue;  // Previous state (for status changes)
    private String newValue;  // New state (for status changes)
    private String status;  // SUCCESS, FAILED, PENDING
    private LocalDateTime createdAt;

    /**
     * Constructor for creating a new audit log entry.
     */
    public AuditLog(int accountId, String action, String details, String status) {
        this.accountId = accountId;
        this.action = action;
        this.details = details;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Full constructor for all fields.
     */
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

    // ============================================
    // Getters and Setters
    // ============================================

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
