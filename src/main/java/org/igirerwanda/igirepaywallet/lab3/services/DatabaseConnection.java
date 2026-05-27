package org.igirerwanda.igirepaywallet.lab3.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;


    private static final String DB_URL = "jdbc:postgresql://localhost:5432/igirepay_db";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "root";

    private DatabaseConnection() {

    }


    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }


    public Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ PostgreSQL driver not found: " + e.getMessage());
            throw new SQLException("PostgreSQL driver not found", e);
        }

        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("✓ Database connection established");
                ensureSchema(connection);
            } catch (SQLException e) {
                System.err.println("❌ Failed to connect to database: " + e.getMessage());
                throw e;
            }
        }
        return connection;
    }


    private void ensureSchema(Connection connection) {
        try (PreparedStatement ps = connection.prepareStatement(
                "ALTER TABLE customers ADD COLUMN IF NOT EXISTS pin VARCHAR(10)"
        )) {
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("⚠️  Schema upgrade skipped (customers.pin): " + e.getMessage());
        }
    }


    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error closing connection: " + e.getMessage());
        }
    }
}
