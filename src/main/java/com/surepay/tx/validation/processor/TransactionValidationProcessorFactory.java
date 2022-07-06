package com.surepay.tx.validation.processor;

import com.surepay.tx.validation.config.CSVConfigProperties;
import com.surepay.tx.validation.exception.TransactionValidationException;
import com.surepay.tx.validation.model.FileType;
import com.surepay.tx.validation.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Factory to create validation processors for different file types.
 */
@Component
public class TransactionValidationProcessorFactory {

    @Autowired
    private StorageService storageService;

    @Autowired
    private CSVConfigProperties csvConfigProperties;

    /**
     * Get transaction validation processor for a given file type.
     *
     * @param identifier The file identifier
     * @param type The type of the file
     * @return A {@link TransactionValidationProcessor}
     */
    public TransactionValidationProcessor getTransactionValidationProcessor(String identifier, FileType type)
            throws TransactionValidationException {

        if (type == null) {
            throw new TransactionValidationException(
                    "File type is empty. Cannot identify a validation processor to use.");
        }

        TransactionValidationProcessor processor;

        if (FileType.CSV == type) {
            processor = new CSVTransactionValidationProcessor(identifier, storageService, csvConfigProperties);
        } else if (FileType.JSON == type) {
            processor = new JSONTransactionValidationProcessor(identifier, storageService, csvConfigProperties);
        } else {
            throw new TransactionValidationException("Unsupported file type : " + type.getType());
        }

        return processor;

    }
}
