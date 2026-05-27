package org.igirerwanda.igirepaywallet.lab2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * TransactionHoldDAO - Data Access Object for TransactionHold
 * 
 * Manages all database operations for transaction holds:
 * - Creating holds when transfers initiated
 * - Releasing holds when transfers complete/fail
 * - Querying active holds for balance calculation
 * - Releasing expired holds
 * 
 * All queries use PreparedStatements for SQL injection prevention.
 */
public class TransactionHoldDAO {
    private Connection connection;

    /**
     * Constructor - accepts a database connection.
     */
    public TransactionHoldDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * CREATE: Place a new hold on an account.
     * 
     * @param hold The TransactionHold object to insert
     * @return The generated hold ID, or -1 if insertion failed
     */
    public int createHold(TransactionHold hold) {
        String sql = "INSERT INTO transaction_holds (account_id, amount, reference_id, hold_time, status, reason, expires_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, hold.getAccountId());
            pstmt.setDouble(2, hold.getAmount());
            pstmt.setString(3, hold.getReferenceId());
            pstmt.setObject(4, hold.getHoldTime());
            pstmt.setString(5, hold.getStatus());
            pstmt.setString(6, hold.getReason());
            pstmt.setObject(7, hold.getExpiresAt());
            
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int holdId = rs.getInt(1);
                    System.out.println("✓ Hold created with ID: " + holdId + " for account " + hold.getAccountId());
                    return holdId;
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error creating hold: " + e.getMessage());
        }
        
        return -1;
    }

    /**
     * READ: Get a hold by ID.
     * 
     * @param holdId The hold ID
     * @return TransactionHold object, or null if not found
     */
    public TransactionHold getHoldById(int holdId) {
        String sql = "SELECT id, account_id, amount, reference_id, hold_time, release_time, status, reason, release_reason, expires_at " +
                    "FROM transaction_holds WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, holdId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return buildHoldFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving hold: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * READ: Get all active holds for an account.
     * 
     * @param accountId The account ID
     * @return List of active holds on that account
     */
    public List<TransactionHold> getActiveHolds(int accountId) {
        List<TransactionHold> holds = new ArrayList<>();
        String sql = "SELECT id, account_id, amount, reference_id, hold_time, release_time, status, reason, release_reason, expires_at " +
                    "FROM transaction_holds WHERE account_id = ? AND status = 'ACTIVE' ORDER BY hold_time DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, accountId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    holds.add(buildHoldFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving active holds: " + e.getMessage());
        }
        
        return holds;
    }

    /**
     * READ: Calculate total amount on hold for an account.
     * 
     * @param accountId The account ID
     * @return Sum of all active holds
     */
    public double getTotalHeldAmount(int accountId) {
        String sql = "SELECT COALESCE(SUM(amount), 0) as total FROM transaction_holds WHERE account_id = ? AND status = 'ACTIVE'";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, accountId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error calculating held amount: " + e.getMessage());
        }
        
        return 0.0;
    }

    /**
     * UPDATE: Release a hold (mark as RELEASED with reason).
     * 
     * @param holdId The hold ID
     * @param releaseReason Reason for release (e.g., "TRANSFER_SUCCESS", "TRANSFER_FAILED")
     * @return true if update successful
     */
    public boolean releaseHold(int holdId, String releaseReason) {
        String sql = "UPDATE transaction_holds SET status = ?, release_time = ?, release_reason = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "RELEASED");
            pstmt.setObject(2, LocalDateTime.now());
            pstmt.setString(3, releaseReason);
            pstmt.setInt(4, holdId);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✓ Hold " + holdId + " released: " + releaseReason);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Error releasing hold: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * UPDATE: Release multiple holds for an account by reference IDs.
     * 
     * @param accountId The account ID
     * @param referenceIds List of reference IDs to release
     * @param releaseReason Reason for release
     * @return Number of holds released
     */
    public int releaseHoldsByReference(int accountId, List<String> referenceIds, String releaseReason) {
        int released = 0;
        
        for (String refId : referenceIds) {
            String sql = "UPDATE transaction_holds SET status = ?, release_time = ?, release_reason = ? " +
                        "WHERE account_id = ? AND reference_id = ? AND status = 'ACTIVE'";
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, "RELEASED");
                pstmt.setObject(2, LocalDateTime.now());
                pstmt.setString(3, releaseReason);
                pstmt.setInt(4, accountId);
                pstmt.setString(5, refId);
                
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    released++;
                }
            } catch (SQLException e) {
                System.err.println("✗ Error releasing hold for reference " + refId + ": " + e.getMessage());
            }
        }
        
        return released;
    }

    /**
     * DELETE: Remove a hold (admin operation or cleanup).
     * 
     * @param holdId The hold ID
     * @return true if delete successful
     */
    public boolean deleteHold(int holdId) {
        String sql = "DELETE FROM transaction_holds WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, holdId);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✓ Hold " + holdId + " deleted");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Error deleting hold: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Custom Query: Get hold by reference ID.
     * 
     * @param referenceId The reference ID (transfer ID)
     * @return TransactionHold, or null if not found
     */
    public TransactionHold getHoldByReference(String referenceId) {
        String sql = "SELECT id, account_id, amount, reference_id, hold_time, release_time, status, reason, release_reason, expires_at " +
                    "FROM transaction_holds WHERE reference_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, referenceId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return buildHoldFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving hold by reference: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Custom Query: Get all holds (active and released) for an account.
     * 
     * @param accountId The account ID
     * @return List of all holds for that account
     */
    public List<TransactionHold> getAllHoldsForAccount(int accountId) {
        List<TransactionHold> holds = new ArrayList<>();
        String sql = "SELECT id, account_id, amount, reference_id, hold_time, release_time, status, reason, release_reason, expires_at " +
                    "FROM transaction_holds WHERE account_id = ? ORDER BY hold_time DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, accountId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    holds.add(buildHoldFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving all holds: " + e.getMessage());
        }
        
        return holds;
    }

    /**
     * Custom Query: Release all expired holds.
     * Should be called periodically to auto-release holds that exceed expiry time.
     * 
     * @return Number of holds released
     */
    public int releaseExpiredHolds() {
        String sql = "UPDATE transaction_holds SET status = ?, release_time = ?, release_reason = ? " +
                    "WHERE status = 'ACTIVE' AND expires_at < ? ";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "RELEASED");
            pstmt.setObject(2, LocalDateTime.now());
            pstmt.setString(3, "EXPIRED");
            pstmt.setObject(4, LocalDateTime.now());
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✓ Released " + rowsAffected + " expired holds");
            }
            return rowsAffected;
        } catch (SQLException e) {
            System.err.println("✗ Error releasing expired holds: " + e.getMessage());
        }
        
        return 0;
    }

    /**
     * Helper method to build a TransactionHold object from a ResultSet.
     */
    private TransactionHold buildHoldFromResultSet(ResultSet rs) throws SQLException {
        return new TransactionHold(
            rs.getInt("id"),
            rs.getInt("account_id"),
            rs.getDouble("amount"),
            rs.getString("reference_id"),
            rs.getObject("hold_time", LocalDateTime.class),
            rs.getObject("release_time", LocalDateTime.class),
            rs.getString("status"),
            rs.getString("reason"),
            rs.getString("release_reason"),
            rs.getObject("expires_at", LocalDateTime.class)
        );
    }
}
