package com.parkinglot.payment;

public class CashPayment implements PaymentStrategy {
    @Override
    public boolean processPayment(double amount) {
        System.out.println("\n--- CASH TRANSACTION ---");
        System.out.printf("Please collect Rs.%.2f in cash from the customer.\n", amount);
        System.out.println("[SYSTEM] Cash counted. Transaction marked as complete.");
        return true;
    }
}