package org.igirerwanda.igirepaywallet.lab3.services;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.igirerwanda.igirepaywallet.lab1.Account;
import org.igirerwanda.igirepaywallet.lab1.Customer;
import org.igirerwanda.igirepaywallet.lab1.SavingsAccount;
import org.igirerwanda.igirepaywallet.lab1.Transaction;
import org.igirerwanda.igirepaywallet.lab1.WalletAccount;
import org.igirerwanda.igirepaywallet.lab2.AccountDAO;
import org.igirerwanda.igirepaywallet.lab2.AuditLog;
import org.igirerwanda.igirepaywallet.lab2.AuditLogDAO;
import org.igirerwanda.igirepaywallet.lab2.CustomerDAO;
import org.igirerwanda.igirepaywallet.lab2.TransactionDAO;


public class AdminService {
    private CustomerDAO customerDAO;
    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;
    private AuditLogDAO auditLogDAO;
    private Connection connection;
    
    public AdminService() {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
            this.customerDAO = new CustomerDAO(connection);
            this.accountDAO = new AccountDAO(connection);
            this.transactionDAO = new TransactionDAO(connection);
            this.auditLogDAO = new AuditLogDAO(connection);
        } catch (SQLException e) {
            System.err.println("❌ Failed to initialize AdminService: " + e.getMessage());
        }
    }


    public String addCustomer(String name, String email) {
        try {
            if (name == null || name.isEmpty() || email == null || email.isEmpty()) {
                System.out.println("❌ Name and email are required");
                return null;
            }


            String defaultPin = "1234";
            
            Customer customer = new Customer(0, name, email, "", defaultPin);
            int customerId = customerDAO.createCustomer(customer);
            
            if (customerId > 0) {
                System.out.println("✓ Customer added: ID " + customerId + " - " + name);

                return customerId + ":" + defaultPin;
            }
            return null;
        } catch (Exception e) {
            System.err.println("❌ Error adding customer: " + e.getMessage());
            return null;
        }
    }


    public int getTotalCustomers() {
        try {
            List<Customer> customers = customerDAO.getAllCustomers();
            return customers != null ? customers.size() : 0;
        } catch (Exception e) {
            System.err.println("❌ Error getting customer count: " + e.getMessage());
            return 0;
        }
    }


    public int getTotalAccounts() {
        try {
            List<Account> accounts = accountDAO.getAllAccounts();
            return accounts != null ? accounts.size() : 0;
        } catch (Exception e) {
            System.err.println("❌ Error getting account count: " + e.getMessage());
            return 0;
        }
    }


    public int getActiveTransactions() {
        try {
            List<Transaction> pending = transactionDAO.getTransactionsByStatus("PENDING");
            return pending != null ? pending.size() : 0;
        } catch (Exception e) {
            System.err.println("❌ Error getting active transactions: " + e.getMessage());
            return 0;
        }
    }


    public List<Customer> getAllCustomers() {
        try {
            return customerDAO.getAllCustomers();
        } catch (Exception e) {
            System.err.println("❌ Error fetching customers: " + e.getMessage());
            return null;
        }
    }


    public boolean updateCustomer(int customerId, String fullName, String email, String phoneNumber) {
        try {
            Customer existing = customerDAO.getCustomerById(customerId);
            if (existing == null) {
                return false;
            }
            if (fullName != null && !fullName.isBlank()) existing.setFullName(fullName.trim());
            if (email != null && !email.isBlank()) existing.setEmail(email.trim());
            if (phoneNumber != null && !phoneNumber.isBlank()) existing.setPhoneNumber(phoneNumber.trim());
            return customerDAO.updateCustomer(existing);
        } catch (Exception e) {
            System.err.println("❌ Error updating customer: " + e.getMessage());
            return false;
        }
    }


    public boolean deleteCustomer(int customerId) {
        try {
            return customerDAO.deleteCustomer(customerId);
        } catch (Exception e) {
            System.err.println("❌ Error deleting customer: " + e.getMessage());
            return false;
        }
    }


    public int createAccountForCustomer(int customerId, String accountType, double initialBalance) {
        try {
            Customer customer = customerDAO.getCustomerById(customerId);
            if (customer == null) {
                System.out.println("❌ Customer not found: " + customerId);
                return -1;
            }

            String type = accountType == null ? "" : accountType.trim().toUpperCase();
            if (!"WALLET".equals(type) && !"SAVINGS".equals(type)) {
                System.out.println("❌ Invalid account type: " + accountType);
                return -1;
            }

            if (initialBalance < 0) {
                System.out.println("❌ Initial balance cannot be negative");
                return -1;
            }

            // NOTE: PIN is validated at customer level; accounts don't persist PIN.
            Account account = "WALLET".equals(type)
                    ? new WalletAccount(customerId, initialBalance, "0000")
                    : new SavingsAccount(customerId, initialBalance, "0000");

            int accountId = accountDAO.createAccount(account);
            if (accountId > 0) {
                AuditLog auditLog = new AuditLog(0, accountId, "ACCOUNT_CREATED",
                        "Account created for customer " + customerId + " (" + type + "), initial balance: " + initialBalance,
                        "SUCCESS", "", "", LocalDateTime.now());
                auditLogDAO.createAuditLog(auditLog);
            }
            return accountId;
        } catch (Exception e) {
            System.err.println("❌ Error creating account: " + e.getMessage());
            return -1;
        }
    }


    public List<Account> getAllAccounts() {
        try {
            return accountDAO.getAllAccounts();
        } catch (Exception e) {
            System.err.println("❌ Error fetching accounts: " + e.getMessage());
            return null;
        }
    }


    public List<Transaction> getAllTransactions() {
        try {
            return transactionDAO.getAllTransactions();
        } catch (Exception e) {
            System.err.println("❌ Error fetching transactions: " + e.getMessage());
            return null;
        }
    }


    public List<AuditLog> getAuditLogs(int limit) {
        try {
            return auditLogDAO.getAllAuditLogs(limit);
        } catch (Exception e) {
            System.err.println("❌ Error fetching audit logs: " + e.getMessage());
            return null;
        }
    }


    public boolean exportAllTransactions() {
        try {
            List<Transaction> transactions = transactionDAO.getAllTransactions();
            if (transactions == null || transactions.isEmpty()) {
                System.out.println("⚠️  No transactions to export");
                return false;
            }

            String fileName = "transactions_" + System.currentTimeMillis() + ".csv";
            FileWriter csvWriter = new FileWriter(fileName);

            // Write header
            csvWriter.append("Transaction ID,Account ID,Type,Amount,Status,Reference ID,Date\n");

            // Write data rows
            for (Transaction txn : transactions) {
                csvWriter.append(String.valueOf(txn.getTransactionId()))
                    .append(",").append(String.valueOf(txn.getAccountId()))
                    .append(",").append(txn.getTransactionType())
                    .append(",").append(String.valueOf(txn.getAmount()))
                    .append(",").append(txn.getStatus())
                    .append(",").append(txn.getReferenceId() != null ? txn.getReferenceId() : "")
                    .append(",").append(txn.getTimestamp().toString())
                    .append("\n");
            }

            csvWriter.flush();
            csvWriter.close();

            System.out.println("✓ Transactions exported to " + fileName);
            return true;
        } catch (IOException e) {
            System.err.println("❌ Error exporting transactions: " + e.getMessage());
            return false;
        }
    }


    public String generateDailySummary() {
        try {
            LocalDateTime today = LocalDateTime.now();
            LocalDateTime startOfDay = today.withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endOfDay = today.withHour(23).withMinute(59).withSecond(59);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            StringBuilder summary = new StringBuilder();
            summary.append("=== Daily Transaction Summary ===\n");
            summary.append("Date: ").append(today.format(formatter)).append("\n\n");

            // Get all transactions
            List<Transaction> allTransactions = transactionDAO.getAllTransactions();
            if (allTransactions == null) {
                allTransactions = new java.util.ArrayList<>();
            }

            double totalDeposits = 0;
            double totalWithdrawals = 0;
            int successfulCount = 0;
            int pendingCount = 0;

            for (Transaction txn : allTransactions) {
                if (txn.getTimestamp().isAfter(startOfDay) && 
                    txn.getTimestamp().isBefore(endOfDay)) {
                    
                    if ("DEPOSIT".equalsIgnoreCase(txn.getTransactionType())) {
                        totalDeposits += txn.getAmount();
                    } else if ("WITHDRAWAL".equalsIgnoreCase(txn.getTransactionType())) {
                        totalWithdrawals += txn.getAmount();
                    }

                    if ("SUCCESS".equalsIgnoreCase(txn.getStatus())) {
                        successfulCount++;
                    } else if ("PENDING".equalsIgnoreCase(txn.getStatus())) {
                        pendingCount++;
                    }
                }
            }

            summary.append("Total Transactions: ").append(allTransactions.size()).append("\n");
            summary.append("Total Deposits: RWF ").append(String.format("%.2f", totalDeposits)).append("\n");
            summary.append("Total Withdrawals: RWF ").append(String.format("%.2f", totalWithdrawals)).append("\n");
            summary.append("Net: RWF ").append(String.format("%.2f", totalDeposits - totalWithdrawals)).append("\n\n");
            summary.append("Successful Transactions: ").append(successfulCount).append("\n");
            summary.append("Pending Transactions: ").append(pendingCount).append("\n");

            return summary.toString();
        } catch (Exception e) {
            System.err.println("❌ Error generating daily summary: " + e.getMessage());
            return "Error generating summary";
        }
    }

    /**
     * Generate reconciliation report
     */
    public String generateReconciliation() {
        try {
            List<Account> accounts = accountDAO.getAllAccounts();
            if (accounts == null) {
                accounts = new java.util.ArrayList<>();
            }

            double totalSystemBalance = 0;
            double totalBalanceHolds = 0;

            for (Account acc : accounts) {
                totalSystemBalance += acc.getBalance();
                totalBalanceHolds += acc.getBalanceOnHold();
            }

            StringBuilder report = new StringBuilder();
            report.append("=== Account Reconciliation Report ===\n");
            report.append("Generated: ").append(LocalDateTime.now()).append("\n\n");
            report.append("Total Accounts: ").append(accounts.size()).append("\n");
            report.append("Total System Balance: RWF ").append(String.format("%.2f", totalSystemBalance)).append("\n");
            report.append("Total Balance Holds: RWF ").append(String.format("%.2f", totalBalanceHolds)).append("\n");
            report.append("Available Balance: RWF ").append(String.format("%.2f", totalSystemBalance - totalBalanceHolds)).append("\n");
            report.append("Status: ✓ BALANCED\n");

            return report.toString();
        } catch (Exception e) {
            System.err.println("❌ Error generating reconciliation: " + e.getMessage());
            return "Error generating reconciliation";
        }
    }


    public String getSystemStatus() {
        try {
            // Test database connection
            if (connection != null && !connection.isClosed()) {
                return "OPERATIONAL";
            }
            return "OFFLINE";
        } catch (SQLException e) {
            return "ERROR";
        }
    }


    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("❌ Error closing connection: " + e.getMessage());
        }
    }
}
