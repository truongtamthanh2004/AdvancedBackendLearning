package org.example;

public class CreditPayment extends Payment{
    @Override
    public void validate() {
        System.out.println("Validate credit successfully");
    }

    @Override
    public void deductAmount() {
        System.out.println("Deduct credit successfully");
    }

    @Override
    public void sendReceipt() {
        System.out.println("Receipt sent to customer by digital.");
    }
}
