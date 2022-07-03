package com.surepay.tx.validation.service;

import com.surepay.tx.validation.exception.TransactionValidationException;
import com.surepay.tx.validation.model.TransactionValidationResults;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * Storage service to handle input and processing results storage.
 */
@Component
public interface StorageService {

    void saveFile(String identifier, MultipartFile file) throws TransactionValidationException;

    InputStream readFile(String identifier) throws TransactionValidationException;

    boolean deleteFile(String identifier) throws TransactionValidationException;

    void saveValidationResults(TransactionValidationResults results) throws TransactionValidationException;

    TransactionValidationResults readValidationResults(String identifier) throws TransactionValidationException;
}
