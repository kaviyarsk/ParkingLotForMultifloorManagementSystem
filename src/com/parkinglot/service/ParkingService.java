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
import java.util.Scanner;

public class ParkingService {
    private final ParkingSpotDAO spotDAO = new ParkingSpotDAO();
    private final TicketDAO ticketDAO = new TicketDAO();
    private PricingStrategy pricingStrategy = new TimeBasedPricing();
  
    public void setPricingStrategy(PricingStrategy pricingStrategy) {
        this.pricingStrategy = pricingStrategy;
    }

    private String formatPaymentMode(String rawClassName) {
        if (rawClassName == null) return "Unknown Payment";
    
        switch (rawClassName) {
            case "CashPayment":
                return "Cash Payment";
            case "UPIPayment":
                return "UPI Payment";
            default:
                return rawClassName.replaceAll("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])", " ");
        }
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
        
        String currentActiveStrategy = this.pricingStrategy.getStrategyName();
        newTicket.setPricingStrategy(currentActiveStrategy);

        boolean isSaved = ticketDAO.saveTicket(newTicket);

        if (isSaved) {
            
            System.out.println("\n========================================");
            System.out.println("          PARKING TICKET ISSUED         ");
            System.out.println("========================================");
            System.out.println(" Ticket ID   : " + newTicket.getDailyTicketSeq());
            System.out.println(" Vehicle No  : " + vehicleNumber);
            System.out.println(" Rate Based On : " + currentActiveStrategy);
            System.out.println(" Location    : Floor " + availableSpot.getFloorNumber() + " | Spot #" + availableSpot.getSpotNumber());
            System.out.println("========================================");
            ReceiptBackupUtility.generateCheckInReceipt(newTicket, String.valueOf(newTicket.getDailyTicketSeq()), availableSpot.getFloorNumber(), availableSpot.getSpotNumber());
                   
        }
    }
    
    public void unparkVehicle(String vehicleNumber) {
        
        try {
            
            Ticket ticket = ticketDAO.getActiveTicketByPlate(vehicleNumber);
            
            if (ticket == null) {
                System.out.println("\n[ERROR] Ticket informatin not match, Kindly check it and try again.");
                return;
            }

            String lockedStrategy = ticket.getPricingStrategy();
            if (lockedStrategy == null) {
                System.out.println("[WARN] No pricing strategy locked at check-in. Defaulting to Standard Hourly Rates.");
                lockedStrategy = "Standard Hourly Price";
            }
            PricingStrategy processingStrategy;
            if (lockedStrategy.toLowerCase().contains("event")) {
                processingStrategy = new com.parkinglot.pricing.EventPricing();
            } else {  
                processingStrategy = new com.parkinglot.pricing.TimeBasedPricing();
            }

            String vehicleType = ticket.getVehicleType();
            double totalFee = processingStrategy.calculateFee(ticket, vehicleType);

            System.out.println("\n========================================");
            System.out.printf(" TOTAL DUE AMOUNT: Rs.%.2f\n", totalFee);
            System.out.println("========================================");
            System.out.println("Select Payment Mode:");
            System.out.println("1. Cash on Hand");
            System.out.println("2. UPI Payment (QR Scan)");
            System.out.print("Please select (1-2): ");
        
            Scanner paymentScanner = new Scanner(System.in);
            String paymentChoice = paymentScanner.nextLine().trim();

            PaymentStrategy paymentStrategy = null;
            String cleanPaymentType = ""; 

        
            if (paymentChoice.equals("1")) {
                paymentStrategy = new CashPayment();
                cleanPaymentType = "CASH";
            } else if (paymentChoice.equals("2")) {
                paymentStrategy = new UPIPayment();
                cleanPaymentType = "UPI";
            } else {
                System.out.println("\n[ERROR] Invalid payment mode selected! Transaction canceled. Returning to main menu...");
                return;
            }

            boolean paymentSuccess = paymentStrategy.processPayment(totalFee);
            if (paymentSuccess) {
                
                ticketDAO.closeTicket(ticket.getTicketId(), totalFee, cleanPaymentType);
    
                spotDAO.updateSpotStatus(ticket.getParkingSpotId(), true);
                String rateTypeName = ticket.getPricingStrategy();
                String rawClassName = paymentStrategy.getClass().getSimpleName();
                String paymentModeName = formatPaymentMode(rawClassName);
                String sequenceStr = String.valueOf(ticket.getDailyTicketSeq());

                String formattedEntryTime = com.parkinglot.util.ReceiptBackupUtility.getCustomFormatter().format(ticket.getEntryTime());

                System.out.println("\n========================================");
                System.out.println("          VEHICLE CHECK-OUT             ");
                System.out.println("========================================");
                System.out.println(" Vehicle No   : " + ticket.getVehicleNumber());
                System.out.println(" Check-In     : " + formattedEntryTime);
                System.out.println(" Location Info: Floor " + ticket.getFloorNumber() + " | Spot #" + ticket.getSpotNumber());
                System.out.println(" Rate Type    : " + rateTypeName);
                System.out.printf(" Settled Cost : Rs.%.2f via %s\n", totalFee, paymentModeName);
                System.out.println("========================================");

                ReceiptBackupUtility.saveReceiptToFile(ticket, sequenceStr, rateTypeName, totalFee, paymentModeName);
                ReceiptBackupUtility.generateCustomerReceipt(ticket, sequenceStr, rateTypeName, totalFee, paymentModeName);
            }
        }catch (NumberFormatException e) {
            System.out.println("\n[ERROR] Invalid characters found in Ticket ID.");
        }
    }
    public void showOccupancyDashboard() {
        spotDAO.displayLiveOccupancyDashboard();
    }
}
