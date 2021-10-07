package com.pringweb.controlFlow;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class ControlFlowRetryTest {

    @Test
    public void retry(){

        var errored = new AtomicBoolean();

        Flux<String> producer =  Flux.create((sink) -> {
            if(!errored.get()){
                errored.set(true);
                sink.error(new IllegalArgumentException("Ooops !"));
            }else{
                sink.next("Hello");
            }
            sink.complete();
        });

        Flux<String> retryOnError = producer.retry();

        StepVerifier.create(retryOnError).expectNext("Hello").verifyComplete();
        assertTrue(errored.get());

    }

}
