package org.igirerwanda.igirepaywallet.lab2;

import org.igirerwanda.igirepaywallet.lab1.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class CustomerDAO {
    private Connection connection;

    /**
     * Constructor - accepts a database connection.
     */
    public CustomerDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * CREATE: Add a new customer to the database.
     * 
     * @param customer The customer object to insert
     * @return The generated customer ID, or -1 if insertion failed
     */
    public int createCustomer(Customer customer) {
        String sql = "INSERT INTO customers (full_name, email, phone_number) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Use ? placeholders to prevent SQL injection
            pstmt.setString(1, customer.getFullName());
            pstmt.setString(2, customer.getEmail());
            pstmt.setString(3, customer.getPhoneNumber());
            
            pstmt.executeUpdate();
            
            // Get the generated ID
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int customerId = rs.getInt(1);
                    System.out.println("✓ Customer created with ID: " + customerId);
                    return customerId;
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error creating customer: " + e.getMessage());
        }
        
        return -1;
    }

    /**
     * READ: Get a customer by ID.
     * 
     * @param customerId The customer ID
     * @return Customer object, or null if not found
     */
    public Customer getCustomerById(int customerId) {
        String sql = "SELECT id, full_name, email, phone_number FROM customers WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Customer(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone_number")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving customer: " + e.getMessage());
        }
        
        return null;
    }

    /**
     * READ: Get all customers from the database.
     * 
     * @return List of all customers
     */
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT id, full_name, email, phone_number FROM customers";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Customer customer = new Customer(
                    rs.getInt("id"),
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getString("phone_number")
                );
                customers.add(customer);
            }
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving customers: " + e.getMessage());
        }
        
        return customers;
    }

    /**
     * UPDATE: Update customer details.
     * 
     * @param customer The customer with updated information
     * @return true if update successful
     */
    public boolean updateCustomer(Customer customer) {
        String sql = "UPDATE customers SET full_name = ?, email = ?, phone_number = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, customer.getFullName());
            pstmt.setString(2, customer.getEmail());
            pstmt.setString(3, customer.getPhoneNumber());
            pstmt.setInt(4, customer.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✓ Customer updated successfully");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Error updating customer: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * DELETE: Delete a customer by ID.
     * 
     * @param customerId The customer ID to delete
     * @return true if deletion successful
     */
    public boolean deleteCustomer(int customerId) {
        String sql = "DELETE FROM customers WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✓ Customer deleted successfully");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Error deleting customer: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Custom Query: Get count of all customers.
     * 
     * @return Number of customers in database
     */
    public int getCustomerCount() {
        String sql = "SELECT COUNT(*) as count FROM customers";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("✗ Error getting customer count: " + e.getMessage());
        }
        
        return 0;
    }
}
