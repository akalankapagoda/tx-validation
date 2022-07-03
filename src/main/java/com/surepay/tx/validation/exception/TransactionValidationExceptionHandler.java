package com.surepay.tx.validation.exception;

import com.surepay.tx.validation.model.ResponseMessage;
import com.surepay.tx.validation.model.ResponseStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Exception handler for handling controller level errors and returning error responses.
 */
@ControllerAdvice
public class TransactionValidationExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = Logger.getLogger(TransactionValidationExceptionHandler.class.getName());

    /**
     * Handle an error with a response status and a reason.
     *
     * @param e The thrown exception
     * @param request The web request
     *
     * @return A response message constructed with provided reason details and status
     */
    @ExceptionHandler(value = { TransactionValidationException.class })
    protected ResponseEntity<Object> handleResponseStatus(TransactionValidationException e, WebRequest request) {

        logger.log(Level.SEVERE, "Error handling request.", e);

        String message;

        if (HttpStatus.BAD_REQUEST == e.getHttpStatus()) {
            message = e.getMessage();
        } else {
            message = "An error occurred. Please contact the system administrator!";
        }

        ResponseMessage responseMessage = new ResponseMessage(ResponseStatus.FAIL, message);

        return handleExceptionInternal(e, responseMessage, new HttpHeaders(), e.getHttpStatus(), request);

    }


}
