package com.parkinglot.model;

public enum VehicleType {
    CAR(60.0, 30.0),
    BIKE(30.0, 20.0),
    AUTO(20.0, 10.0),   // Easily added!
    TRUCK(70.0, 40.0);  // Easily added!

    // 2. Add private fields to hold these configuration values
    private final double hourlyRate;
    private final double eventFlatRate;

    // 3. Add the Enum constructor to map the numbers above into the fields
    VehicleType(double hourlyRate, double eventFlatRate) {
        this.hourlyRate = hourlyRate;
        this.eventFlatRate = eventFlatRate;
    }

    // 4. Add the getter method that TimeBasedPricing is looking for!
    public double getHourlyRate() {
        return hourlyRate;
    }

    // Optional: Getter for event pricing if you want to clean up EventPricing later too
    public double getEventFlatRate() {
        return eventFlatRate;
    }
}