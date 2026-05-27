package org.igirerwanda.igirepaywallet.lab3.services;

import org.igirerwanda.igirepaywallet.lab1.Account;
import org.igirerwanda.igirepaywallet.lab1.Customer;
import org.igirerwanda.igirepaywallet.lab1.Transaction;
import org.igirerwanda.igirepaywallet.lab2.*;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;


public class CustomerService {
    private CustomerDAO customerDAO;
    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;
    private AuditLogDAO auditLogDAO;
    private TransactionHoldDAO holdDAO;
    private Connection connection;

    public CustomerService() {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
            this.customerDAO = new CustomerDAO(connection);
            this.accountDAO = new AccountDAO(connection);
            this.transactionDAO = new TransactionDAO(connection);
            this.auditLogDAO = new AuditLogDAO(connection);
            this.holdDAO = new TransactionHoldDAO(connection);
        } catch (SQLException e) {
            System.err.println("❌ Failed to initialize CustomerService: " + e.getMessage());
        }
    }


    public boolean transferMoney(int customerId, String fromAccStr, String toAccStr,
                               double amount, String pin) {
        try {
            int fromAccountId = Integer.parseInt(fromAccStr.trim());
            int toAccountId = Integer.parseInt(toAccStr.trim());
            return transferMoney(customerId, fromAccountId, toAccountId, amount, pin);
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid account ID format");
            return false;
        }
    }

    public boolean transferMoney(int customerId, int fromAccountId, int toAccountId,
                                 double amount, String pin) {
        try {
            if (amount <= 0) {
                System.out.println("❌ Invalid transfer amount");
                return false;
            }
            if (fromAccountId == toAccountId) {
                System.out.println("❌ Cannot transfer to the same account");
                return false;
            }
            if (!validateCustomerPin(customerId, pin)) {
                return false;
            }

            Account sender = accountDAO.getAccountById(fromAccountId);
            if (sender == null) {
                System.out.println("❌ Sender account not found");
                return false;
            }
            if (sender.getCustomerId() != customerId) {
                System.out.println("❌ Sender account does not belong to you");
                return false;
            }

            Account receiver = accountDAO.getAccountById(toAccountId);
            if (receiver == null) {
                System.out.println("❌ Recipient account not found");
                return false;
            }

            if ("LOCKED".equals(sender.getAccountStatus()) || "SUSPENDED".equals(sender.getAccountStatus())) {
                System.out.println("❌ Your account is " + sender.getAccountStatus());
                return false;
            }

            double available = sender.getBalance() - sender.getBalanceOnHold();
            if (available < amount) {
                System.out.println("❌ Insufficient balance. Available: RWF " + available);
                return false;
            }

            String referenceId = "TRF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            if (transactionDAO.referenceIdExists(referenceId)) {
                System.out.println("❌ Duplicate reference ID");
                return false;
            }

            double newSenderBalance = sender.getBalance() - amount;
            double newReceiverBalance = receiver.getBalance() + amount;

            if (!accountDAO.updateBalance(fromAccountId, newSenderBalance)) {
                return false;
            }
            if (!accountDAO.updateBalance(toAccountId, newReceiverBalance)) {
                accountDAO.updateBalance(fromAccountId, sender.getBalance());
                return false;
            }

            Transaction sendTxn = new Transaction(fromAccountId, referenceId, "TRANSFER_OUT",
                    amount, "Transfer to account " + toAccountId);
            sendTxn.setStatus("SUCCESS");
            sendTxn.setTimestamp(LocalDateTime.now());
            transactionDAO.createTransaction(sendTxn);

            Transaction receiveTxn = new Transaction(toAccountId, referenceId + "-IN", "TRANSFER_IN",
                    amount, "Transfer from account " + fromAccountId);
            receiveTxn.setStatus("SUCCESS");
            receiveTxn.setTimestamp(LocalDateTime.now());
            transactionDAO.createTransaction(receiveTxn);

            AuditLog auditLog = new AuditLog(fromAccountId, "TRANSFER",
                    "Sent RWF " + amount + " to account " + toAccountId, "SUCCESS");
            auditLogDAO.createAuditLog(auditLog);

            System.out.println("✓ Transfer completed. Reference: " + referenceId);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error transferring money: " + e.getMessage());
            return false;
        }
    }


    public boolean initiateTransfer(int customerId, String fromAccStr, String toAccStr,
                                    double amount, String pin) {
        return transferMoney(customerId, fromAccStr, toAccStr, amount, pin);
    }

    private boolean validateCustomerPin(int customerId, String pin) {
        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer == null) {
            System.out.println("❌ Customer not found");
            return false;
        }
        String storedPin = customer.getPin();
        if (storedPin == null || storedPin.isBlank()) {
            storedPin = "1234";
            customerDAO.updateCustomerPin(customerId, storedPin);
        }
        if (!storedPin.equals(pin)) {
            System.out.println("❌ Invalid PIN");
            return false;
        }
        return true;
    }

    public boolean completeTransfer(int holdId, String pin) {
        try {
            // Get hold details
            TransactionHold hold = holdDAO.getHoldById(holdId);
            if (hold == null) {
                System.out.println("❌ Hold not found");
                return false;
            }

            if (!"ACTIVE".equals(hold.getStatus())) {
                System.out.println("❌ Hold is not active");
                return false;
            }


            Account senderAccount = accountDAO.getAccountById(hold.getAccountId());
            if (senderAccount == null) {
                System.out.println("❌ Sender account not found");
                return false;
            }


            try {
                senderAccount.validatePinWithAttemptTracking(pin);
            } catch (Exception e) {
                System.out.println("❌ Invalid PIN");
                return false;
            }

            String referenceId = hold.getReferenceId();


            Transaction sendTxn = null;
            Transaction receiveTxn = null;
            List<Transaction> txns = transactionDAO.getAllTransactions();
            for (Transaction t : txns) {
                if (referenceId.equals(t.getReferenceId())) {
                    if (t.getAccountId() == hold.getAccountId()) {
                        sendTxn = t;
                    } else {
                        receiveTxn = t;
                    }
                }
            }

            if (sendTxn == null || receiveTxn == null) {
                System.out.println("❌ Related transactions not found");
                return false;
            }


            Account receiverAccount = accountDAO.getAccountById(receiveTxn.getAccountId());
            if (receiverAccount == null) {
                System.out.println("❌ Receiver account not found");
                return false;
            }

            double amount = hold.getAmount();


            double newSenderBalance = senderAccount.getBalance() - amount;
            accountDAO.updateBalance(hold.getAccountId(), newSenderBalance);


            double newReceiverBalance = receiverAccount.getBalance() + amount;
            accountDAO.updateBalance(receiveTxn.getAccountId(), newReceiverBalance);


            double newBalanceOnHold = senderAccount.getBalanceOnHold() - amount;
            accountDAO.updateBalanceOnHold(hold.getAccountId(), newBalanceOnHold);


            transactionDAO.markTransactionAsSuccess(sendTxn.getTransactionId());
            transactionDAO.markTransactionAsSuccess(receiveTxn.getTransactionId());


            holdDAO.releaseHold(holdId, "Transfer completed");


            AuditLog auditLog = new AuditLog(0, hold.getAccountId(), "TRANSFER_COMPLETED", 
                "Transfer of RWF " + amount + " completed. New balance: RWF " + newSenderBalance, 
                "SUCCESS", "", "", LocalDateTime.now());
            auditLogDAO.createAuditLog(auditLog);

            System.out.println("✓ Transfer completed successfully!");
            System.out.println("  Sender: RWF " + newSenderBalance + " | Receiver: RWF " + newReceiverBalance);
            return true;

        } catch (Exception e) {
            System.err.println("❌ Error completing transfer: " + e.getMessage());
            return false;
        }
    }


    public boolean depositMoney(int customerId, int accountId, double amount, String pin) {
        try {
            if (amount <= 0) {
                System.out.println("❌ Invalid deposit amount");
                return false;
            }
            if (!validateCustomerPin(customerId, pin)) {
                return false;
            }

            Account account = accountDAO.getAccountById(accountId);
            if (account == null) {
                System.out.println("❌ Account not found");
                return false;
            }
            if (account.getCustomerId() != customerId) {
                System.out.println("❌ Account does not belong to you");
                return false;
            }

            String referenceId = "DEP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            Transaction txn = new Transaction(accountId, referenceId, "DEPOSIT", amount, "Cash deposit");
            txn.setStatus("SUCCESS");
            txn.setTimestamp(LocalDateTime.now());

            int txnId = transactionDAO.createTransaction(txn);
            if (txnId <= 0) {
                System.out.println("❌ Failed to create deposit transaction");
                return false;
            }

            double newBalance = account.getBalance() + amount;
            accountDAO.updateBalance(accountId, newBalance);

            AuditLog auditLog = new AuditLog(accountId, "DEPOSIT",
                    "Deposit of RWF " + amount + ". New balance: RWF " + newBalance, "SUCCESS");
            auditLogDAO.createAuditLog(auditLog);

            System.out.println("✓ Deposit of RWF " + amount + " completed. New balance: RWF " + newBalance);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error creating deposit: " + e.getMessage());
            return false;
        }
    }


    public boolean withdrawMoney(int customerId, int accountId, double amount, String pin) {
        try {
            if (amount <= 0) {
                System.out.println("❌ Invalid withdrawal amount");
                return false;
            }
            if (!validateCustomerPin(customerId, pin)) {
                return false;
            }

            Account account = accountDAO.getAccountById(accountId);
            if (account == null) {
                System.out.println("❌ Account not found");
                return false;
            }
            if (account.getCustomerId() != customerId) {
                System.out.println("❌ Account does not belong to you");
                return false;
            }

            double available = account.getBalance() - account.getBalanceOnHold();
            if (available < amount) {
                System.out.println("❌ Insufficient balance");
                return false;
            }

            String referenceId = "WTH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            Transaction txn = new Transaction(accountId, referenceId, "WITHDRAWAL", amount, "Cash withdrawal");
            txn.setStatus("SUCCESS");
            txn.setTimestamp(LocalDateTime.now());

            int txnId = transactionDAO.createTransaction(txn);
            if (txnId <= 0) {
                System.out.println("❌ Failed to create withdrawal transaction");
                return false;
            }

            double newBalance = account.getBalance() - amount;
            accountDAO.updateBalance(accountId, newBalance);

            AuditLog auditLog = new AuditLog(accountId, "WITHDRAWAL",
                    "Withdrawal of RWF " + amount + ". New balance: RWF " + newBalance, "SUCCESS");
            auditLogDAO.createAuditLog(auditLog);

            System.out.println("✓ Withdrawal of RWF " + amount + " completed. New balance: RWF " + newBalance);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error creating withdrawal: " + e.getMessage());
            return false;
        }
    }


    public String getStatement(int customerId) {
        try {
            Customer customer = customerDAO.getCustomerById(customerId);
            if (customer == null) {
                return "Customer not found";
            }

            List<Account> accounts = accountDAO.getAccountsByCustomerId(customerId);
            if (accounts == null) {
                accounts = new java.util.ArrayList<>();
            }

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            StringBuilder statement = new StringBuilder();
            statement.append("╔════════════════════════════════════════╗\n");
            statement.append("║        ACCOUNT STATEMENT              ║\n");
            statement.append("╚════════════════════════════════════════╝\n\n");
            statement.append("Customer: ").append(customer.getFullName()).append("\n");
            statement.append("Customer ID: ").append(customerId).append("\n");
            statement.append("Statement Date: ").append(now.format(formatter)).append("\n");
            statement.append("Email: ").append(customer.getEmail()).append("\n\n");

            double totalBalance = 0;
            double totalHolds = 0;

            for (Account account : accounts) {
                statement.append("=== ").append(account.getAccountType().toUpperCase()).append(" (ID: ").append(account.getAccountId()).append(") ===\n");
                statement.append("Status: ").append(account.getAccountStatus()).append("\n");
                statement.append("Balance: RWF ").append(String.format("%.2f", account.getBalance())).append("\n");
                statement.append("On Hold: RWF ").append(String.format("%.2f", account.getBalanceOnHold())).append("\n");
                statement.append("Available: RWF ").append(String.format("%.2f", account.getBalance() - account.getBalanceOnHold())).append("\n");
                statement.append("Created: ").append(account.getCreatedAt()).append("\n\n");

                totalBalance += account.getBalance();
                totalHolds += account.getBalanceOnHold();
            }

            statement.append("=== TOTAL SUMMARY ===\n");
            statement.append("Total Balance: RWF ").append(String.format("%.2f", totalBalance)).append("\n");
            statement.append("Total Holds: RWF ").append(String.format("%.2f", totalHolds)).append("\n");
            statement.append("Total Available: RWF ").append(String.format("%.2f", totalBalance - totalHolds)).append("\n");

            return statement.toString();
        } catch (Exception e) {
            System.err.println("❌ Error generating statement: " + e.getMessage());
            return "Error generating statement";
        }
    }


    public boolean exportStatement(int customerId) {
        try {
            Customer customer = customerDAO.getCustomerById(customerId);
            if (customer == null) {
                System.out.println("❌ Customer not found");
                return false;
            }

            List<Account> accounts = accountDAO.getAccountsByCustomerId(customerId);
            if (accounts == null) {
                accounts = new java.util.ArrayList<>();
            }

            String fileName = "statement_customer_" + customerId + "_" + System.currentTimeMillis() + ".csv";
            FileWriter csvWriter = new FileWriter(fileName);

            csvWriter.append("Customer ID,Account ID,Account Type,Balance,On Hold,Available,Status,Created Date\n");

            for (Account account : accounts) {
                csvWriter.append(String.valueOf(customerId))
                    .append(",").append(String.valueOf(account.getAccountId()))
                    .append(",").append(account.getAccountType())
                    .append(",").append(String.valueOf(account.getBalance()))
                    .append(",").append(String.valueOf(account.getBalanceOnHold()))
                    .append(",").append(String.valueOf(account.getBalance() - account.getBalanceOnHold()))
                    .append(",").append(account.getAccountStatus())
                    .append(",").append(account.getCreatedAt().toString())
                    .append("\n");
            }

            csvWriter.flush();
            csvWriter.close();

            System.out.println("✓ Statement exported to " + fileName);
            return true;
        } catch (IOException e) {
            System.err.println("❌ Error exporting statement: " + e.getMessage());
            return false;
        }
    }


    public List<Account> getCustomerAccounts(int customerId) {
        try {
            return accountDAO.getAccountsByCustomerId(customerId);
        } catch (Exception e) {
            System.err.println("❌ Error fetching accounts: " + e.getMessage());
            return null;
        }
    }


    public List<Transaction> getAccountTransactions(int accountId) {
        try {
            return transactionDAO.getTransactionsByAccountId(accountId);
        } catch (Exception e) {
            System.err.println("❌ Error fetching transactions: " + e.getMessage());
            return null;
        }
    }

    public List<TransactionHold> getCustomerHolds(int customerId) {
        try {
            List<Account> accounts = accountDAO.getAccountsByCustomerId(customerId);
            List<TransactionHold> allHolds = new java.util.ArrayList<>();
            
            if (accounts != null) {
                for (Account account : accounts) {
                    List<TransactionHold> holds = holdDAO.getActiveHolds(account.getAccountId());
                    if (holds != null) {
                        allHolds.addAll(holds);
                    }
                }
            }
            
            return allHolds;
        } catch (Exception e) {
            System.err.println("❌ Error fetching holds: " + e.getMessage());
            return null;
        }
    }

    /**
     * Close database connection
     */
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
