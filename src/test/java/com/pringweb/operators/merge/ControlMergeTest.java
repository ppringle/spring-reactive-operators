package com.pringweb.operators.merge;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class ControlMergeTest {

    @Test
    public void merge() {

        Flux<Integer> fastest = Flux.just(5, 6);
        Flux<Integer> secondFastest = Flux.just(1, 2).delayElements(Duration.ofMillis(2));
        Flux<Integer> thirdFastest = Flux.just(3, 4).delayElements(Duration.ofMillis(20));

        Flux<Flux<Integer>> streamOfStreams = Flux.just(fastest, secondFastest, thirdFastest);

        Flux<Integer> merge = Flux.merge(streamOfStreams);

        StepVerifier.create(merge).expectNext(5, 6, 1, 2, 3, 4).verifyComplete();

    }

}
