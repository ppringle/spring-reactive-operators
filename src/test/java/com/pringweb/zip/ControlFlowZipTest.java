package com.pringweb.zip;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class ControlFlowZipTest {

    @Test
    public void zip() {
        Flux<Integer> integers = Flux.just(1, 2, 3);
        Flux<String> letters = Flux.just("A", "B", "C");
        Flux<String> zip = Flux.zip(integers, letters).map(tuple -> this.format(tuple.getT1(), tuple.getT2()));

        StepVerifier.create(zip).expectNext("1:A", "2:B", "3:C").verifyComplete();
    }

    private String format(Integer i, String s) {
        return i + ":" + s;
    }

}
