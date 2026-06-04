package com.parkinglot;

import com.parkinglot.model.VehicleType;
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
            System.out.println("4. Switch to Event Pricing");
            System.out.println("5. Switch to Standard Hour Pricing");
            
            System.out.println("6. Exit System Application");
            System.out.print("Please select an option (1-6): ");

            String inputChoice = scanner.nextLine().trim();
            
            switch (inputChoice) {
                case "1":
                    String vehicleNumber = "";
                    String platePattern = "^[A-Z]{2}[0-9]{2}[A-Z]{2}[0-9]{4}$";
                    while (true) {
                        System.out.print("Enter Vehicle License Plate : ");
                        vehicleNumber = scanner.nextLine().trim().toUpperCase(); 

                        if (vehicleNumber.matches(platePattern)) {
                            break; 
                        } else {
                            System.out.println("[ERROR] Invalid Plate Format! Expected format: 2 Letters, 2 Digits, 2 Letters, 4 Digits.");
                            System.out.println("Example: TN38AB1707");
                        }
                    }

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
                    try {
                        System.out.println("\n--- VEHICLE CHECK-OUT VERIFICATION ---");
                        
                        String plateInput = "";
                        String platePattern1 = "^[A-Z]{2}[0-9]{2}[A-Z]{2}[0-9]{4}$";
        
                        while (true) {
                            System.out.print("Enter Vehicle License Plate Number: ");
                            plateInput = scanner.nextLine().trim().toUpperCase();

                            if (plateInput.matches(platePattern1)) {
                                break; 
                            } else {
                                System.out.println("[ERROR] Invalid Plate Format! Expected: 2 Letters, 2 Digits, 2 Letters, 4 Digits.");
                            }
                    }
        
                        parkingService.unparkVehicle(plateInput);
        
                    } catch (java.util.InputMismatchException e) {
                        System.out.println("\n[ERROR] Input type mismatch detected! Returning to main menu...");
                        scanner.nextLine(); 
                    } catch (Exception e) {
                        System.out.println("\n[ERROR] Something went wrong during check-out processing.");
                        scanner.nextLine(); 
                    }
                    break;
                case "3":
                    parkingService.showOccupancyDashboard();
                    break;

                case "4":
                    parkingService.setPricingStrategy(new com.parkinglot.pricing.EventPricing());
                    System.out.println("\n[SYSTEM] Switched successfully to Event Pricing.");
                    break;

                case "5":
                    parkingService.setPricingStrategy(new com.parkinglot.pricing.TimeBasedPricing());
                    System.out.println("\n[SYSTEM] Switched successfully to Hourly Standard Pricing.");
                    break;

                case "6":
                    System.out.println("\nShutting down Parking Management System. Goodbye!");
                    running = false;
                    break;

                default:
                    System.out.println("\n[INVALID] Selection invalid. Please type a valid choice from 1 to 6.");
                    break;
            }
        }
        scanner.close();
    }
}