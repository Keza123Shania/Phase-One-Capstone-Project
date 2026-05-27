package org.igirerwanda.igirepaywallet.lab2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnection {
    // Database connection details
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/igirepay_db";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "root";
    private static final String DB_DRIVER = "org.postgresql.Driver";

    // Static connection instance
    private static Connection connection = null;

    /**
     * Get a database connection.
     * Creates a new connection if one doesn't exist, or returns the existing one.
     * 
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Load PostgreSQL driver
            Class.forName(DB_DRIVER);
            
            // Create connection if not already established
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("✓ Database connection established successfully");
            }
            
            return connection;
        } catch (ClassNotFoundException e) {
            System.err.println("✗ PostgreSQL driver not found: " + e.getMessage());
            throw new SQLException("Database driver not found", e);
        } catch (SQLException e) {
            System.err.println("✗ Database connection failed: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Close the database connection.
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("✗ Error closing connection: " + e.getMessage());
        }
    }

    /**
     * Test the database connection.
     * 
     * @return true if connection is successful
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("✓ Database connection test successful");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Database connection test failed: " + e.getMessage());
        }
        return false;
    }

    /**
     * Get database configuration info.
     */
    public static void printDatabaseInfo() {
        System.out.println("\n--- Database Configuration ---");
        System.out.println("URL: " + DB_URL);
        System.out.println("User: " + DB_USER);
        System.out.println("Driver: " + DB_DRIVER);
    }
}
