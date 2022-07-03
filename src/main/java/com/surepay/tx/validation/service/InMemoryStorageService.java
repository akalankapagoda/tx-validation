package com.surepay.tx.validation.service;

import com.surepay.tx.validation.exception.TransactionValidationException;
import com.surepay.tx.validation.model.TransactionValidationResults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Storage service implementation which is storing everything in-memory.
 *
 * Warning! Not suitable for production.
 */
@Service
public class InMemoryStorageService implements StorageService {

    private Map<String, MultipartFile> files = new HashMap<>();

    private Map<String, TransactionValidationResults> validationResultsMap = new HashMap<>();

    @Override
    public void saveFile(String identifier, MultipartFile file) throws TransactionValidationException {
        files.put(identifier, file);
    }

    @Override
    public InputStream readFile(String identifier) throws TransactionValidationException {

        MultipartFile file = files.get(identifier);

        InputStream stream = null;

        if (file != null) {
            try {
                stream = file.getInputStream();
            } catch (IOException e) {
                throw new TransactionValidationException("Failed to load file : " + identifier, e);
            }
        }

        return stream;
    }

    @Override
    public boolean deleteFile(String identifier) throws TransactionValidationException {

        if (files.remove(identifier) != null) {
            return true;
        }

        return false;
    }

    @Override
    public void saveValidationResults(TransactionValidationResults validationResults)
            throws TransactionValidationException {
        validationResultsMap.put(validationResults.getIdentifier(), validationResults);
    }

    @Override
    public TransactionValidationResults readValidationResults(String identifier)
            throws TransactionValidationException {

        return validationResultsMap.get(identifier);
    }
}
