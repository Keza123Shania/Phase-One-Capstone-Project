package org.igirerwanda.igirepaywallet.lab3.services;

import java.sql.Connection;
import java.sql.SQLException;

import org.igirerwanda.igirepaywallet.lab1.Customer;
import org.igirerwanda.igirepaywallet.lab2.AccountDAO;
import org.igirerwanda.igirepaywallet.lab2.CustomerDAO;


public class AuthService {
    private CustomerDAO customerDAO;
    private AccountDAO accountDAO;
    private Connection connection;

    public AuthService() {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
            this.customerDAO = new CustomerDAO(connection);
            this.accountDAO = new AccountDAO(connection);
        } catch (SQLException e) {
            System.err.println("❌ Failed to initialize AuthService: " + e.getMessage());
        }
    }


    public boolean authenticateAdmin(String username, String password) {
        try {

            if (username == null || password == null) {
                return false;
            }
            

            boolean isValid = username.equals("admin") && password.equals("admin123");
            
            if (isValid) {
                System.out.println("✓ Admin '" + username + "' authenticated successfully");
            } else {
                System.out.println("❌ Invalid admin credentials for user: " + username);
            }
            
            return isValid;
        } catch (Exception e) {
            System.err.println("❌ Error authenticating admin: " + e.getMessage());
            return false;
        }
    }


    public boolean authenticateCustomer(String customerId, String pin) {
        try {
            if (customerId == null || pin == null) {
                System.out.println("❌ Customer ID and PIN cannot be null");
                return false;
            }


            int custId = Integer.parseInt(customerId);


            if (!pin.matches("\\d{4}")) {
                System.out.println("❌ PIN must be 4 digits");
                return false;
            }


            Customer customer = customerDAO.getCustomerById(custId);
            if (customer == null) {
                System.out.println("❌ Customer not found: " + custId);
                return false;
            }


            String storedPin = customer.getPin();
            if (storedPin == null || storedPin.isBlank()) {

                storedPin = "1234";
                customerDAO.updateCustomerPin(custId, storedPin);
            }
            
            if (!storedPin.equals(pin)) {
                System.out.println("❌ Invalid PIN for customer: " + custId);
                return false;
            }


            System.out.println("✓ Customer " + custId + " authenticated successfully");
            return true;

        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid Customer ID format");
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error authenticating customer: " + e.getMessage());
            return false;
        }
    }


    public int registerCustomer(String name, String email, String phoneNumber) {
        try {
            if (name == null || email == null || name.isEmpty() || email.isEmpty()) {
                System.out.println("❌ Name and email are required");
                return -1;
            }

            Customer customer = new Customer(0, name, email, phoneNumber);
            int customerId = customerDAO.createCustomer(customer);

            if (customerId > 0) {
                System.out.println("✓ Customer registered: ID " + customerId);
            }

            return customerId;
        } catch (Exception e) {
            System.err.println("❌ Error registering customer: " + e.getMessage());
            return -1;
        }
    }

    public boolean validatePinFormat(String pin) {
        return pin != null && pin.matches("\\d{4}");
    }


    public Customer getCustomerDetails(int customerId) {
        try {
            return customerDAO.getCustomerById(customerId);
        } catch (Exception e) {
            System.err.println("❌ Error getting customer details: " + e.getMessage());
            return null;
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
