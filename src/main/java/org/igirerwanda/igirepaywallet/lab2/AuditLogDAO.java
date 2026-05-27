package org.igirerwanda.igirepaywallet.lab2;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class AuditLogDAO {
    private Connection connection;


    public AuditLogDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Create a new audit log entry.
     * 
     * @param auditLog The audit log to create
     * @return The generated audit log ID
     */
    public int createAuditLog(AuditLog auditLog) {
        String sql = "INSERT INTO audit_logs (account_id, action, details, old_value, new_value, status, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, auditLog.getAccountId());
            pstmt.setString(2, auditLog.getAction());
            pstmt.setString(3, auditLog.getDetails());
            pstmt.setString(4, auditLog.getOldValue());
            pstmt.setString(5, auditLog.getNewValue());
            pstmt.setString(6, auditLog.getStatus());
            pstmt.setObject(7, auditLog.getCreatedAt());
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.err.println("✗ Error creating audit log: " + e.getMessage());
        }
        return -1;
    }


    public AuditLog getAuditLogById(int id) {
        String sql = "SELECT * FROM audit_logs WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return buildAuditLogFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving audit log: " + e.getMessage());
        }
        return null;
    }


    public List<AuditLog> getAuditLogsByAccountId(int accountId) {
        String sql = "SELECT * FROM audit_logs WHERE account_id = ? ORDER BY created_at DESC";
        List<AuditLog> logs = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, accountId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                logs.add(buildAuditLogFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving audit logs: " + e.getMessage());
        }
        return logs;
    }


    public List<AuditLog> getAuditLogsByAction(String action) {
        String sql = "SELECT * FROM audit_logs WHERE action = ? ORDER BY created_at DESC";
        List<AuditLog> logs = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, action);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                logs.add(buildAuditLogFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving audit logs by action: " + e.getMessage());
        }
        return logs;
    }


    public List<AuditLog> getAllAuditLogs(int limit) {
        String sql = "SELECT * FROM audit_logs ORDER BY created_at DESC LIMIT ?";
        List<AuditLog> logs = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                logs.add(buildAuditLogFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving all audit logs: " + e.getMessage());
        }
        return logs;
    }


    public List<AuditLog> getAuditLogsByAccountAndDateRange(int accountId, 
                                                            LocalDateTime startDate, 
                                                            LocalDateTime endDate) {
        String sql = "SELECT * FROM audit_logs WHERE account_id = ? AND created_at BETWEEN ? AND ? ORDER BY created_at DESC";
        List<AuditLog> logs = new ArrayList<>();
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, accountId);
            pstmt.setObject(2, startDate);
            pstmt.setObject(3, endDate);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                logs.add(buildAuditLogFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving audit logs by date range: " + e.getMessage());
        }
        return logs;
    }


    public int getAuditLogCount(int accountId) {
        String sql = "SELECT COUNT(*) as count FROM audit_logs WHERE account_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, accountId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("✗ Error counting audit logs: " + e.getMessage());
        }
        return 0;
    }


    public boolean deleteAuditLog(int id) {
        String sql = "DELETE FROM audit_logs WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("✗ Error deleting audit log: " + e.getMessage());
        }
        return false;
    }


    private AuditLog buildAuditLogFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int accountId = rs.getInt("account_id");
        String action = rs.getString("action");
        String details = rs.getString("details");
        String oldValue = rs.getString("old_value");
        String newValue = rs.getString("new_value");
        String status = rs.getString("status");
        LocalDateTime createdAt = rs.getObject("created_at", LocalDateTime.class);
        
        return new AuditLog(id, accountId, action, details, oldValue, newValue, status, createdAt);
    }
}
