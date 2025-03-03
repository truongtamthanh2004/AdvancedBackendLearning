package org.example;

public abstract class Payment {
    protected abstract void validate();
    protected abstract void deductAmount();
    protected abstract void sendReceipt();

    public void excecute() {
        validate();
        deductAmount();
        sendReceipt();
    }

}
