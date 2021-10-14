package com.pringweb.r2dbc.springdata;

import com.pringweb.r2dbc.Customer;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {

    @Query("select id, email from customer c where c.email = :email")
    Flux<Customer> findByEmail(String email);

}