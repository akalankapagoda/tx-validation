package com.surepay.tx.validation.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception related to transaction validation.
 */
public class TransactionValidationException extends Exception {

    HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

    public TransactionValidationException(String message) {
        super(message);
    }

    public TransactionValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransactionValidationException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    public TransactionValidationException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
