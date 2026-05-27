package org.igirerwanda.igirepaywallet.lab1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class Lab1Console {
    private static Scanner scanner = new Scanner(System.in);
    private static List<Customer> customers = new ArrayList<>();
    private static Map<Integer, Account> accounts = new HashMap<>();
    private static IdempotencyManager idempotencyManager = new IdempotencyManager();
    private static int nextCustomerId = 1;
    private static int nextAccountId = 1;

    public static void run() {
        boolean running = true;
        
        while (running) {
            displayMainMenu();
            int choice = getIntInput("Select an option (0-5): ");
            
            switch (choice) {
                case 1:
                    createCustomer();
                    break;
                case 2:
                    createAccount();
                    break;
                case 3:
                    performTransaction();
                    break;
                case 4:
                    viewAccountDetails();
                    break;
                case 5:
                    viewAllCustomers();
                    break;
                case 0:
                    System.out.println("\n✓ Exiting Lab 1. Thank you!");
                    running = false;
                    break;
                default:
                    System.out.println("\n✗ Invalid option. Please try again.");
            }
        }
    }

    private static void displayMainMenu() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║  IgirePay Lab 1: Interactive Testing   ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("\n1. Create Customer");
        System.out.println("2. Create Account");
        System.out.println("3. Perform Transaction");
        System.out.println("4. View Account Details");
        System.out.println("5. View All Customers");
        System.out.println("0. Exit");
    }

    private static void createCustomer() {
        System.out.println("\n--- Create New Customer ---");
        
        System.out.print("Full Name: ");
        String fullName = scanner.nextLine().trim();
        
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        
        System.out.print("Phone Number: ");
        String phoneNumber = scanner.nextLine().trim();
        
        if (fullName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty()) {
            System.out.println("\n✗ All fields are required!");
            return;
        }
        
        Customer customer = new Customer(nextCustomerId++, fullName, email, phoneNumber);
        customers.add(customer);
        System.out.println("\n✓ Customer created successfully!");
        System.out.println("  Customer ID: " + customer.getId());
        System.out.println("  " + customer);
    }

    private static void createAccount() {
        System.out.println("\n--- Create New Account ---");
        
        System.out.println("Available Customers:");
        if (customers.isEmpty()) {
            System.out.println("✗ No customers available. Please create a customer first.");
            return;
        }
        
        for (Customer c : customers) {
            System.out.println("  ID: " + c.getId() + " | Name: " + c.getFullName());
        }
        
        int customerId = getIntInput("Select Customer ID: ");
        Customer customer = findCustomerById(customerId);
        
        if (customer == null) {
            System.out.println("\n✗ Customer not found!");
            return;
        }
        
        System.out.print("Account Type (1=WALLET, 2=SAVINGS): ");
        int accountType = getIntInput("Choice: ");
        
        System.out.print("Initial Balance (RWF): ");
        double initialBalance = scanner.nextDouble();
        scanner.nextLine();  // Consume newline
        
        System.out.print("Account PIN (4+ digits): ");
        String pin = scanner.nextLine().trim();
        
        if (pin.length() < 4) {
            System.out.println("\n✗ PIN must be at least 4 characters!");
            return;
        }
        
        Account account;
        if (accountType == 1) {
            account = new WalletAccount(customerId, initialBalance, pin);
            account.setAccountId(nextAccountId);
            System.out.println("\n✓ Wallet Account created!");
        } else if (accountType == 2) {
            account = new SavingsAccount(customerId, initialBalance, pin);
            account.setAccountId(nextAccountId);
            System.out.println("\n✓ Savings Account created!");
        } else {
            System.out.println("\n✗ Invalid account type!");
            return;
        }
        
        nextAccountId++;
        accounts.put(account.getAccountId(), account);
        System.out.println("  Account ID: " + account.getAccountId());
        System.out.println("  Balance: " + initialBalance + " RWF");
        System.out.println("  " + account);
    }

    private static void performTransaction() {
        System.out.println("\n--- Perform Transaction ---");
        
        System.out.println("Available Accounts:");
        if (accounts.isEmpty()) {
            System.out.println("✗ No accounts available!");
            return;
        }
        
        for (Account acc : accounts.values()) {
            Customer customer = findCustomerById(acc.getCustomerId());
            String customerName = customer != null ? customer.getFullName() : "Unknown";
            System.out.println("  Account ID: " + acc.getAccountId() + " | " + 
                             customerName + " | " + acc.getAccountType() + 
                             " | Balance: " + acc.getBalance() + " RWF");
        }
        
        int accountId = getIntInput("Select Account ID: ");
        Account account = accounts.get(accountId);
        
        if (account == null) {
            System.out.println("\n✗ Account not found!");
            return;
        }
        
        System.out.print("Enter PIN: ");
        String pin = scanner.nextLine().trim();
        
        if (!account.validatePin(pin)) {
            System.out.println("\n✗ Incorrect PIN!");
            return;
        }
        
        System.out.println("\n1. Deposit");
        System.out.println("2. Withdraw");
        int txnType = getIntInput("Select transaction type: ");
        
        System.out.print("Amount (RWF): ");
        double amount = scanner.nextDouble();
        scanner.nextLine();  // Consume newline
        
        System.out.print("Reference ID (e.g., TXN-001): ");
        String referenceId = scanner.nextLine().trim();
        
        if (referenceId.isEmpty()) {
            System.out.println("\n✗ Reference ID is required!");
            return;
        }
        
        try {

            if (idempotencyManager.isDuplicate(referenceId)) {
                System.out.println("\n✗ DUPLICATE DETECTED!");
                System.out.println("  This transaction reference was already processed.");
                return;
            }
            
            boolean success = false;
            if (txnType == 1) {
                success = account.deposit(amount, referenceId);
            } else if (txnType == 2) {
                success = account.withdraw(amount, referenceId);
            } else {
                System.out.println("\n✗ Invalid transaction type!");
                return;
            }
            
            if (success) {
                idempotencyManager.markAsProcessed(referenceId);
                System.out.println("\n✓ Transaction successful!");
                System.out.println("  New Balance: " + account.getBalance() + " RWF");
            }
            
        } catch (DuplicateTransactionException e) {
            System.out.println("\n✗ Error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("\n✗ Error: " + e.getMessage());
        }
    }

    private static void viewAccountDetails() {
        System.out.println("\n--- View Account Details ---");
        
        if (accounts.isEmpty()) {
            System.out.println("✗ No accounts available!");
            return;
        }
        
        System.out.println("Available Accounts:");
        for (Account acc : accounts.values()) {
            Customer customer = findCustomerById(acc.getCustomerId());
            String customerName = customer != null ? customer.getFullName() : "Unknown";
            System.out.println("  Account ID: " + acc.getAccountId() + " | " + customerName);
        }
        
        int accountId = getIntInput("Select Account ID: ");
        Account account = accounts.get(accountId);
        
        if (account == null) {
            System.out.println("\n✗ Account not found!");
            return;
        }
        
        System.out.println("\n--- Account Details ---");
        System.out.println("Account ID: " + account.getAccountId());
        System.out.println("Account Type: " + account.getAccountType());
        System.out.println("Balance: " + account.getBalance() + " RWF");
        System.out.println("Created At: " + account.getCreatedAt());
        System.out.println("Active: " + (account.isActive() ? "Yes" : "No"));
        
        if (account instanceof SavingsAccount) {
            SavingsAccount savings = (SavingsAccount) account;
            System.out.println("Minimum Balance Requirement: " + savings.getMinimumBalance() + " RWF");
            System.out.println("Withdrawal Fee: " + savings.getWithdrawalFeePercentage() + "%");
        }
    }

    private static void viewAllCustomers() {
        System.out.println("\n--- All Customers ---");
        
        if (customers.isEmpty()) {
            System.out.println("No customers yet.");
            return;
        }
        
        for (Customer c : customers) {
            System.out.println("  ID: " + c.getId());
            System.out.println("  " + c);
            
            List<Account> customerAccounts = new ArrayList<>();
            for (Account acc : accounts.values()) {
                if (acc.getCustomerId() == c.getId()) {
                    customerAccounts.add(acc);
                }
            }
            
            if (customerAccounts.isEmpty()) {
                System.out.println("  Accounts: None");
            } else {
                System.out.println("  Accounts:");
                for (Account acc : customerAccounts) {
                    System.out.println("    - Account ID: " + acc.getAccountId() + 
                                     " | Type: " + acc.getAccountType() + 
                                     " | Balance: " + acc.getBalance() + " RWF");
                }
            }
            System.out.println();
        }
    }

    private static Customer findCustomerById(int id) {
        for (Customer c : customers) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int input = scanner.nextInt();
                scanner.nextLine();  // Consume newline
                return input;
            } catch (Exception e) {
                System.out.println("✗ Invalid input. Please enter a number.");
                scanner.nextLine();  // Clear invalid input
            }
        }
    }
}
