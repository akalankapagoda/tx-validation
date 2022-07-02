package com.surepay.tx.validation.model;

/**
 * A generic service response message.
 */
public class ResponseMessage {

    private ResponseStatus status;

    private String message;

    public ResponseMessage() {}

    public ResponseMessage(ResponseStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
