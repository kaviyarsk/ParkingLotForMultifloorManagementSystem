package com.parkinglot.model;

import java.sql.Timestamp;

public class Ticket {
    private int ticketId;
    private int dailyTicketSeq;
    private int parkingSpotId;
    private String vehicleNumber;
    private Timestamp entryTime;
    private Timestamp exitTime;
    private double amountPaid;
    private String pricingStrategy;
    private int floorNumber;
    private int spotNumber;
    private String vehicleType;
    private String paymentType;

    public Ticket(String vehicleNumber, int parkingSpotId) {
        this.vehicleNumber = vehicleNumber;
        this.parkingSpotId = parkingSpotId;
    }

    public Ticket(int ticketId, int dailyTicketSeq, int parkingSpotId, String vehicleNumber, 
        Timestamp entryTime, Timestamp exitTime, double amountPaid, String pricingStrategy, 
        int floorNumber,int spotNumber, String vehicleType, String paymentType) {
        this.ticketId = ticketId;
        this.dailyTicketSeq=dailyTicketSeq;     
        this.parkingSpotId = parkingSpotId;
        this.vehicleNumber = vehicleNumber;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.amountPaid = amountPaid;
        this.pricingStrategy = pricingStrategy;
        this.floorNumber = floorNumber;
        this.spotNumber = spotNumber;
        this.vehicleType = vehicleType;
        this.paymentType = paymentType;
    }

    
    public int getTicketId() { return ticketId; }
    public void setTicketId(int ticketId) { this.ticketId = ticketId; }

    public int getDailyTicketSeq() { return dailyTicketSeq; }
    public void setDailyTicketSeq(int dailyTicketSeq) { this.dailyTicketSeq = dailyTicketSeq; }
    
    public int getParkingSpotId() { return parkingSpotId; }
    public void setParkingSpotId(int parkingSpotId) { this.parkingSpotId = parkingSpotId; }
    
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber){ this.vehicleNumber=vehicleNumber;}

    public Timestamp getEntryTime() { return entryTime; }
    public void setEntryTime(Timestamp entryTime){this.entryTime=entryTime;}

    public Timestamp getExitTime() { return exitTime; }
    public void setExitTime(Timestamp exitTime) { this.exitTime = exitTime; }

    public double getAmountPaid() { return amountPaid; }
    public void setAmountPaid(double amountPaid) { this.amountPaid = amountPaid; }

    public String getPricingStrategy() { return pricingStrategy; }
    public void setPricingStrategy(String pricingStrategy) { this.pricingStrategy = pricingStrategy; }
    
    public int getFloorNumber() { return floorNumber; }
    public void setFloorNumber(int floorNumber) { this.floorNumber = floorNumber; }

    public int getSpotNumber() { return spotNumber; }
    public void setSpotNumber(int spotNumber) { this.spotNumber = spotNumber; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType=vehicleType; }

    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }
}
