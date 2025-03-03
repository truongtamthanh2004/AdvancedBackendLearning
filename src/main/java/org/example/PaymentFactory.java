package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PaymentFactory {
    private static final Map<String, Supplier<Payment>> paymentMethods = new HashMap<>();

    static {
        paymentMethods.put("CASH", CashPayment::new);
        paymentMethods.put("CARD", CreditPayment::new);
    }

    public static Payment createPayment(String method) {
        return paymentMethods.getOrDefault(method.toUpperCase(), () -> {
            throw new IllegalArgumentException("Unsupported payment method: " + method);
        }).get();
    }
}
