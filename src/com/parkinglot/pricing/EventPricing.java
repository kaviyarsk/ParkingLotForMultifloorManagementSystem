package com.parkinglot.pricing;

import com.parkinglot.model.Ticket;
import com.parkinglot.model.VehicleType;

public class EventPricing implements PricingStrategy {

    @Override
    public double calculateFee(Ticket ticket, String vehicleType) {
        
        try {
            VehicleType type = VehicleType.valueOf(vehicleType.toUpperCase()); 
            return type.getEventFlatRate();
        
        } catch (IllegalArgumentException e) {
            return 20.0; 
        }
    }
    @Override
    public String getStrategyName() {
        return "Event Price";
    }
}