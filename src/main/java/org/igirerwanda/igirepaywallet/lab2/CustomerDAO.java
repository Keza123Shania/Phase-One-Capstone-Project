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


    public CustomerDAO(Connection connection) {
        this.connection = connection;
    }

    /**

     * @param customer The customer object to insert
     * @return The generated customer ID, or -1 if insertion failed
     */
    public int createCustomer(Customer customer) {
        String sql = "INSERT INTO customers (full_name, email, phone_number, pin) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Use ? placeholders to prevent SQL injection
            pstmt.setString(1, customer.getFullName());
            pstmt.setString(2, customer.getEmail());
            pstmt.setString(3, customer.getPhoneNumber());
            String pin = customer.getPin();
            if (pin == null || pin.isBlank()) {
                pin = "1234";
            }
            pstmt.setString(4, pin);
            
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

     * @param customerId The customer ID
     * @return Customer object, or null if not found
     */
    public Customer getCustomerById(int customerId) {
        String sql = "SELECT id, full_name, email, phone_number, pin FROM customers WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Customer(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone_number"),
                        rs.getString("pin")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving customer: " + e.getMessage());
        }
        
        return null;
    }

    /**

     * @return List of all customers
     */
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT id, full_name, email, phone_number, pin FROM customers";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Customer customer = new Customer(
                    rs.getInt("id"),
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getString("phone_number"),
                    rs.getString("pin")
                );
                customers.add(customer);
            }
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving customers: " + e.getMessage());
        }
        
        return customers;
    }

    /**

     * @param customer The customer with updated information
     * @return true if update successful
     */
    public boolean updateCustomer(Customer customer) {
        String sql = "UPDATE customers SET full_name = ?, email = ?, phone_number = ?, pin = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, customer.getFullName());
            pstmt.setString(2, customer.getEmail());
            pstmt.setString(3, customer.getPhoneNumber());
            String pin = customer.getPin();
            if (pin == null || pin.isBlank()) {
                pin = "1234";
            }
            pstmt.setString(4, pin);
            pstmt.setInt(5, customer.getId());
            
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


    public boolean updateCustomerPin(int customerId, String pin) {
        String sql = "UPDATE customers SET pin = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, pin);
            pstmt.setInt(2, customerId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("✗ Error updating customer PIN: " + e.getMessage());
            return false;
        }
    }

    /**

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
