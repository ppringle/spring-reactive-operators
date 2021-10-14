package com.pringweb.operators.controlFlow;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ControlFlowTimeoutTest {

    @Test
    public void timeout() {

        Flux<Integer> integers =
                Flux.just(1, 2, 3).delayElements(Duration.ofSeconds(1)).timeout(Duration.ofMillis(500)).onErrorResume(e -> {
                    assertTrue(e instanceof TimeoutException,
                            "This exception should be an instance of: " + TimeoutException.class);
                    return Flux.just(0);
                });

        StepVerifier.create(integers).expectNext(0).verifyComplete();

    }


}
