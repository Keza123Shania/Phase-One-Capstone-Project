package org.igirerwanda.igirepaywallet.lab2;

import org.igirerwanda.igirepaywallet.lab1.Customer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;


public class Lab2Console {
    private static Scanner scanner = new Scanner(System.in);
    private static Connection connection;
    private static CustomerDAO customerDAO;
    private static AccountDAO accountDAO;
    private static TransactionDAO transactionDAO;
    private static ProcessedRequestDAO processedRequestDAO;

    public static void run() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║  IgirePay Lab 2: Database Integration  ║");
        System.out.println("╚════════════════════════════════════════╝");

        // Test database connection
        if (!testDatabaseConnection()) {
            System.out.println("\n✗ Cannot proceed without database connection.");
            System.out.println("\nDatabase Setup Instructions:");
            System.out.println("1. Install PostgreSQL");
            System.out.println("2. Create database: CREATE DATABASE igirepay;");
            System.out.println("3. Run the schema script: src/main/resources/database_schema.sql");
            System.out.println("4. Update DatabaseConnection.java with your credentials");
            return;
        }

        // Initialize DAOs
        try {
            customerDAO = new CustomerDAO(connection);
            accountDAO = new AccountDAO(connection);
            transactionDAO = new TransactionDAO(connection);
            processedRequestDAO = new ProcessedRequestDAO(connection);

            displayMenu();

        } catch (Exception e) {
            System.err.println("✗ Error initializing application: " + e.getMessage());
        } finally {
            DatabaseConnection.closeConnection();
        }
    }

    private static boolean testDatabaseConnection() {
        try {
            connection = DatabaseConnection.getConnection();
            DatabaseConnection.printDatabaseInfo();
            return DatabaseConnection.testConnection();
        } catch (SQLException e) {
            System.err.println("✗ Database connection error: " + e.getMessage());
            return false;
        }
    }

    private static void displayMenu() {
        boolean running = true;

        while (running) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║            Lab 2 Menu                  ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.println("\n1. Customer Operations (CRUD)");
            System.out.println("2. Account Operations (CRUD)");
            System.out.println("3. Transaction Operations");
            System.out.println("4. View Database Statistics");
            System.out.println("0. Exit");
            System.out.print("\nSelect an option (0-4): ");

            try {
                int choice = getIntInput();

                switch (choice) {
                    case 1:
                        customerMenu();
                        break;
                    case 2:
                        accountMenu();
                        break;
                    case 3:
                        transactionMenu();
                        break;
                    case 4:
                        viewStatistics();
                        break;
                    case 0:
                        System.out.println("\n✓ Exiting Lab 2. Thank you!");
                        running = false;
                        break;
                    default:
                        System.out.println("\n✗ Invalid option.");
                }
            } catch (Exception e) {
                System.out.println("\n✗ Error: " + e.getMessage());
                scanner.nextLine();
            }
        }
    }

    private static void customerMenu() {
        System.out.println("\n--- Customer Operations ---");
        System.out.println("1. Create Customer");
        System.out.println("2. View Customer");
        System.out.println("3. View All Customers");
        System.out.println("4. Update Customer");
        System.out.println("5. Delete Customer");
        System.out.print("Select option: ");

        int choice = getIntInput();

        switch (choice) {
            case 1:
                createCustomer();
                break;
            case 2:
                viewCustomer();
                break;
            case 3:
                viewAllCustomers();
                break;
            case 4:
                updateCustomer();
                break;
            case 5:
                deleteCustomer();
                break;
            default:
                System.out.println("\n✗ Invalid option.");
        }
    }

    private static void createCustomer() {
        System.out.print("Full Name: ");
        String fullName = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Phone Number: ");
        String phoneNumber = scanner.nextLine().trim();

        if (fullName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty()) {
            System.out.println("\n✗ All fields required!");
            return;
        }

        Customer customer = new Customer(fullName, email, phoneNumber);
        customerDAO.createCustomer(customer);
    }

    private static void viewCustomer() {
        System.out.print("Customer ID: ");
        int customerId = getIntInput();

        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer != null) {
            System.out.println("\n✓ " + customer);
        } else {
            System.out.println("\n✗ Customer not found!");
        }
    }

    private static void viewAllCustomers() {
        List<Customer> customers = customerDAO.getAllCustomers();
        if (customers.isEmpty()) {
            System.out.println("\nNo customers found.");
        } else {
            System.out.println("\n--- All Customers ---");
            for (Customer c : customers) {
                System.out.println(c);
            }
        }
    }

    private static void updateCustomer() {
        System.out.print("Customer ID: ");
        int customerId = getIntInput();

        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer == null) {
            System.out.println("\n✗ Customer not found!");
            return;
        }

        System.out.print("New Full Name: ");
        String fullName = scanner.nextLine().trim();
        System.out.print("New Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("New Phone Number: ");
        String phoneNumber = scanner.nextLine().trim();

        customer.setFullName(fullName);
        customer.setEmail(email);
        customer.setPhoneNumber(phoneNumber);

        if (customerDAO.updateCustomer(customer)) {
            System.out.println("✓ Customer updated successfully");
        }
    }

    private static void deleteCustomer() {
        System.out.print("Customer ID: ");
        int customerId = getIntInput();

        if (customerDAO.deleteCustomer(customerId)) {
            System.out.println("✓ Customer deleted successfully");
        }
    }

    private static void accountMenu() {
        System.out.println("\n--- Account Operations ---");
        System.out.println("1. Create Account");
        System.out.println("2. View Account");
        System.out.println("3. View Accounts by Customer");
        System.out.println("4. Update Account Balance");
        System.out.println("5. Deactivate Account");
        System.out.print("Select option: ");

        int choice = getIntInput();

        switch (choice) {
            case 1:
                System.out.println("\n(Note: Use Lab 1 to create accounts. Lab 2 is for DB persistence.)");
                break;
            case 2:
                System.out.print("Account ID: ");
                int accountId = getIntInput();
                if (accountDAO.getAccountById(accountId) != null) {
                    System.out.println("✓ " + accountDAO.getAccountById(accountId));
                } else {
                    System.out.println("\n✗ Account not found!");
                }
                break;
            case 3:
                System.out.print("Customer ID: ");
                int customerId = getIntInput();
                var accounts = accountDAO.getAccountsByCustomerId(customerId);
                if (accounts.isEmpty()) {
                    System.out.println("\nNo accounts found for customer.");
                } else {
                    System.out.println("\n--- Accounts ---");
                    for (var acc : accounts) {
                        System.out.println(acc);
                    }
                }
                break;
            case 4:
                System.out.print("Account ID: ");
                accountId = getIntInput();
                System.out.print("New Balance (RWF): ");
                double balance = scanner.nextDouble();
                scanner.nextLine();
                accountDAO.updateBalance(accountId, balance);
                break;
            case 5:
                System.out.print("Account ID: ");
                accountId = getIntInput();
                accountDAO.updateAccountStatus(accountId, false);
                break;
            default:
                System.out.println("\n✗ Invalid option.");
        }
    }

    private static void transactionMenu() {
        System.out.println("\n--- Transaction Operations ---");
        System.out.println("1. View Transaction History (by Account)");
        System.out.println("2. Check Duplicate (Reference ID)");
        System.out.println("3. View Processed Request Count");
        System.out.print("Select option: ");

        int choice = getIntInput();

        switch (choice) {
            case 1:
                System.out.print("Account ID: ");
                int accountId = getIntInput();
                var transactions = transactionDAO.getTransactionsByAccountId(accountId);
                if (transactions.isEmpty()) {
                    System.out.println("\nNo transactions found for account.");
                } else {
                    System.out.println("\n--- Transaction History ---");
                    for (var txn : transactions) {
                        System.out.println(txn);
                    }
                }
                break;
            case 2:
                System.out.print("Reference ID: ");
                String refId = scanner.nextLine().trim();
                if (processedRequestDAO.isProcessed(refId)) {
                    System.out.println("\n✓ This reference ID was already processed");
                    System.out.println("  Processed at: " + processedRequestDAO.getProcessingTime(refId));
                } else {
                    System.out.println("\n✗ This reference ID has not been processed");
                }
                break;
            case 3:
                int count = processedRequestDAO.getProcessedCount();
                System.out.println("\n✓ Total processed requests: " + count);
                break;
            default:
                System.out.println("\n✗ Invalid option.");
        }
    }

    private static void viewStatistics() {
        System.out.println("\n--- Database Statistics ---");
        System.out.println("Total Customers: " + customerDAO.getCustomerCount());
        System.out.println("Total Accounts: " + accountDAO.getAllAccounts().size());
        System.out.println("Total Processed Requests: " + processedRequestDAO.getProcessedCount());
    }

    private static int getIntInput() {
        while (true) {
            try {
                int input = scanner.nextInt();
                scanner.nextLine();
                return input;
            } catch (Exception e) {
                System.out.print("✗ Invalid input. Please enter a number: ");
                scanner.nextLine();
            }
        }
    }
}
