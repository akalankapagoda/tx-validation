package com.surepay.tx.validation.controller;

import com.surepay.tx.validation.model.ResponseMessage;
import com.surepay.tx.validation.model.ResponseStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service controller for exposing endpoints for transaction validation services.
 */
@RestController
@RequestMapping("transaction/validation")
public class TransactionValidationController {

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
     * @param file Input transactions file
     *
     * @return
     */
    @PostMapping
    public ResponseMessage validateTransaction(@RequestParam("file") MultipartFile file) {

        return new ResponseMessage(ResponseStatus.FAIL, "Not implemented!");

    }
}
