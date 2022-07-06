package com.surepay.tx.validation.processor;

import com.surepay.tx.validation.config.CSVConfigProperties;
import com.surepay.tx.validation.exception.TransactionValidationException;
import com.surepay.tx.validation.model.ResponseStatus;
import com.surepay.tx.validation.model.Transaction;
import com.surepay.tx.validation.model.TransactionValidationResults;
import com.surepay.tx.validation.service.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
class JSONTransactionValidationProcessorTest {

    @MockBean
    private static CSVConfigProperties mockCSVConfigProperties;

    @MockBean
    private static StorageService mockStorageService;

    private static JSONTransactionValidationProcessor processor;

    private static final String TEST_IDENTIFIER = "testJSON";

    @BeforeEach
    private void setupTest() throws TransactionValidationException, IOException {

        InputStream inputStream = new ClassPathResource("records.json").getInputStream();
        when(mockStorageService.readFile(TEST_IDENTIFIER)).thenReturn(inputStream);

        TransactionValidationResults results = new TransactionValidationResults(TEST_IDENTIFIER);
        results.setStatus(ResponseStatus.SUBMITTED);

        when(mockStorageService.readValidationResults(TEST_IDENTIFIER)).thenReturn(results);

        processor = new JSONTransactionValidationProcessor(TEST_IDENTIFIER, mockStorageService, mockCSVConfigProperties);
    }

    @Test
    void hasNext() throws TransactionValidationException, IOException {
        assertThat(processor.hasNext()).isTrue();
    }

    @Test
    void readNextBatch() throws TransactionValidationException {

        List<Transaction> transactions  = processor.readNextBatch();

        assertThat(transactions.size()).isEqualTo(10);

        assertThat(processor.hasNext()).isFalse();

    }
}