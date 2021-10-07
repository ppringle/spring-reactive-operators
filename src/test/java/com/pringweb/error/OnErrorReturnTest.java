package com.pringweb.error;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class OnErrorReturnTest {

    private final Flux<Integer> resultsInError = Flux.just(1, 2, 3).flatMap(value -> {
        if (value == 2) {
            return Flux.error(new IllegalArgumentException("Ooops !"));
        }
        return Flux.just(value);
    });

    @Test
    public void onErrorReturn() {
        Flux<Integer> integerFlux = resultsInError.onErrorReturn(IllegalArgumentException.class, 2);
        StepVerifier.create(integerFlux).expectNext(1, 2).verifyComplete();
    }

}
