package org.igirerwanda.igirepaywallet.lab2;

import org.igirerwanda.igirepaywallet.lab1.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class TransactionDAO {
    private Connection connection;

    /**
     * Constructor - accepts a database connection.
     */
    public TransactionDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * CREATE: Add a new transaction to the database.
     * 
     * @param transaction The transaction object to insert
     * @return The generated transaction ID, or -1 if insertion failed
     */
    public int createTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (account_id, reference_id, transaction_type, amount, created_at, status, description) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, transaction.getAccountId());
            pstmt.setString(2, transaction.getReferenceId());
            pstmt.setString(3, transaction.getTransactionType());
            pstmt.setDouble(4, transaction.getAmount());
            pstmt.setObject(5, transaction.getTimestamp());
            pstmt.setString(6, transaction.getStatus());
            pstmt.setString(7, transaction.getDescription());
            
            pstmt.executeUpdate();
            
            // Get the generated ID
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int transactionId = rs.getInt(1);
                    System.out.println("✓ Transaction created with ID: " + transactionId);
                    return transactionId;
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error creating transaction: " + e.getMessage());
        }
        
        return -1;
    }

    /**
     * READ: Get a transaction by ID.
     * 
     * @param transactionId The transaction ID
     * @return Transaction object, or null if not found
     */
    public Transaction getTransactionById(int transactionId) {
        String sql = "SELECT id, account_id, reference_id, transaction_type, amount, created_at, status, description " +
                    "FROM transactions WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, transactionId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return buildTransactionFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving transaction: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * READ: Get transaction history for a specific account.
     * 
     * @param accountId The account ID
     * @return List of transactions for that account
     */
    public List<Transaction> getTransactionsByAccountId(int accountId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT id, account_id, reference_id, transaction_type, amount, created_at, status, description " +
                    "FROM transactions WHERE account_id = ? ORDER BY created_at DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, accountId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(buildTransactionFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving transactions: " + e.getMessage());
        }
        
        return transactions;
    }

    /**
     * READ: Get all transactions.
     * 
     * @return List of all transactions
     */
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT id, account_id, reference_id, transaction_type, amount, created_at, status, description " +
                    "FROM transactions ORDER BY created_at DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                transactions.add(buildTransactionFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving transactions: " + e.getMessage());
        }
        
        return transactions;
    }

    /**
     * UPDATE: Update transaction status (SUCCESS, FAILED, PENDING).
     * 
     * @param transactionId The transaction ID
     * @param status The new status
     * @return true if update successful
     */
    public boolean updateTransactionStatus(int transactionId, String status) {
        String sql = "UPDATE transactions SET status = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, transactionId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✓ Transaction status updated to: " + status);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Error updating transaction status: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * READ: Check if a reference ID exists (for duplicate detection).
     * 
     * @param referenceId The reference ID to check
     * @return true if reference ID already exists
     */
    public boolean referenceIdExists(String referenceId) {
        String sql = "SELECT COUNT(*) as count FROM transactions WHERE reference_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, referenceId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error checking reference ID: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Custom Query: Get transaction count for an account.
     * 
     * @param accountId The account ID
     * @return Number of transactions
     */
    public int getTransactionCount(int accountId) {
        String sql = "SELECT COUNT(*) as count FROM transactions WHERE account_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, accountId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error getting transaction count: " + e.getMessage());
        }
        
        return 0;
    }

    /**
     * Helper method to build a Transaction object from a ResultSet.
     */
    private Transaction buildTransactionFromResultSet(ResultSet rs) throws SQLException {
        return new Transaction(
            rs.getInt("id"),
            rs.getInt("account_id"),
            rs.getString("reference_id"),
            rs.getString("transaction_type"),
            rs.getDouble("amount"),
            rs.getObject("created_at", LocalDateTime.class),
            rs.getString("status"),
            rs.getString("description")
        );
    }
}
