package org.igirerwanda.igirepaywallet.lab2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnection {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/igirepay_db";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "root";
    private static final String DB_DRIVER = "org.postgresql.Driver";


    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        try {

            Class.forName(DB_DRIVER);
            

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


    public static void printDatabaseInfo() {
        System.out.println("\n--- Database Configuration ---");
        System.out.println("URL: " + DB_URL);
        System.out.println("User: " + DB_USER);
        System.out.println("Driver: " + DB_DRIVER);
    }
}
