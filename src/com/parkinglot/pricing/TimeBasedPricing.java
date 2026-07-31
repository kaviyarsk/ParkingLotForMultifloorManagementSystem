package com.parkinglot.pricing;
import com.parkinglot.model.Ticket;
import com.parkinglot.model.VehicleType;
public class TimeBasedPricing implements PricingStrategy {

    @Override
    public double calculateFee(Ticket ticket, String vehicleType) {
        if (ticket == null || ticket.getEntryTime() == null) {
            return 0.0;
        }

        long entryMillis = ticket.getEntryTime().getTime();
        long exitMillis = System.currentTimeMillis();        
        long durationMillis = exitMillis - entryMillis;
        
        double hours = Math.ceil(durationMillis / 3600000.0);
        
        if (hours <= 0) {
            hours = 1.0;
        }

        double hourlyRate = 2.0; 
        try {
            VehicleType type = VehicleType.valueOf(vehicleType.toUpperCase());
            hourlyRate = type.getHourlyRate(); 
        } catch (IllegalArgumentException e) {
            System.out.println("[SYSTEM WARNING] Unknown vehicle type. Using fallback base rate.");
        }
        
        return hours * hourlyRate;
    }
    @Override
    public String getStrategyName() {
        return "Standard Hourly Price";
    }
}
