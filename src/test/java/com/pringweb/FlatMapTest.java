package com.pringweb;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FlatMapTest {

    @Test
    public void flatMap() {

        Flux<Integer> data =
                Flux.just(new Pair(1, 300), new Pair(2, 200), new Pair(3, 100))
                        .flatMap(value -> this.delayReplyFor(value.id, value.delay));

        StepVerifier.create(data).expectNext(3,2,1).verifyComplete();

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
