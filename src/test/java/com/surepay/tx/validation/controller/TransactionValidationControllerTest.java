package com.surepay.tx.validation.controller;

import com.surepay.tx.validation.exception.TransactionValidationException;
import com.surepay.tx.validation.model.ResponseMessage;
import com.surepay.tx.validation.model.ResponseStatus;
import com.surepay.tx.validation.model.TransactionValidationResults;
import com.surepay.tx.validation.service.StorageService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Test TransactionValidationControllerTest.
 */
@SpringBootTest
public class TransactionValidationControllerTest {

    @Autowired
    private TransactionValidationController transactionValidationController;

    @MockBean
    private StorageService mockStorageService;

    private static TransactionValidationResults testResults;

    private static final String TEST_IDENTIFIER = "test-identifier";

    @BeforeAll
    private static void init() {

        testResults = new TransactionValidationResults(TEST_IDENTIFIER);
        testResults.setStatus(ResponseStatus.SUBMITTED);
    }

    @Test
    public void testValidateTransactions() throws TransactionValidationException {
        String nonEmptyFileName = "non-empty-file-name";

        MultipartFile nonEmptyMultipartFile = new MockMultipartFile(nonEmptyFileName, "content".getBytes());

        doNothing().when(mockStorageService).saveFile(TEST_IDENTIFIER, nonEmptyMultipartFile);
        doNothing().when(mockStorageService).saveValidationResults(testResults);

        ResponseMessage response = transactionValidationController.validateTransactions(TEST_IDENTIFIER,
                nonEmptyMultipartFile, "json");

        assertThat(response instanceof TransactionValidationResults).isTrue();

        assertThat(((TransactionValidationResults) response).getIdentifier()).isEqualTo(TEST_IDENTIFIER);

        assertThat((response).getStatus()).isEqualTo(ResponseStatus.SUBMITTED);
    }

    /**
     * Test with empty file for invalid input.
     *
     * @throws TransactionValidationException
     */
    @Test
    public void testValidateTransactionsWithInvalidInput() throws TransactionValidationException {
        String emptyFileName = "empty-file-name";

        MultipartFile emptyMultipartFile = new MockMultipartFile(emptyFileName, new byte[0]);

        doNothing().when(mockStorageService).saveFile(TEST_IDENTIFIER, emptyMultipartFile);
        doNothing().when(mockStorageService).saveValidationResults(testResults);

        assertThatThrownBy(() -> transactionValidationController.validateTransactions(TEST_IDENTIFIER,
                emptyMultipartFile, "json"))
                .isInstanceOf(TransactionValidationException.class)
                .hasMessageContaining("Invalid input");
        ;
    }

    /**
     * Test if we get the correct validation results.
     *
     * @throws TransactionValidationException
     */
    @Test
    public void testGetValidationResults() throws TransactionValidationException {
        doNothing().when(mockStorageService).saveValidationResults(testResults);
        when(mockStorageService.readValidationResults(TEST_IDENTIFIER)).thenReturn(testResults);

        ResponseMessage response = transactionValidationController.getValidationResults(TEST_IDENTIFIER);

        assertThat(response instanceof TransactionValidationResults).isTrue();

        assertThat(((TransactionValidationResults) response).getIdentifier()).isEqualTo(TEST_IDENTIFIER);
    }
}
