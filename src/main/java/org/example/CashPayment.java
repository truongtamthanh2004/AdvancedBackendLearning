package org.example;

public class CashPayment extends Payment{
    @Override
    public void validate() {
        System.out.println("Validate cash successfully");
    }

    @Override
    public void deductAmount() {
        System.out.println("Deduct cash successfully");
    }

    @Override
    public void sendReceipt() {
        System.out.println("Receipt sent to customer by paper.");
    }
}
