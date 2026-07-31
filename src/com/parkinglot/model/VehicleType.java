package com.parkinglot.model;

public enum VehicleType {
    CAR(60.0, 30.0),
    BIKE(30.0, 20.0),
    AUTO(20.0, 10.0),   
    TRUCK(70.0, 40.0);  

    private final double hourlyRate;
    private final double eventFlatRate;

    VehicleType(double hourlyRate, double eventFlatRate) {
        this.hourlyRate = hourlyRate;
        this.eventFlatRate = eventFlatRate;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public double getEventFlatRate() {
        return eventFlatRate;
    }
}
