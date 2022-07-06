package com.surepay.tx.validation.controller;

import com.surepay.tx.validation.exception.TransactionValidationException;
import com.surepay.tx.validation.model.ResponseMessage;
import com.surepay.tx.validation.model.ResponseStatus;
import com.surepay.tx.validation.service.TransactionValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service controller for exposing endpoints for transaction validation services.
 */
@RestController
@RequestMapping("transaction/validation")
public class TransactionValidationController {

    private static final Logger logger = Logger.getLogger(TransactionValidationController.class.getName());

    @Autowired
    private TransactionValidationService transactionValidationService;

    /**
     * A health check endpoint to validate the deployment.
     *
     * @return A hello message
     */
    @GetMapping("/hello")
    public ResponseMessage hello() {

        return new ResponseMessage(ResponseStatus.SUCCESS, "Hello! The validation service is up and running!");
    }

    /**
     * Transaction validation service.
     *
     * @param identifier The file identifier
     * @param file Input transactions file
     * @param type The type of the file provided
     *
     * @return Transaction Validation Results after submitting for processing
     *
     * @throws TransactionValidationException
     */
    @PostMapping
    public ResponseMessage validateTransactions(@RequestParam("identifier") String identifier,
                                                @RequestParam("file") MultipartFile file,
                                                @RequestParam("type") String type)
            throws TransactionValidationException {

        if (logger.getLevel() != null && Level.FINE.intValue() >= logger.getLevel().intValue()) {
            logger.log(Level.FINE, "Request received for validation. Identifier : " + identifier +
                    " File : " + file.getName() + " Type : " + type);
        }


        if (!StringUtils.hasText(identifier) || file == null || file.isEmpty() || !StringUtils.hasText(type)) {

            throw new TransactionValidationException("Invalid input. identifier, file and type are required!",
                    HttpStatus.BAD_REQUEST);

        }

        return transactionValidationService.submitFileForProcessing(identifier, file, type);

    }

    /**
     * Retrieve results for a previously submitted validation request.
     *
     * @param identifier The submitted identifier
     *
     * @return The validation results or status
     * @throws TransactionValidationException
     */
    @GetMapping
    public ResponseMessage getValidationResults(@RequestParam("identifier") String identifier)
            throws TransactionValidationException {


        if (!StringUtils.hasText(identifier)) {

            throw new TransactionValidationException("Invalid request. identifier is required!",
                    HttpStatus.BAD_REQUEST);

        }

        return transactionValidationService.getValidationResults(identifier);
    }
}
