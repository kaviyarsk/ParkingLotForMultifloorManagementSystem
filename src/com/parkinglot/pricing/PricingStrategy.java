package com.parkinglot.pricing;

import com.parkinglot.model.Ticket;

public interface PricingStrategy {
    
    double calculateFee(Ticket ticket, String vehicleType);
    String getStrategyName();
}
