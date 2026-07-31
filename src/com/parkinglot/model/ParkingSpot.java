package com.parkinglot.model;

public class ParkingSpot {
    private int parkingSpotId; 
    private int floorNumber;
    private int spotNumber;
    private String spotType; 
    private boolean isAvailable;

    public ParkingSpot(int parkingSpotId, int floorNumber,int spotNumber, String spotType, boolean isAvailable) {
        this.parkingSpotId = parkingSpotId;
        this.floorNumber = floorNumber;
        this.spotNumber = spotNumber;
        this.spotType = spotType;
        this.isAvailable = isAvailable;
    }

    public int getParkingSpotId() {
        return parkingSpotId;
    }
    public void setParkingSpotId(int parkingSpotId) { 
        this.parkingSpotId = parkingSpotId; 
    }
    public int getFloorNumber() {
        return floorNumber;
    }
    public int getSpotNumber() {
        return spotNumber;
    }
    public String getSpotType() {
        return spotType;
    }
    public boolean isAvailable() {
        return isAvailable;
    }
    public void setAvailable(boolean available) {
        isAvailable = available;
    }

}
