package com.pringweb.operators.error;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@Slf4j
public class OnErrorResumeTest {

    private final Flux<Integer> resultsInError = Flux.just(1, 2, 3).flatMap(value -> {
        if (value == 2) {
            return Flux.error(new IllegalArgumentException("Ooops !"));
        }
        return Flux.just(value);
    });

    @Test
    public void onErrorResume() {
        Flux<Integer> integerFlux = resultsInError.onErrorResume(IllegalArgumentException.class, e -> Flux.just(3, 2,
                1));
        StepVerifier.create(integerFlux).expectNext(1, 3, 2, 1).verifyComplete();
    }

}
