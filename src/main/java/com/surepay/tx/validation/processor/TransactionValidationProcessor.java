package com.surepay.tx.validation.processor;

import com.surepay.tx.validation.config.CSVConfigProperties;
import com.surepay.tx.validation.exception.TransactionValidationException;
import com.surepay.tx.validation.model.BaseTransaction;
import com.surepay.tx.validation.model.ResponseStatus;
import com.surepay.tx.validation.model.Transaction;
import com.surepay.tx.validation.model.TransactionValidationResults;
import com.surepay.tx.validation.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A runnable processor for validating transaction.
 */
public abstract class TransactionValidationProcessor implements Runnable {

    private static final Logger logger = Logger.getLogger(TransactionValidationProcessor.class.getName());

    private StorageService storageService;

    private CSVConfigProperties csvConfigProperties;

    private HashMap<String, BaseTransaction> readTransactions = new HashMap<>();

    protected TransactionValidationResults results;
    private Set<BaseTransaction> invalidRecords;

    protected String identifier;

    protected BufferedReader reader;

    TransactionValidationProcessor(String identifier, StorageService storageService, CSVConfigProperties csvConfigProperties)
            throws TransactionValidationException {

        this.identifier = identifier;
        this.storageService = storageService;
        this.csvConfigProperties = csvConfigProperties;


        results = storageService.readValidationResults(identifier);

        InputStream inputStream = storageService.readFile(identifier);

        if (inputStream == null) {
            logger.log(Level.SEVERE, "The data stream for the identifier : " + identifier + " is null");
            results.setStatus(ResponseStatus.FAIL);
            results.setMessage("The data stream for the identifier : " + identifier + " is null");
            return;
        }

        reader = new BufferedReader(new InputStreamReader(inputStream));

        invalidRecords = results.getInvalidRecords();
    }

    /**
     * Reads the records and validates each.
     * Populates the results with invalid records.
     */
    @Override
    public void run() {

        try {

            while (hasNext()) {

                List<Transaction> transactions = readNextBatch();

                if (transactions == null || transactions.isEmpty()) {
                    // Something is not right, maybe lot of invalid records, we stop the processing here

                    results.setStatus(ResponseStatus.FAIL);
                    results.setMessage("An unexpected error occurred when reading the file. " +
                            "Please check the file type and content.");

                    return;

                }

                transactions.stream().forEach( transaction -> validateTransaction(transaction));

                if (ResponseStatus.FAIL != results.getStatus()) {
                    // We set this to success only if this has not already been set to FAIL

                    results.setStatus(ResponseStatus.SUCCESS);
                }
            }

        } catch (TransactionValidationException e) {
            logger.log(Level.SEVERE, "Failed to read the data.", e);

            results.setStatus(ResponseStatus.FAIL);
            results.setMessage("Failed to read the JSON data.");
        }
    }

    /**
     * Verifies a given transaction is valid.
     *
     * A transaction is invalid if
     *    1. The reference is repeated
     *    2. The end balance is incorrect after mutation
     *
     * Populates the results with invalid transaction details.
     *
     * @param transaction The transaction to validate
     */
    private void validateTransaction(Transaction transaction) {

        BaseTransaction currentBaseTransaction = new BaseTransaction();

        currentBaseTransaction.setReference(transaction.getReference());
        currentBaseTransaction.setDescription(transaction.getDescription());

        String reference = transaction.getReference();

        BaseTransaction existingTransaction = readTransactions.get(reference);

        if (existingTransaction != null) { // The reference already exists, this is invalid

            invalidRecords.add(currentBaseTransaction);

            invalidRecords.add(existingTransaction);
        } else {
            readTransactions.put(currentBaseTransaction.getReference(), currentBaseTransaction);

            // Now verify the end balance
            if (transaction.getEndBalance().compareTo((transaction.getStartBalance().add(transaction.getMutation()))) != 0)  {
                invalidRecords.add(currentBaseTransaction);
            }

        }
    }

    /**
     * Check whether there are any unread transactions.
     *
     * @return True if there are more unread transactions
     */
    public abstract boolean hasNext() throws TransactionValidationException;

    /**
     * Reads the next transaction from the file.
     *
     * @return The next Transaction object created by reading the file
     *
     * @throws TransactionValidationException
     */
    public abstract List<Transaction> readNextBatch() throws TransactionValidationException;

    /**
     * Closes a reader object if applicable.
     *
     * @throws TransactionValidationException
     */
    public void close() throws TransactionValidationException {

        try {
            reader.close();
        } catch (IOException e) {
            throw new TransactionValidationException("Failed to close the reader", e);
        }

    }
}
