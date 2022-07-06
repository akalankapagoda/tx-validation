package com.surepay.tx.validation.model;

/**
 * A base transaction with minimal information.
 */
public class BaseTransaction {

    private String reference;

    private String description;

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
