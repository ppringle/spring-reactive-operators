package com.pringweb;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class ConcatMapTest {

    @Test
    public void concatMap() {

        Flux<Integer> data =
                Flux.just(new ConcatMapTest.Pair(1, 300), new ConcatMapTest.Pair(2, 200),
                                new ConcatMapTest.Pair(3, 100))
                        .concatMap(value -> this.delayReplyFor(value.id, value.delay));

        StepVerifier.create(data).expectNext(1, 2, 3).verifyComplete();

    }

    private Flux<Integer> delayReplyFor(Integer id, long delay) {
        return Flux.just(id).delayElements(Duration.ofMillis(delay));
    }

    @NoArgsConstructor
    @AllArgsConstructor
    static class Pair {

        private Integer id;
        private long delay;

    }
}
