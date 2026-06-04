package com.parkinglot.dao;

import com.parkinglot.config.DBConfig;
import com.parkinglot.model.Ticket;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TicketDAO {

    public int getNextDailySequence() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cutoffToday = LocalDateTime.of(now.toLocalDate(), LocalTime.of(6, 0));

        LocalDateTime businessDayStart= now.isBefore(cutoffToday) ? cutoffToday.minusDays(1) : cutoffToday;
        
        String query = "SELECT MAX(daily_ticket_seq) FROM tickets WHERE entry_time >= ?";
        
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setTimestamp(1, Timestamp.valueOf(businessDayStart));
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1; 
    }
        
    public boolean saveTicket(Ticket ticket) {
        int nextSeq = getNextDailySequence();
        ticket.setDailyTicketSeq(nextSeq);
        String query = "INSERT INTO tickets (daily_ticket_seq, parking_spot_id, vehicle_number, pricing_strategy) " +
                   "VALUES (?, ?, ?, ?)";     
    
        try (Connection conn = DBConfig.getConnection();
            PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
        
            ps.setInt(1, ticket.getDailyTicketSeq());   
            ps.setInt(2, ticket.getParkingSpotId());           
            ps.setString(3, ticket.getVehicleNumber());
            ps.setString(4, ticket.getPricingStrategy());
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        ticket.setTicketId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Ticket getActiveTicketByPlate(String vehicleNumber) {
        String query = "SELECT t.ticket_id, t.daily_ticket_seq, t.parking_spot_id, t.vehicle_number, " +
                       "t.entry_time, t.exit_time, t.amount_paid,t.pricing_strategy, p.floor_number, p.spot_number, p.spot_type,t.payment_type " +
                       "FROM tickets t JOIN parking_spots p ON t.parking_spot_id = p.parking_spot_id " +
                       "WHERE t.vehicle_number = UPPER(?) AND t.exit_time IS NULL";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, vehicleNumber.trim());
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return new Ticket(
                    rs.getInt("ticket_id"),
                    rs.getInt("daily_ticket_seq"),
                    rs.getInt("parking_spot_id"),
                    rs.getString("vehicle_number"),
                    rs.getTimestamp("entry_time"),
                    rs.getTimestamp("exit_time"),
                    rs.getDouble("amount_paid"),
                    rs.getString("pricing_strategy"),
                    rs.getInt("floor_number"),
                    rs.getInt("spot_number"),
                    rs.getString("spot_type"),
                    rs.getString("payment_type")
                );
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to fetch active ticket with provided sequence and license plate.");
            e.printStackTrace();
        }
        return null;
    }

    public boolean isVehicleAlreadyParked(String vehicleNumber) {
        String query = "SELECT COUNT(*) FROM tickets WHERE vehicle_number = ? AND exit_time IS NULL";
        try (Connection conn = DBConfig.getConnection();
            PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, vehicleNumber.toUpperCase());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void closeTicket(int ticketId, double amount, String paymentType) {
        String query = "UPDATE tickets SET exit_time = CURRENT_TIMESTAMP(), amount_paid = ?, payment_type = ? WHERE ticket_id = ?";
        try (Connection conn = DBConfig.getConnection();
            PreparedStatement ps = conn.prepareStatement(query)) {
        
            ps.setDouble(1, amount);
            ps.setString(2, paymentType);
            ps.setInt(3, ticketId);        
            int rowsUpdated = ps.executeUpdate();
        
            if (rowsUpdated > 0) {
                System.out.println("[DATABASE] Ticket record successfully finalized in MySQL database.");
            } else {
                System.err.println("[WARN] No ticket records found matching ticket_id: " + ticketId);
            }
        
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to execute closeTicket update transaction.");
            e.printStackTrace();
        }
    }
}