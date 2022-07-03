package com.surepay.tx.validation;

import com.surepay.tx.validation.controller.TransactionValidationController;
import com.surepay.tx.validation.service.StorageService;
import com.surepay.tx.validation.service.TransactionValidationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Application tests to make sure the Spring application is properly initialized.
 */
@SpringBootTest
class ValidationApplicationTests {

	@Autowired
	private TransactionValidationController transactionValidationController;

	@Autowired
	private TransactionValidationService transactionValidationService;

	@Autowired
	private StorageService storageService;

	@Test
	void contextLoads() {
		assertThat(transactionValidationController).isNotNull();

		assertThat(transactionValidationService).isNotNull();

		assertThat(storageService).isNotNull();
	}

}
