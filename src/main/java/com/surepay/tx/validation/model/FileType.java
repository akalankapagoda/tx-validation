package com.surepay.tx.validation.model;

/**
 * Transaction records file type.
 */
public enum FileType {

    JSON("json"),

    CSV("csv");

    private String type;

    FileType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }


}
