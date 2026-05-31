package com.parkinglot;

import com.parkinglot.model.VehicleType;
import com.parkinglot.pricing.EventPricing;
import com.parkinglot.pricing.TimeBasedPricing;
import com.parkinglot.service.ParkingService;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ParkingService parkingService = new ParkingService();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("========================================");
        System.out.println("   WELCOME TO ENTERPRISE PARKING LOT   ");
        System.out.println("========================================");

        while (running) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. Park a Vehicle (Check-In)");
            System.out.println("2. Unpark a Vehicle (Check-Out)");
            System.out.println("3. View Live Occupancy Dashboard");
            System.out.println("4. Switch to Event Pricing (Holiday Flat Rate)");
            System.out.println("5. Switch to Standard Pricing (Hourly Rates)");
            
            System.out.println("6. Exit System Application");
            System.out.print("Please select an option (1-6): ");

            String inputChoice = scanner.nextLine().trim();
            
            switch (inputChoice) {
                case "1":
                    System.out.print("\nEnter Vehicle License Plate Number: ");
                    String vehicleNumber = scanner.nextLine().trim().toUpperCase();

                    System.out.print("Enter Vehicle Type (CAR / BIKE / AUTO / TRUCK): ");
                    String vehicleType = scanner.nextLine().trim().toUpperCase();

                    try {
                        VehicleType type = VehicleType.valueOf(vehicleType);
                        parkingService.parkVehicle(vehicleNumber, type.name());
                    } catch (IllegalArgumentException e) {
                        System.out.println("\n[ERROR] Unsupported vehicle type! We only accept CAR, BIKE, AUTO, or TRUCK.");
                    }
                    break;

                case "2":
                    System.out.print("\nEnter Ticket ID to process checkout: ");
                    String maskedTicketId = scanner.nextLine().trim();
                    parkingService.unparkVehicle(maskedTicketId);
                    break;
                case "3":
                    // Clear terminal layout view and pull live dashboard metrics
                    parkingService.showOccupancyDashboard();
                    break;

                case "4":
                    parkingService.setPricingStrategy(new EventPricing());
                    System.out.println("\n[SYSTEM] Switched successfully to Holiday Flat Rate Event Pricing.");
                    break;

                case "5":
                    parkingService.setPricingStrategy(new TimeBasedPricing());
                    System.out.println("\n[SYSTEM] Switched successfully back to Hourly Standard Pricing.");
                    break;

                case "6":
                    System.out.println("\nShutting down Parking Management System. Goodbye!");
                    running = false;
                    break;

                default:
                    System.out.println("\n[INVALID] Selection invalid. Please type a valid choice from 1 to 5.");
                    break;
            }
        }
        scanner.close();
    }
}