package com.parkinglot.service;
import com.parkinglot.dao.ParkingSpotDAO;
import com.parkinglot.dao.TicketDAO;
import com.parkinglot.model.ParkingSpot;
import com.parkinglot.model.Ticket;
import com.parkinglot.payment.CashPayment;
import com.parkinglot.payment.PaymentStrategy;
import com.parkinglot.payment.UPIPayment;
import com.parkinglot.pricing.PricingStrategy;
import com.parkinglot.pricing.TimeBasedPricing;
import com.parkinglot.util.ReceiptBackupUtility;
import java.time.LocalDate;
import java.util.Scanner;

public class ParkingService {
    private final ParkingSpotDAO spotDAO = new ParkingSpotDAO();
    private final TicketDAO ticketDAO = new TicketDAO();
    private PricingStrategy pricingStrategy = new TimeBasedPricing();
    
    public void setPricingStrategy(PricingStrategy pricingStrategy) {
        this.pricingStrategy = pricingStrategy;
    }

    public void parkVehicle(String vehicleNumber, String vehicleType) {
        if (ticketDAO.isVehicleAlreadyParked(vehicleNumber)) {
            System.out.println("\n[DENIED] Vehicle " + vehicleNumber + " is already parked inside!");
            return;
        }

        ParkingSpot availableSpot = spotDAO.getAvailableSpot(vehicleType);

        if (availableSpot == null) {
            System.out.println("\n[SORRY] No vacant spaces available for type: " + vehicleType);
            return;
        }

        spotDAO.updateSpotStatus(availableSpot.getParkingSpotId(), false);
        
        Ticket newTicket = new Ticket(vehicleNumber, availableSpot.getParkingSpotId());
        
        boolean isSaved = ticketDAO.saveTicket(newTicket);

        if (isSaved) {
            int dayOfMonth = LocalDate.now().getDayOfMonth();
            String maskedTicketId = String.valueOf(newTicket.getDailyTicketSeq()) + dayOfMonth;
            System.out.println("\n========================================");
            System.out.println("          PARKING TICKET ISSUED         ");
            System.out.println("========================================");
            System.out.println(" Ticket ID   : " + maskedTicketId);
            System.out.println(" Vehicle No  : " + vehicleNumber);
            System.out.println(" Location    : Floor " + availableSpot.getFloorNumber() + " | Spot #" + availableSpot.getSpotNumber());
            System.out.println("========================================");
            ReceiptBackupUtility.generateCheckInReceipt(newTicket, maskedTicketId, availableSpot.getFloorNumber(), availableSpot.getSpotNumber());
                   
        }
    }
    
    public void unparkVehicle(String maskedTicketId) {
        if (maskedTicketId == null || maskedTicketId.length() < 3) {
            System.out.println("\n[ERROR] Invalid Ticket ID format!");
            return;
        }

        try {
            String dailySeqStr = maskedTicketId.substring(0, maskedTicketId.length() - 2);
            int dailySequence = Integer.parseInt(dailySeqStr);

            Ticket ticket = ticketDAO.getActiveTicketByDailySequence(dailySequence);

            if (ticket == null) {
                System.out.println("\n[ERROR] Ticket not found or vehicle has already checked out.");
                return;
            }

            String vehicleType = (ticket.getParkingSpotId() <= 5) ? "CAR" : "BIKE";
            double totalFee = pricingStrategy.calculateFee(ticket, vehicleType);

            // --- NEW PAYMENT MODE SELECTION LOGIC ---
            System.out.println("\n========================================");
            System.out.printf(" TOTAL DUE AMOUNT: Rs.%.2f\n", totalFee);
            System.out.println("========================================");
            System.out.println("Select Payment Mode:");
            System.out.println("1. Cash on Hand");
            System.out.println("2. UPI Payment (QR Scan)");
            System.out.print("Please select (1-2): ");
        
            Scanner paymentScanner = new Scanner(System.in);
            String paymentChoice = paymentScanner.nextLine().trim();
            PaymentStrategy paymentStrategy = paymentChoice.equals("2") ? new UPIPayment() : new CashPayment();
            boolean paymentSuccess = paymentStrategy.processPayment(totalFee);
            if (paymentSuccess) {
                
                ticketDAO.closeTicket(ticket.getTicketId(), totalFee);
    
                spotDAO.updateSpotStatus(ticket.getParkingSpotId(), true);
                String rateTypeName = pricingStrategy.getStrategyName();
                String paymentModeName = paymentStrategy.getClass().getSimpleName();
                System.out.println("\n========================================");
                System.out.println("          VEHICLE CHECK-OUT             ");
                System.out.println("========================================");
                System.out.println(" Vehicle No   : " + ticket.getVehicleNumber());
                System.out.println(" Location Info: Floor " + ticket.getFloorNumber() + " | Spot #" + ticket.getSpotNumber());
                System.out.println(" Rate Type    : " + rateTypeName);
                System.out.printf(" Settled Cost : Rs. %.2f via %s\n", totalFee, paymentModeName);
                System.out.println("========================================");

                // 4. Local File Backups remain active
                ReceiptBackupUtility.saveReceiptToFile(ticket, maskedTicketId, rateTypeName, totalFee, paymentModeName);
                ReceiptBackupUtility.generateCustomerReceipt(ticket, maskedTicketId, rateTypeName, totalFee, paymentModeName);
            }
        }catch (NumberFormatException e) {
            System.out.println("\n[ERROR] Invalid characters found in Ticket ID.");
        }
    }
    public void showOccupancyDashboard() {
        spotDAO.displayLiveOccupancyDashboard();
    }
}