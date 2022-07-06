package com.surepay.tx.validation.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configs related to CSV processing.
 */
@Configuration
@ConfigurationProperties(prefix = "csv")
public class CSVConfigProperties {

    /**
     * CSV processing batch size;
     */
    private Integer batchSize;

    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }
}
