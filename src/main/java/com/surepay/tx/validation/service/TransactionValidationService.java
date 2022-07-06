package com.surepay.tx.validation.service;

import com.surepay.tx.validation.exception.TransactionValidationException;
import com.surepay.tx.validation.model.FileType;
import com.surepay.tx.validation.model.ResponseStatus;
import com.surepay.tx.validation.model.TransactionValidationResults;
import com.surepay.tx.validation.processor.TransactionValidationProcessor;
import com.surepay.tx.validation.processor.TransactionValidationProcessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Transaction validation service for parsing and validation transaction records.
 */
@Service
public class TransactionValidationService {

    private static final int EXECUTOR_THREAD_POOL_SIZE = 10; // TODO: This should come from a config and default to 10

    private ExecutorService executorService = Executors.newFixedThreadPool(EXECUTOR_THREAD_POOL_SIZE);

    @Autowired
    private TransactionValidationProcessorFactory validationProcessorFactory;

    @Autowired
    private StorageService storageService;

    /**
     * Submit a file for processing.
     *
     * @param identifier The file identifier
     * @param file The file data to process
     * @param type The file type
     *
     * @throws TransactionValidationException
     */
    public TransactionValidationResults submitFileForProcessing(String identifier, MultipartFile file, String type)
            throws TransactionValidationException {

        storageService.saveFile(identifier, file);

        FileType fileType = FileType.valueOf(type.toUpperCase());


        TransactionValidationResults results = new TransactionValidationResults(identifier);
        results.setStatus(ResponseStatus.SUBMITTED);

        storageService.saveValidationResults(results);

        TransactionValidationProcessor processor = validationProcessorFactory.getTransactionValidationProcessor(identifier, fileType);

        executorService.submit(processor);

        return results;

    }

    /**
     * Retrieve validation results for a specific identifier.
     *
     * @param identifier The file identifier to retrieve results of
     *
     * @return Transaction Validation results or status
     * @throws TransactionValidationException
     */
    public TransactionValidationResults getValidationResults(String identifier) throws TransactionValidationException {

        TransactionValidationResults results = storageService.readValidationResults(identifier);

        if (results == null) {
            throw new TransactionValidationException("Results not found for identifier : "  +
                    identifier, HttpStatus.NOT_FOUND);
        }

        return results;

    }
}
