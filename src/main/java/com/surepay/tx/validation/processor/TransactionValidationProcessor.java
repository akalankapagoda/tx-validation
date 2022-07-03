package com.surepay.tx.validation.processor;

/**
 * A runnable processor for validating transaction.
 */
public abstract class TransactionValidationProcessor implements Runnable {

    private String identifier;

    TransactionValidationProcessor(String identifier) {
        this.identifier = identifier;
    }
}
