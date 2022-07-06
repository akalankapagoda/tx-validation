package com.surepay.tx.validation.processor;

import com.surepay.tx.validation.config.CSVConfigProperties;
import com.surepay.tx.validation.exception.TransactionValidationException;
import com.surepay.tx.validation.model.ResponseStatus;
import com.surepay.tx.validation.model.Transaction;
import com.surepay.tx.validation.service.StorageService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A transaction validation processor for processing and validation transactions in a CSV file.
 */
public class CSVTransactionValidationProcessor extends TransactionValidationProcessor {

    private static final Logger logger = Logger.getLogger(CSVTransactionValidationProcessor.class.getName());

    /**
     * Batch size for processing.
     */
    private int batchSize;

    /**
     * The default batch size if configs didn't specify one.
     */
    private static final int DEFAULT_BATCH_SIZE = 1000;

    private static final int NO_OF_COLUMNS = 6;

    private static final String CSV_DELIMITER = ",";

    public CSVTransactionValidationProcessor(String identifier, StorageService storageService,
                                             CSVConfigProperties CSVConfigProperties) throws TransactionValidationException {
        super(identifier, storageService, CSVConfigProperties);


        if (CSVConfigProperties.getBatchSize() == null) {
            batchSize = DEFAULT_BATCH_SIZE;
        } else {
            batchSize = CSVConfigProperties.getBatchSize();
        }
    }

    @Override
    public boolean hasNext() throws TransactionValidationException {
        try {
            return reader.ready();
        } catch (IOException e) {
            throw new TransactionValidationException("Failed to check if the file " + identifier + " has more records", e);
        }
    }

    @Override
    public List<Transaction> readNextBatch() throws TransactionValidationException {

        List<Transaction> transactions = new ArrayList<>();
        try {
            for (int i = 0; i < batchSize; i++) {

                if (!reader.ready()) { // No more lines to read
                    break;
                }

                String lineString = reader.readLine();

                String[] line = lineString.split(CSV_DELIMITER);

                if (line.length != NO_OF_COLUMNS) {
                    logger.log(Level.WARNING, "Skipping invalid line");
                    continue;
                }

                Transaction transaction = new Transaction();

                transaction.setReference(line[0]);
                transaction.setAccountNumber(line[1]);
                transaction.setDescription(line[2]);

                try {
                    transaction.setStartBalance(new BigDecimal(line[3]));
                    transaction.setMutation(new BigDecimal(line[4]));
                    transaction.setEndBalance(new BigDecimal(line[5]));
                } catch (NumberFormatException e) {
                    // Note that we're going to run into this each time the CSV contains headers for the first line
                    logger.log(Level.WARNING, "Skipping invalid line  : lineString", e);
                    continue;
                }

                transactions.add(transaction);


            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to read the CSV stream.", e);
            results.setStatus(ResponseStatus.FAIL);
            results.setMessage("Failed to read the CSV stream.");
        }

        return transactions;
    }
}
