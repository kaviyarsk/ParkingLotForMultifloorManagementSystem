package com.parkinglot.payment;

public class UPIPayment implements PaymentStrategy {
    @Override
    public boolean processPayment(double amount) {
        System.out.println("\n--- UPI TRANSACTION ---");
        System.out.println("[SYSTEM] Generating Dynamic QR Code...");
        System.out.printf(">> [QR CODE DISPLAYED] Scan to pay: $%.2f <<\n", amount);
        System.out.println("Waiting for secure bank ping approval response...");
        System.out.println("[SYSTEM] Payment received successfully via UPI!");
        return true;
    }
}