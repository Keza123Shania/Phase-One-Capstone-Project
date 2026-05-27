package org.igirerwanda.igirepaywallet.lab2;

import org.igirerwanda.igirepaywallet.lab1.Account;
import org.igirerwanda.igirepaywallet.lab1.WalletAccount;
import org.igirerwanda.igirepaywallet.lab1.SavingsAccount;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class AccountDAO {
    private Connection connection;

    /**
     * Constructor - accepts a database connection.
     */
    public AccountDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * CREATE: Add a new account to the database.
     * 
     * @param account The account object to insert
     * @return The generated account ID, or -1 if insertion failed
     */
    public int createAccount(Account account) {
        String sql = "INSERT INTO accounts (customer_id, account_type, balance, created_at) " +
                    "VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, account.getCustomerId());
            pstmt.setString(2, account.getAccountType());
            pstmt.setDouble(3, account.getBalance());
            pstmt.setObject(4, account.getCreatedAt());
            
            pstmt.executeUpdate();
            
            // Get the generated ID
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int accountId = rs.getInt(1);
                    System.out.println("✓ Account created with ID: " + accountId);
                    return accountId;
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error creating account: " + e.getMessage());
        }
        
        return -1;
    }

    /**
     * READ: Get an account by ID.
     * 
     * @param accountId The account ID
     * @return Account object, or null if not found
     */
    public Account getAccountById(int accountId) {
        String sql = "SELECT id, customer_id, account_type, balance, created_at, is_active " +
                    "FROM accounts WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, accountId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return buildAccountFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving account: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * READ: Get all accounts for a specific customer.
     * 
     * @param customerId The customer ID
     * @return List of accounts for that customer
     */
    public List<Account> getAccountsByCustomerId(int customerId) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT id, customer_id, account_type, balance, created_at, is_active " +
                    "FROM accounts WHERE customer_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    accounts.add(buildAccountFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving accounts: " + e.getMessage());
        }
        
        return accounts;
    }

    /**
     * READ: Get all accounts from the database.
     * 
     * @return List of all accounts
     */
    public List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT id, customer_id, account_type, balance, created_at, is_active FROM accounts";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                accounts.add(buildAccountFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving accounts: " + e.getMessage());
        }
        
        return accounts;
    }

    /**
     * UPDATE: Update account balance.
     * 
     * @param accountId The account ID
     * @param newBalance The new balance
     * @return true if update successful
     */
    public boolean updateBalance(int accountId, double newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, newBalance);
            pstmt.setInt(2, accountId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✓ Account balance updated");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Error updating balance: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * UPDATE: Update account status (active/inactive).
     * 
     * @param accountId The account ID
     * @param isActive Active status
     * @return true if update successful
     */
    public boolean updateAccountStatus(int accountId, boolean isActive) {
        String sql = "UPDATE accounts SET is_active = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setBoolean(1, isActive);
            pstmt.setInt(2, accountId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✓ Account status updated");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Error updating account status: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * DELETE: Delete an account (deactivate, don't actually delete for audit trail).
     * 
     * @param accountId The account ID
     * @return true if deletion successful
     */
    public boolean deleteAccount(int accountId) {
        return updateAccountStatus(accountId, false);
    }

    /**
     * Custom Query: Get account balance.
     * 
     * @param accountId The account ID
     * @return The current balance, or -1 if not found
     */
    public double getBalance(int accountId) {
        String sql = "SELECT balance FROM accounts WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, accountId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("balance");
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error getting balance: " + e.getMessage());
        }
        
        return -1;
    }

    /**
     * Helper method to build an Account object from a ResultSet.
     */
    private Account buildAccountFromResultSet(ResultSet rs) throws SQLException {
        int accountId = rs.getInt("id");
        int customerId = rs.getInt("customer_id");
        String accountType = rs.getString("account_type");
        double balance = rs.getDouble("balance");
        LocalDateTime createdAt = rs.getObject("created_at", LocalDateTime.class);
        boolean isActive = rs.getBoolean("is_active");
        
        if ("WALLET".equals(accountType)) {
            return new WalletAccount(accountId, customerId, balance, "****", createdAt, isActive);
        } else if ("SAVINGS".equals(accountType)) {
            return new SavingsAccount(accountId, customerId, balance, "****", createdAt, isActive);
        }
        
        return null;
    }
}
