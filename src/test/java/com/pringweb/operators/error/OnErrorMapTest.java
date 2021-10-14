package com.pringweb.operators.error;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OnErrorMapTest {

    @Test
    public void onErrorMap() {

        class GenericException extends RuntimeException {
        }

        var counter = new AtomicInteger();

        Flux<Integer> resultsInError = Flux.error(new IllegalArgumentException("ooops"));

        Flux<Integer> exceptionHandlingStream = resultsInError
                .onErrorMap(IllegalArgumentException.class, ex -> new GenericException())
                .doOnError(GenericException.class, ge -> counter.incrementAndGet());

        StepVerifier.create(exceptionHandlingStream).expectError().verify();
        assertEquals(counter.get(), 1);

    }

}
