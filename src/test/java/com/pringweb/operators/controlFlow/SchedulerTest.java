package com.pringweb.operators.controlFlow;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class SchedulerTest {

    //Schedulers.immediate() can be seen as the no-op scheduler, which the pipeline executes in the current thread.
    @Test
    public void immediate(){
        Flux.just("a", "b", "c", "d", "e")
                .log()
                .map(value -> value.toUpperCase())
                .subscribeOn(Schedulers.immediate())
                .subscribe(System.out::println);
    }


    @Test
    public void singleTest() throws InterruptedException {

        Flux.just("a", "b", "c", "d", "e")
                .log()
                .map(value -> value.toUpperCase())
                .subscribeOn(Schedulers.single())
                .subscribe(System.out::println);

        //As part of the work is passed to another thread, the sleep() call delays the application exit, so the
        // worker thread can complete the task.
        Thread.sleep(5000);
    }

    @Test
    public void boundedElasticTest() throws InterruptedException {

        List<String> words = new ArrayList<>();
        words.add("a.txt");
        words.add("b.txt");
        words.add("c.txt");

        Flux flux = Flux.fromArray(words.toArray(new String[0]))
                .publishOn(Schedulers.boundedElastic())
                .map(w -> scanFile(debug(w, "map")));

        flux.subscribe(y -> log.info("Element: {}, => subscriber1", y));
        flux.subscribe(y -> log.info("Element: {}, => subscriber2", y));

        Thread.sleep(5000);
    }

    @Test
    public void parallelTest() throws InterruptedException {

        Flux flux = Flux.range(1, 5)
                .publishOn(Schedulers.parallel())
                .map(v -> debug(v, "map"));

        flux.subscribe(w -> debug(w,"subscribe1"));
        flux.subscribe(w -> debug(w,"subscribe2"));
        flux.subscribe(w -> debug(w,"subscribe3"));
        flux.subscribe(w -> debug(w,"subscribe4"));
        flux.subscribe(w -> debug(w,"subscribe5"));

        Thread.sleep(5000);
    }


    public static <T> T debug(T el, String function) {
        log.info("Element: {}; in function => {}", el, function);
        return el;
    }

    private String scanFile(String filename){

        InputStream stream = getClass().getClassLoader().getResourceAsStream(filename);
        Scanner scanner = new Scanner(stream);
        String line = scanner.nextLine();
        scanner.close();

        return line;
    }

}
