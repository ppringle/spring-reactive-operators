package com.pringweb.r2dbc.springdata;

import com.pringweb.r2dbc.Customer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class CustomerRepositoryTests extends DBIntegrationTest {

    private static String CUSTOMER_EMAIL = "first@email.com";
    private static String CUSTOMER_EMAIL2 = "second@email.com";
    private static String CUSTOMER_EMAIL3 = "third@email.com";

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    public void init(){
        StepVerifier.create(customerRepository.deleteAll())
                .verifyComplete();
    }

    @Test
    public void findByEmail_shouldReturnCustomerWithThatEmail(){

        StepVerifier.create(insertFlux(CUSTOMER_EMAIL3))
                .expectNextCount(1).verifyComplete();

        var findElementByEmail = customerRepository.findByEmail(CUSTOMER_EMAIL3);

        StepVerifier.create(findElementByEmail)
                .expectNextMatches(c -> c.getEmail().equalsIgnoreCase(CUSTOMER_EMAIL3)).verifyComplete();
    }

    @Test
    public void insert_customer_shouldInsertNewRow() {

        StepVerifier.create(insertFlux(CUSTOMER_EMAIL))
                .expectNextMatches(customer -> customer.getId() != null &&
                        customer.getEmail().equalsIgnoreCase(
                                CUSTOMER_EMAIL))
                .verifyComplete();
    }

    @Test
    public void update_customer_shouldUpdateRecord() {

        var save = customerRepository.save(new Customer(null, CUSTOMER_EMAIL));

        StepVerifier
                .create(save.log())
                .expectNextMatches(p -> p.getId() != null)
                .verifyComplete();

        StepVerifier
                .create(customerRepository.findAll())
                .expectNextCount(1)
                .verifyComplete();

        var updateFlux = customerRepository
                .findAll()
                .map(c -> new Customer(c.getId(), c.getEmail().toUpperCase()))
                .flatMap(customerRepository::save);

        StepVerifier
                .create(updateFlux)
                .expectNextMatches(c -> c.getEmail().equals(CUSTOMER_EMAIL.toUpperCase()))
                .verifyComplete();
    }

    @Test
    public void deleteCustomer_shouldDeleteRecord() {

        AtomicInteger customerIdToBeDeleted = new AtomicInteger();

        StepVerifier.create(insertFlux(CUSTOMER_EMAIL)).expectNextCount(1).verifyComplete();

        customerRepository.findAll().take(1).doOnNext(c -> customerIdToBeDeleted.set(c.getId()));

        Mono<Void> customerToBeDeleted = customerRepository.deleteById(customerIdToBeDeleted.get());
        StepVerifier.create(customerToBeDeleted)
                .expectNextCount(0)
                .verifyComplete();

    }

    @Test
    public void findById_shouldReturnCustomer() {

        StepVerifier.create(insertFlux(CUSTOMER_EMAIL, CUSTOMER_EMAIL2, CUSTOMER_EMAIL3))
                .expectNextCount(3)
                .verifyComplete();

        var recordsById = customerRepository.findAll()
                .flatMap(customer -> Mono.zip(Mono.just(customer),
                        customerRepository.findById(customer.getId())))
                .filterWhen(tuple2 -> Mono.just(tuple2.getT1().equals(tuple2.getT2())));

        StepVerifier.create(recordsById)
                .expectNextCount(3)
                .verifyComplete();

    }

    private Flux<Customer> insertFlux(String... email) {
        return Flux.fromArray(email).map(e -> new Customer(null, e)).flatMap(customerRepository::save);
    }

}
