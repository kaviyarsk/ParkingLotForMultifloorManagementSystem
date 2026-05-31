package com.parkinglot.payment;

public interface PaymentStrategy {
    
    boolean processPayment(double amount);
}
