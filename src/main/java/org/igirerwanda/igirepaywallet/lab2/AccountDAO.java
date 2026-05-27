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
        String sql = "INSERT INTO accounts (customer_id, account_type, balance, balance_on_hold, created_at, " +
                    "failed_pin_attempts, account_status, locked_until) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, account.getCustomerId());
            pstmt.setString(2, account.getAccountType());
            pstmt.setDouble(3, account.getBalance());
            pstmt.setDouble(4, account.getBalanceOnHold());
            pstmt.setObject(5, account.getCreatedAt());
            pstmt.setInt(6, account.getFailedPinAttempts());
            pstmt.setString(7, account.getAccountStatus());
            pstmt.setObject(8, account.getLockedUntil());
            
            pstmt.executeUpdate();
            

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
        String sql = "SELECT id, customer_id, account_type, balance, balance_on_hold, created_at, is_active, " +
                    "failed_pin_attempts, account_status, locked_until " +
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
        String sql = "SELECT id, customer_id, account_type, balance, balance_on_hold, created_at, is_active, " +
                    "failed_pin_attempts, account_status, locked_until " +
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
     * @return List of all accounts
     */
    public List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT id, customer_id, account_type, balance, balance_on_hold, created_at, is_active, " +
                    "failed_pin_attempts, account_status, locked_until FROM accounts";
        
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

     * @param accountId The account ID
     * @return true if deletion successful
     */
    public boolean deleteAccount(int accountId) {
        return updateAccountStatus(accountId, false);
    }

    /**

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




    public boolean updateBalanceOnHold(int accountId, double balanceOnHold) {
        String sql = "UPDATE accounts SET balance_on_hold = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, balanceOnHold);
            pstmt.setInt(2, accountId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("✗ Error updating balance on hold: " + e.getMessage());
        }
        return false;
    }


    public boolean lockAccount(int accountId, LocalDateTime lockedUntil) {
        String sql = "UPDATE accounts SET account_status = ?, locked_until = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "LOCKED");
            pstmt.setObject(2, lockedUntil);
            pstmt.setInt(3, accountId);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✓ Account " + accountId + " locked");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Error locking account: " + e.getMessage());
        }
        return false;
    }

    public boolean unlockAccount(int accountId) {
        String sql = "UPDATE accounts SET account_status = ?, locked_until = ?, failed_pin_attempts = 0 WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "ACTIVE");
            pstmt.setObject(2, null);
            pstmt.setInt(3, accountId);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✓ Account " + accountId + " unlocked");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Error unlocking account: " + e.getMessage());
        }
        return false;
    }


    public boolean updateAccountStatusField(int accountId, String newStatus) {
        String sql = "UPDATE accounts SET account_status = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, accountId);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✓ Account status updated to " + newStatus);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Error updating account status: " + e.getMessage());
        }
        return false;
    }


    public boolean updateFailedPinAttempts(int accountId, int attempts) {
        String sql = "UPDATE accounts SET failed_pin_attempts = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, attempts);
            pstmt.setInt(2, accountId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("✗ Error updating failed PIN attempts: " + e.getMessage());
        }
        return false;
    }


    public List<Account> getAccountsByStatus(String status) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT id, customer_id, account_type, balance, balance_on_hold, created_at, is_active, " +
                    "failed_pin_attempts, account_status, locked_until " +
                    "FROM accounts WHERE account_status = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    accounts.add(buildAccountFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving accounts by status: " + e.getMessage());
        }
        return accounts;
    }


    public String getAccountStatus(int accountId) {
        String sql = "SELECT account_status FROM accounts WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, accountId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("account_status");
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error getting account status: " + e.getMessage());
        }
        return null;
    }

    private Account buildAccountFromResultSet(ResultSet rs) throws SQLException {
        int accountId = rs.getInt("id");
        int customerId = rs.getInt("customer_id");
        String accountType = rs.getString("account_type");
        double balance = rs.getDouble("balance");
        double balanceOnHold = rs.getDouble("balance_on_hold");
        LocalDateTime createdAt = rs.getObject("created_at", LocalDateTime.class);
        boolean isActive = rs.getBoolean("is_active");
        int failedPinAttempts = rs.getInt("failed_pin_attempts");
        String accountStatus = rs.getString("account_status");
        LocalDateTime lockedUntil = rs.getObject("locked_until", LocalDateTime.class);
        
        Account account = null;
        if ("WALLET".equals(accountType)) {
            account = new WalletAccount(accountId, customerId, balance, "****", createdAt, isActive);
        } else if ("SAVINGS".equals(accountType)) {
            account = new SavingsAccount(accountId, customerId, balance, "****", createdAt, isActive);
        }
        

        if (account != null) {
            account.setBalanceOnHold(balanceOnHold);
            account.setFailedPinAttempts(failedPinAttempts);
            account.setAccountStatus(accountStatus);
            account.setLockedUntil(lockedUntil);
        }
        
        return account;
    }
}
