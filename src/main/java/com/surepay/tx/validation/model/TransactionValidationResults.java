package com.surepay.tx.validation.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Holds information about a submitted validation job progress and results.
 */
public class TransactionValidationResults extends ResponseMessage {

    private String identifier;

    private Set<BaseTransaction> invalidRecords = new HashSet<>();

    public TransactionValidationResults(String identifier, ResponseStatus status, String message) {
        super(status, message);
        this.identifier = identifier;
    }

    public TransactionValidationResults(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Set<BaseTransaction> getInvalidRecords() {
        return invalidRecords;
    }

    public void setInvalidRecords(Set<BaseTransaction> invalidRecords) {
        this.invalidRecords = invalidRecords;
    }
}
