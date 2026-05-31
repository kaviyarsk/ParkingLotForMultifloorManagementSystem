package com.parkinglot.util;

import com.parkinglot.model.Ticket;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReceiptBackupUtility {
    private static final String DIRECTORY_PATH = "records";
    private static final String FILE_PATH = "records/receipts.txt";

    public static void saveReceiptToFile(Ticket ticket, String maskedTicketId, String rateType, double amount, String paymentMode) {
        try {
            // Ensure the 'records' directory exists on your computer
            File directory = new File(DIRECTORY_PATH);
            if (!directory.exists()) {
                directory.mkdirs(); 
            }

            try (FileWriter fw = new FileWriter(FILE_PATH, true);
                 PrintWriter pw = new PrintWriter(fw)) {

                // Format current system timestamp for audit logs
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                // Write structured receipt text blocks
                pw.println("========================================");
                pw.println("         OFFLINE TRANSACTION LOG        ");
                pw.println("========================================");
                pw.println(" Process Time : " + timestamp);
                pw.println(" Ticket ID    : " + maskedTicketId);
                pw.println(" Vehicle No   : " + ticket.getVehicleNumber());
                pw.println(" Location Info: Floor " + ticket.getFloorNumber() + " | Spot #" + ticket.getSpotNumber());
                pw.println(" Rate Type    : " + rateType);
                pw.printf(" Settled Cost : Rs.%.2f via %s\n", amount, paymentMode);
                pw.println("========================================\n"); // Extra spacing line
                
            }
        } catch (IOException e) {
            System.err.println("[SYSTEM WARNING] Failed to write offline receipt backup: " + e.getMessage());
        }
    }

    public static void generateCheckInReceipt(Ticket ticket, String maskedTicketId,int floor, int spotNum) {
        try {
            File directory = new File("records");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Create a unique entry ticket file name using the vehicle number and a timestamp token
            String fileTimeToken = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
            String ticketFileName =  "records/ticket_" + ticket.getVehicleNumber() + "_" + fileTimeToken + ".txt";

            // Open a new file to save this specific entry ticket
            try (FileWriter fw = new FileWriter(ticketFileName);
                 PrintWriter pw = new PrintWriter(fw)) {

                String displayTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                // Print a clean, customer-facing parking ticket layout
                pw.println("========================================");
                pw.println("       ENTERPRISE PARKING LOT          ");
                pw.println("          PARKING TICKET               ");
                pw.println("========================================");
                pw.println(" Date & Entry Time: " + displayTime);
                pw.println(" Ticket ID        : #" + maskedTicketId);
                pw.println(" Vehicle No       : " + ticket.getVehicleNumber());
                pw.println("----------------------------------------");
                pw.println(" ASSIGNED SLOT    : ");
                pw.println("   FLOOR          : " + floor);
                pw.println("   SPOT NUMBER    : " + spotNum);
                pw.println("----------------------------------------");
                pw.println(" NOTE: Please keep this ticket safe.    ");
                pw.println(" Charges will be calculated at checkout.");
                pw.println("========================================");
                pw.println("            HAVE A NICE DAY             ");
                pw.println("========================================");

                System.out.println("[SYSTEM] Individual customer entry ticket saved to: " + ticketFileName);
            }
        } catch (IOException e) {
            System.err.println("[SYSTEM WARNING] Failed to generate check-in ticket file: " + e.getMessage());
        }
    }

    public static void generateCustomerReceipt(Ticket ticket, String maskedTicketId, String rateType, double amount, String paymentMode) {
        try {
            File directory = new File(DIRECTORY_PATH);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Create a dynamic file name using vehicle number and short time token
            String fileTimeToken = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));
            String customerFileName = DIRECTORY_PATH + "/receipt_" + ticket.getVehicleNumber() + "_" + fileTimeToken + ".txt";

            // Open a brand new file (Notice: append flag is left out so it's a fresh file for this customer)
            try (FileWriter fw = new FileWriter(customerFileName);
                 PrintWriter pw = new PrintWriter(fw)) {

                String displayTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                // Print a beautiful customer-facing invoice receipt
                pw.println("========================================");
                pw.println("       ENTERPRISE PARKING RECEIPT       ");
                pw.println("========================================");
                pw.println(" Date & Time  : " + displayTime);
                pw.println(" Invoice ID   : #" + maskedTicketId);
                pw.println(" Vehicle No   : " + ticket.getVehicleNumber());
                pw.println(" Assigned Slot: Floor " + ticket.getFloorNumber() + " | Spot #" + ticket.getSpotNumber());
                pw.println("----------------------------------------");
                pw.println(" Pricing based on  : " + rateType);
                pw.printf(" Payment Mode : %s\n", paymentMode);
                pw.println("----------------------------------------");
                pw.printf(" TOTAL PAID   : Rs.%.2f\n", amount);
                pw.println("========================================");
                pw.println("       THANK YOU FOR PARKING WITH US!    ");
                pw.println("========================================");
                
                System.out.println("[SYSTEM] Individual customer invoice saved to: " + customerFileName);
            }
        } catch (IOException e) {
            System.err.println("[SYSTEM WARNING] Failed to generate customer receipt file: " + e.getMessage());
        }
    }
}