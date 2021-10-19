package com.pringweb.r2dbc.springdata;

import com.pringweb.r2dbc.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

@SpringBootTest
public class CustomerServiceTests extends DBIntegrationTest {

    private final String INVALID_EMAIL = "invalidemail.com";
    private final String VALID_EMAIL = "valid@email.com";

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerService customerService;

    @BeforeEach
    public void init(){
        StepVerifier.create(customerRepository.deleteAll())
                .verifyComplete();
    }

    @Test
    public void upsert_shouldThrowErrorAndNotInsertRecordIfProvidedEmailIsInvalid(){
        var error = customerService.upsert(INVALID_EMAIL);
        StepVerifier.create(error).expectError(IllegalArgumentException.class).verify();
        StepVerifier.create(customerRepository.findAll()).expectNextCount(0);
    }

    @Test
    public void upsert_shouldInsertRecordIfProvidedEmailIsValid(){
        var valid = customerService.upsert(VALID_EMAIL);

        StepVerifier.create(valid)
                .expectNextMatches(c -> c.getEmail().equalsIgnoreCase(VALID_EMAIL))
                .verifyComplete();

        StepVerifier.create(customerRepository.findAll().take(1))
                .expectNextMatches(c -> c.getEmail().equalsIgnoreCase(VALID_EMAIL))
                .verifyComplete();
    }

    @Test
    public void normalizeEmails_usingInvalidEmailShouldNotUpdateTheRecord(){

        StepVerifier.create(customerRepository.save(new Customer(null, INVALID_EMAIL)))
                .expectNextCount(1).verifyComplete();

        var attemptToNormalizeInvalidEmail = customerService.normalizeEmails();

        StepVerifier.create(attemptToNormalizeInvalidEmail)
                .expectError(IllegalArgumentException.class).verify();

    }

    @Test
    public void normalizeEmails_usingValidEmailShouldUpdateTheRecord(){

        StepVerifier.create(customerRepository.save(new Customer(null, VALID_EMAIL)))
                .expectNextCount(1).verifyComplete();

        var attemptToNormalizeValidEmail = customerService.normalizeEmails();

        StepVerifier.create(attemptToNormalizeValidEmail)
                .expectNextMatches(c -> c.getEmail().equals(VALID_EMAIL.toUpperCase())).verifyComplete();

    }

}
