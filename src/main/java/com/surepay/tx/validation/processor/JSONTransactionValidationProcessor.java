package com.surepay.tx.validation.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.surepay.tx.validation.config.CSVConfigProperties;
import com.surepay.tx.validation.exception.TransactionValidationException;
import com.surepay.tx.validation.model.Transaction;
import com.surepay.tx.validation.service.StorageService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * A transaction validation processor for processing and validation transactions in a JSON file.
 */
public class JSONTransactionValidationProcessor extends TransactionValidationProcessor {

    private static final Logger logger = Logger.getLogger(JSONTransactionValidationProcessor.class.getName());

    private boolean hasNext = true;

    private ObjectMapper objectMapper = new ObjectMapper();

    JSONTransactionValidationProcessor(String identifier, StorageService storageService,
                                       CSVConfigProperties CSVConfigProperties) throws TransactionValidationException {

        super(identifier, storageService, CSVConfigProperties);
    }

    @Override
    public boolean hasNext() throws TransactionValidationException {
        return hasNext;
    }

    /**
     * We're reading the entire JSON at one go here since reading an object by object in JSON is cumbersome.
     *
     * @return Transactions read from a JSON
     * @throws TransactionValidationException
     */
    @Override
    public List<Transaction> readNextBatch() throws TransactionValidationException {

        try {
            List<Transaction> transactions = Arrays.asList(objectMapper.readValue(reader, Transaction[].class));
            hasNext = false;

            return  transactions;
        } catch (IOException e) {
            throw new TransactionValidationException("Failed to read data for identifier : " + identifier);
        }
    }

    @Override
    public void close() throws TransactionValidationException {

    }
}
