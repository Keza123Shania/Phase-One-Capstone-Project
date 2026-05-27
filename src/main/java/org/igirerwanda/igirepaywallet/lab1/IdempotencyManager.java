package org.igirerwanda.igirepaywallet.lab1;

import java.util.HashSet;
import java.util.Set;


public class IdempotencyManager {
    private Set<String> processedReferenceIds;


    public IdempotencyManager() {
        this.processedReferenceIds = new HashSet<>();
    }

    /**
     * Check if a reference ID has already been processed.
     * 
     * @param referenceId The unique transaction reference ID
     * @return true if already processed, false if new
     */
    public boolean isDuplicate(String referenceId) {
        if (referenceId == null || referenceId.trim().isEmpty()) {
            throw new IllegalArgumentException("Reference ID cannot be null or empty");
        }
        return processedReferenceIds.contains(referenceId);
    }

    /**
     * Mark a reference ID as processed.
     * 
     * @param referenceId The unique transaction reference ID
     * @throws DuplicateTransactionException if already processed
     */
    public void markAsProcessed(String referenceId) throws DuplicateTransactionException {
        if (referenceId == null || referenceId.trim().isEmpty()) {
            throw new IllegalArgumentException("Reference ID cannot be null or empty");
        }

        if (isDuplicate(referenceId)) {
            throw new DuplicateTransactionException(
                "Transaction with reference ID '" + referenceId + "' has already been processed. " +
                "This is a duplicate request and will not be processed again."
            );
        }

        processedReferenceIds.add(referenceId);
    }


    public int getProcessedCount() {
        return processedReferenceIds.size();
    }


    public void clearAllProcessed() {
        processedReferenceIds.clear();
    }


    public Set<String> getProcessedReferenceIds() {
        return new HashSet<>(processedReferenceIds);
    }
}
