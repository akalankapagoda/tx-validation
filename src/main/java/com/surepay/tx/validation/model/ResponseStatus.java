package com.surepay.tx.validation.model;

/**
 * Represents an operation status.
 */
public enum ResponseStatus {

    SUCCESS,

    /**
     * The operation is submitted for processing.
     */
    SUBMITTED,

    /**
     * The operation is in-progress.
     */
    IN_PROGRESS,

    /**
     * Data not found.
     */
    NOT_FOUND,

    FAIL
}
