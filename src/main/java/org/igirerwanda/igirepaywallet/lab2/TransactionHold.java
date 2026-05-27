package org.igirerwanda.igirepaywallet.lab2;

import java.time.LocalDateTime;


/**
 * TransactionHold Model Class
 * 
 * Represents a hold placed on an account's balance for a pending transfer.
 * Holds ensure that the balance cannot be spent while a transfer is in progress,
 * preventing overdrafts and ensuring sufficient funds at transaction completion.
 * 
 * Use Case:
 * - Customer A initiates transfer of 5000 RWF to Customer B
 * - Customer A's balance is 10000 RWF
 * - Hold of 5000 RWF created → available balance becomes 5000 RWF
 * - If transfer succeeds: hold released, balance becomes 5000 RWF (after debit)
 * - If transfer fails: hold released, balance returns to 10000 RWF
 * 
 * Status values: ACTIVE, RELEASED, EXPIRED
 */
public class TransactionHold {
    private int id;
    private int accountId;
    private double amount;                  // Amount on hold
    private String referenceId;             // Link to transaction or transfer ID
    private LocalDateTime holdTime;         // When hold was created
    private LocalDateTime releaseTime;      // When hold was released (null if active)
    private String status;                  // ACTIVE, RELEASED, EXPIRED
    private String reason;                  // Why hold was placed (e.g., "TRANSFER_PENDING", "WITHDRAWAL_PENDING")
    private String releaseReason;           // Why hold was released (e.g., "TRANSFER_SUCCESS", "TRANSFER_FAILED")
    private LocalDateTime expiresAt;        // When hold expires (auto-release if no action)

    /**
     * Constructor for creating a new hold (without ID).
     */
    public TransactionHold(int accountId, double amount, String referenceId, String reason) {
        this.accountId = accountId;
        this.amount = amount;
        this.referenceId = referenceId;
        this.holdTime = LocalDateTime.now();
        this.status = "ACTIVE";
        this.reason = reason;
        this.releaseReason = null;
        // Set expiry to 24 hours from now (default hold duration)
        this.expiresAt = LocalDateTime.now().plusHours(24);
    }

    /**
     * Full constructor for loading from database.
     */
    public TransactionHold(int id, int accountId, double amount, String referenceId, 
                          LocalDateTime holdTime, LocalDateTime releaseTime, String status,
                          String reason, String releaseReason, LocalDateTime expiresAt) {
        this.id = id;
        this.accountId = accountId;
        this.amount = amount;
        this.referenceId = referenceId;
        this.holdTime = holdTime;
        this.releaseTime = releaseTime;
        this.status = status;
        this.reason = reason;
        this.releaseReason = releaseReason;
        this.expiresAt = expiresAt;
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public LocalDateTime getHoldTime() {
        return holdTime;
    }

    public void setHoldTime(LocalDateTime holdTime) {
        this.holdTime = holdTime;
    }

    public LocalDateTime getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(LocalDateTime releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReleaseReason() {
        return releaseReason;
    }

    public void setReleaseReason(String releaseReason) {
        this.releaseReason = releaseReason;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    /**
     * Check if hold is still active (before expiry).
     */
    public boolean isActive() {
        return "ACTIVE".equals(this.status);
    }

    /**
     * Check if hold has expired based on current time.
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

    /**
     * Get duration in seconds since hold was created.
     */
    public long getDurationSeconds() {
        LocalDateTime endTime = (releaseTime != null) ? releaseTime : LocalDateTime.now();
        return java.time.temporal.ChronoUnit.SECONDS.between(holdTime, endTime);
    }

    @Override
    public String toString() {
        return "TransactionHold{" +
                "id=" + id +
                ", accountId=" + accountId +
                ", amount=" + amount +
                ", referenceId='" + referenceId + '\'' +
                ", holdTime=" + holdTime +
                ", releaseTime=" + releaseTime +
                ", status='" + status + '\'' +
                ", reason='" + reason + '\'' +
                ", releaseReason='" + releaseReason + '\'' +
                ", expiresAt=" + expiresAt +
                '}';
    }
}
