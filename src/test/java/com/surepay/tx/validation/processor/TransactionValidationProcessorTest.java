package com.surepay.tx.validation.processor;

import com.surepay.tx.validation.config.CSVConfigProperties;
import com.surepay.tx.validation.exception.TransactionValidationException;
import com.surepay.tx.validation.model.BaseTransaction;
import com.surepay.tx.validation.model.ResponseStatus;
import com.surepay.tx.validation.model.Transaction;
import com.surepay.tx.validation.model.TransactionValidationResults;
import com.surepay.tx.validation.service.InMemoryStorageService;
import com.surepay.tx.validation.service.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class TransactionValidationProcessorTest {


    @MockBean
    private static CSVConfigProperties mockCSVConfigProperties;

    private static StorageService storageService = new InMemoryStorageService();

    private static TransactionValidationProcessor processor;

    private static final String TEST_IDENTIFIER = "test";

    @BeforeEach
    private void setupTest() throws TransactionValidationException {

        // We will not be using this, but is invoked in the processor constructor, so we mock it here
        MultipartFile multipartFile = new MockMultipartFile(TEST_IDENTIFIER,
                TEST_IDENTIFIER, "text/plain", TEST_IDENTIFIER.getBytes());

        storageService.saveFile(TEST_IDENTIFIER, multipartFile);

        TransactionValidationResults results = new TransactionValidationResults(TEST_IDENTIFIER);
        results.setStatus(ResponseStatus.SUBMITTED);

        storageService.saveValidationResults(results);

        processor = new MockTransactionValidationProcess(TEST_IDENTIFIER, storageService, mockCSVConfigProperties);
    }

    @Test
    void testTransactionValidation() throws InterruptedException, TransactionValidationException {
        Thread thread = new Thread(processor);

        thread.start();

        thread.join(); // Wait for processing to complete

        TransactionValidationResults results = storageService.readValidationResults(TEST_IDENTIFIER);

        assertThat(results.getStatus()).isEqualTo(ResponseStatus.SUCCESS);

        Set<BaseTransaction> invalidRecords = results.getInvalidRecords();

        assertThat(invalidRecords.size()).isEqualTo(4);

        List<String> duplicateTxDescriptions = Arrays.asList("Description3", "Description4", "Description6");

        for (BaseTransaction invalidRecord : invalidRecords) {
            String ref = invalidRecord.getReference();

            if ("Ref2".equals(ref)) {
                assertThat(invalidRecord.getDescription()).isEqualTo("Description2");
            } else {
                assertThat(ref).isEqualTo("RefDuplicate");
                assertThat(invalidRecord.getDescription())
                        .matches(s -> duplicateTxDescriptions.stream().anyMatch(s::equals));
            }
        }
    }
}

/**
 * A mock class which doesn't rely on file reader layer of the processor implementation and purely focuses on the logic.
 */
class MockTransactionValidationProcess extends TransactionValidationProcessor {

    Map<Integer, List<Transaction>> batches;

    private int currentBatchNo = 1;

    MockTransactionValidationProcess(String identifier, StorageService storageService, CSVConfigProperties csvConfigProperties) throws TransactionValidationException {
        super(identifier, storageService, csvConfigProperties);

        batches = new HashMap<>();

        // Batch 1
        List<Transaction> batch1 = new ArrayList<>();

        Transaction validTransaction = new Transaction();
        validTransaction.setReference("Ref1");
        validTransaction.setAccountNumber("AcctNo");
        validTransaction.setDescription("Description1");

        validTransaction.setStartBalance(new BigDecimal("10.5"));
        validTransaction.setMutation(new BigDecimal("2.45"));
        validTransaction.setEndBalance(new BigDecimal("12.95"));

        batch1.add(validTransaction);

        Transaction invalidEndBalanceTx = new Transaction();
        invalidEndBalanceTx.setReference("Ref2");
        invalidEndBalanceTx.setAccountNumber("AcctNo");
        invalidEndBalanceTx.setDescription("Description2");

        invalidEndBalanceTx.setStartBalance(new BigDecimal("10.5"));
        invalidEndBalanceTx.setMutation(new BigDecimal("-2.45"));
        invalidEndBalanceTx.setEndBalance(new BigDecimal("12.95"));

        batch1.add(invalidEndBalanceTx);

        Transaction duplicateTxOriginal = new Transaction();
        duplicateTxOriginal.setReference("RefDuplicate");
        duplicateTxOriginal.setAccountNumber("AcctNo");
        duplicateTxOriginal.setDescription("Description3");

        duplicateTxOriginal.setStartBalance(new BigDecimal("10.5"));
        duplicateTxOriginal.setMutation(new BigDecimal("2.45"));
        duplicateTxOriginal.setEndBalance(new BigDecimal("12.95"));

        batch1.add(duplicateTxOriginal);

        Transaction duplicateTx = new Transaction();
        duplicateTx.setReference("RefDuplicate");
        duplicateTx.setAccountNumber("AcctNo");
        duplicateTx.setDescription("Description4");

        duplicateTx.setStartBalance(new BigDecimal("10.5"));
        duplicateTx.setMutation(new BigDecimal("2.45"));
        duplicateTx.setEndBalance(new BigDecimal("12.95"));

        batch1.add(duplicateTx);

        batches.put(1, batch1);
        // Batch 2
        List<Transaction> batch2 = new ArrayList<>();

        Transaction validTransaction2 = new Transaction();
        validTransaction2.setReference("Ref5");
        validTransaction2.setAccountNumber("AcctNo");
        validTransaction2.setDescription("Description5");

        validTransaction2.setStartBalance(new BigDecimal("10.5"));
        validTransaction2.setMutation(new BigDecimal("2.45"));
        validTransaction2.setEndBalance(new BigDecimal("12.95"));

        batch2.add(validTransaction2);

        Transaction duplicateTx2 = new Transaction();
        duplicateTx2.setReference("RefDuplicate");
        duplicateTx2.setAccountNumber("AcctNo");
        duplicateTx2.setDescription("Description6");

        duplicateTx2.setStartBalance(new BigDecimal("10.5"));
        duplicateTx2.setMutation(new BigDecimal("2.45"));
        duplicateTx2.setEndBalance(new BigDecimal("12.95"));

        batch2.add(duplicateTx2);

        batches.put(2, batch2);

    }

    @Override
    public boolean hasNext() throws TransactionValidationException {
        return batches.get(currentBatchNo) != null;
    }

    @Override
    public List<Transaction> readNextBatch() throws TransactionValidationException {
        return batches.get(currentBatchNo++);
    }
}