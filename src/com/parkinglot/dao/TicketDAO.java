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
        String query = "INSERT INTO tickets (daily_ticket_seq, parking_spot_id, vehicle_number) " +
                   "VALUES (?, ?, ?)";     
    
        try (Connection conn = DBConfig.getConnection();
            PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
        
            ps.setInt(1, ticket.getDailyTicketSeq());   // Maps to placeholder 1 (daily_ticket_seq)
            ps.setInt(2, ticket.getParkingSpotId());           // Maps to placeholder 3 (spot_number)
            ps.setString(3, ticket.getVehicleNumber()); // Maps to placeholder 4 (vehicle_number)
           
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

    public Ticket getActiveTicketByDailySequence(int dailySeq) {
        String query = "SELECT t.ticket_id, t.daily_ticket_seq, t.parking_spot_id, t.vehicle_number, " +
                       "t.entry_time, t.exit_time, t.amount_paid, p.floor_number, p.spot_number " +
                       "FROM tickets t JOIN parking_spots p ON t.parking_spot_id = p.parking_spot_id " +
                       "WHERE t.daily_ticket_seq = ? AND t.exit_time IS NULL";

        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, dailySeq);
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
                    rs.getInt("floor_number"),
                    rs.getInt("spot_number")
                );
            }
        } catch (SQLException e) {
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

    public void closeTicket(int ticketId, double amount) {
        String query = "UPDATE tickets SET exit_time = CURRENT_TIMESTAMP, amount_paid = ? WHERE ticket_id = ?";
        try (Connection conn = DBConfig.getConnection();
            PreparedStatement ps = conn.prepareStatement(query)) {
        
            ps.setDouble(1, amount);
            ps.setInt(2, ticketId);        
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}