package com.pringweb.r2dbc.springdata;

import com.pringweb.r2dbc.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final TransactionalOperator transactionalOperator;

    public Flux<Customer> upsert(String email){

        var customers = customerRepository.findAll()
                .filter(c -> c.getEmail().equalsIgnoreCase(email))
                .flatMap(c -> customerRepository.save(new Customer(c.getId(), email)))
                .switchIfEmpty(customerRepository.save(new Customer(null, email)));

        var validatedResults = errorIfEmailsAreInvalid(customers);

        return this.transactionalOperator.transactional(validatedResults);
    }

    @Transactional
    public Flux<Customer> normalizeEmails(){

        return errorIfEmailsAreInvalid(customerRepository.findAll()
                .flatMap(c -> this.upsert(c.getEmail().toUpperCase())));

    }

    private Flux<Customer> errorIfEmailsAreInvalid(Flux<Customer> customers) {

        return customers.filter(c -> c.getEmail().contains("@"))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Email is invalid !")));

    }


}
