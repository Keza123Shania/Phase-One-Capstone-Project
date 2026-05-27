package org.igirerwanda.igirepaywallet.lab2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class ProcessedRequestDAO {
    private Connection connection;


    public ProcessedRequestDAO(Connection connection) {
        this.connection = connection;
    }


    public int markAsProcessed(String referenceId) {
        String sql = "INSERT INTO processed_requests (reference_id, processed_at) VALUES (?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, referenceId);
            pstmt.setObject(2, LocalDateTime.now());
            
            pstmt.executeUpdate();
            
            // Get the generated ID
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    System.out.println("✓ Request marked as processed: " + referenceId);
                    return id;
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error marking request as processed: " + e.getMessage());
        }
        
        return -1;
    }

    /**

     * @param referenceId The reference ID to check
     * @return true if the reference ID was already processed
     */
    public boolean isProcessed(String referenceId) {
        String sql = "SELECT COUNT(*) as count FROM processed_requests WHERE reference_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, referenceId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error checking if request is processed: " + e.getMessage());
        }
        
        return false;
    }

    /**

     * @param referenceId The reference ID
     * @return LocalDateTime when it was processed, or null if not found
     */
    public LocalDateTime getProcessingTime(String referenceId) {
        String sql = "SELECT processed_at FROM processed_requests WHERE reference_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, referenceId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getObject("processed_at", LocalDateTime.class);
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error getting processing time: " + e.getMessage());
        }
        
        return null;
    }


    public List<String> getAllProcessedRequests() {
        List<String> referenceIds = new ArrayList<>();
        String sql = "SELECT reference_id FROM processed_requests ORDER BY processed_at DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                referenceIds.add(rs.getString("reference_id"));
            }
        } catch (SQLException e) {
            System.err.println("✗ Error retrieving processed requests: " + e.getMessage());
        }
        
        return referenceIds;
    }

    /**

     * @return Number of processed requests
     */
    public int getProcessedCount() {
        String sql = "SELECT COUNT(*) as count FROM processed_requests";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("✗ Error getting processed count: " + e.getMessage());
        }
        
        return 0;
    }

    /**

     * @param referenceId The reference ID to remove
     * @return true if deletion successful
     */
    public boolean deleteProcessedRequest(String referenceId) {
        String sql = "DELETE FROM processed_requests WHERE reference_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, referenceId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✓ Processed request deleted");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Error deleting processed request: " + e.getMessage());
        }
        
        return false;
    }
}
