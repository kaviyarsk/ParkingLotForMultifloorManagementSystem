package com.parkinglot.dao;
import com.parkinglot.config.DBConfig;
import com.parkinglot.model.ParkingSpot;
import java.sql.*;

public class ParkingSpotDAO {
    public ParkingSpot getAvailableSpot(String vehicleType) {
        String query = "SELECT parking_spot_id, floor_number, spot_number,spot_type, is_available " +
                        "FROM parking_spots WHERE spot_type = ? AND is_available = true LIMIT 1";
        try (Connection conn = DBConfig.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, vehicleType.toUpperCase());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new ParkingSpot(rs.getInt("parking_spot_id"), rs.getInt("floor_number"), rs.getInt("spot_number"), rs.getString("spot_type"), rs.getBoolean("is_available"));
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to query available parking space.");
            e.printStackTrace();
        }
        return null;
    }

    public void updateSpotStatus(int parkingSpotId, boolean available) {
        String query = "UPDATE parking_spots SET is_available = ? WHERE parking_spot_id = ?";
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setBoolean(1, available);
            ps.setInt(2, parkingSpotId);
            ps.executeUpdate();   
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to update spot availability status.");
            e.printStackTrace();
        }
    }
    
    public void displayLiveOccupancyDashboard() {
        String query = "SELECT floor_number, spot_type, " +
                        "SUM(CASE WHEN is_available = TRUE THEN 1 ELSE 0 END) AS available_spots, " +
                        "SUM(CASE WHEN is_available = FALSE THEN 1 ELSE 0 END) AS occupied_spots " +
                        "FROM parking_spots " +
                        "GROUP BY floor_number, spot_type " +
                        "ORDER BY floor_number, spot_type";
        System.out.println("\n=============================================================");
        System.out.println("          LIVE FLOOR OCCUPANCY DASHBOARD               ");
        System.out.println("=============================================================");
        System.out.printf("%-10s  | %-12s | %-15s | %-12s\n", "FLOOR", "VEHICLE TYPE", "AVAILABLE SPOTS", "OCCUPIED SPOTS");
        System.out.println("-------------------------------------------------------------");
        try (Connection conn = DBConfig.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                System.out.printf("Floor %-5d | %-12s | %-15d | %-12d\n", rs.getInt("floor_number"), rs.getString("spot_type"), rs.getInt("available_spots"), rs.getInt("occupied_spots"));
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to compile live dashboard metrics.");
            e.printStackTrace();
        }
        System.out.println("=============================================================");
    }
}
