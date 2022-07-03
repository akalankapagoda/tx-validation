package com.surepay.tx.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.surepay.tx.validation.model.ResponseMessage;
import com.surepay.tx.validation.model.ResponseStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Verify that the HTTP server is properly functioning.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ServerRequestTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void helloShouldReturnDefaultMessage() throws Exception {

        ResponseEntity<ResponseMessage> responseEntity  =
                this.restTemplate.getForEntity("http://localhost:" + port + "/transaction/validation/hello",
                        ResponseMessage.class);

        ResponseMessage body = mapper.convertValue(responseEntity.getBody(), ResponseMessage.class);

        assertThat(body.getStatus()).isEqualTo(ResponseStatus.SUCCESS);
    }
}
