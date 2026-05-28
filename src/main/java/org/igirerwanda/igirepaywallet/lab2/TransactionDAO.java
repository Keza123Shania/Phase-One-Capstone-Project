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


    public TransactionDAO(Connection connection) {
        this.connection = connection;
    }

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


    public boolean markTransactionAsSuccess(int transactionId) {
        String sql = "UPDATE transactions SET status = ?, processed_at = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "SUCCESS");
            pstmt.setObject(2, LocalDateTime.now());
            pstmt.setInt(3, transactionId);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✓ Transaction " + transactionId + " marked as SUCCESS");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Error marking transaction as success: " + e.getMessage());
        }
        
        return false;
    }

    public boolean markTransactionAsFailed(int transactionId, String failureReason) {
        String sql = "UPDATE transactions SET status = ?, processed_at = ?, failure_reason = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "FAILED");
            pstmt.setObject(2, LocalDateTime.now());
            pstmt.setString(3, failureReason);
            pstmt.setInt(4, transactionId);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✓ Transaction " + transactionId + " marked as FAILED: " + failureReason);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Error marking transaction as failed: " + e.getMessage());
        }
        
        return false;
    }

    public List<Transaction> getTransactionsByStatus(String status) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT id, account_id, reference_id, transaction_type, amount, created_at, status, description " +
                    "FROM transactions WHERE status = ? ORDER BY created_at DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(buildTransactionFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving transactions by status: " + e.getMessage());
        }
        
        return transactions;
    }

    public List<Transaction> getPendingTransactions(int accountId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT id, account_id, reference_id, transaction_type, amount, created_at, status, description " +
                    "FROM transactions WHERE account_id = ? AND status = 'PENDING' ORDER BY created_at DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, accountId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(buildTransactionFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving pending transactions: " + e.getMessage());
        }
        
        return transactions;
    }

    public List<Transaction> getSuccessfulTransactions(int accountId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT id, account_id, reference_id, transaction_type, amount, created_at, status, description " +
                    "FROM transactions WHERE account_id = ? AND status = 'SUCCESS' ORDER BY created_at DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, accountId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(buildTransactionFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving successful transactions: " + e.getMessage());
        }
        
        return transactions;
    }

    public List<Transaction> getFailedTransactions(int accountId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT id, account_id, reference_id, transaction_type, amount, created_at, status, description " +
                    "FROM transactions WHERE account_id = ? AND status = 'FAILED' ORDER BY created_at DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, accountId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(buildTransactionFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving failed transactions: " + e.getMessage());
        }
        
        return transactions;
    }

    public List<Transaction> getTransactionsByDateRange(int accountId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT id, account_id, reference_id, transaction_type, amount, created_at, status, description " +
                    "FROM transactions WHERE account_id = ? AND created_at BETWEEN ? AND ? ORDER BY created_at DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, accountId);
            pstmt.setObject(2, startDate);
            pstmt.setObject(3, endDate);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(buildTransactionFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving transactions by date range: " + e.getMessage());
        }
        
        return transactions;
    }

    public int createRollbackTransaction(int originalTransactionId, int accountId, String rollbackReason) {

        Transaction original = getTransactionById(originalTransactionId);
        if (original == null) {
            System.err.println("✗ Original transaction not found for rollback");
            return -1;
        }
        

        String reverseType = original.getTransactionType().equals("DEPOSIT") ? "WITHDRAW" : "DEPOSIT";
        String referenceId = original.getReferenceId() + "_ROLLBACK_" + System.currentTimeMillis();
        String description = rollbackReason + " (Original: " + original.getReferenceId() + ")";
        
        Transaction rollbackTxn = new Transaction(
            accountId,
            referenceId,
            reverseType,
            original.getAmount(),
            description
        );
        rollbackTxn.setStatus("SUCCESS");
        

        String sql = "INSERT INTO transactions (account_id, reference_id, transaction_type, amount, created_at, status, description, reversed_by_transaction_id, rollback_reason) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, rollbackTxn.getAccountId());
            pstmt.setString(2, rollbackTxn.getReferenceId());
            pstmt.setString(3, reverseType);
            pstmt.setDouble(4, rollbackTxn.getAmount());
            pstmt.setObject(5, LocalDateTime.now());
            pstmt.setString(6, "SUCCESS");
            pstmt.setString(7, rollbackTxn.getDescription());
            pstmt.setInt(8, originalTransactionId);
            pstmt.setString(9, rollbackReason);
            
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int rollbackId = rs.getInt(1);
                    System.out.println("✓ Rollback transaction created with ID: " + rollbackId);
                    return rollbackId;
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error creating rollback transaction: " + e.getMessage());
        }
        
        return -1;
    }


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
