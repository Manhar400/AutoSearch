package com.example.demo;

import com.example.demo.mongo.model.response.Output;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * It's a JUnit test class that uses the Spring Boot Test annotation to start up a Spring Boot application
 */
@SpringBootTest
class AutoCompleteTests {

    @Autowired
    SearchDb searchDb;

    @Test
    public void whenTestingForQueryResults() {
        Mono<Output> expected= Mono.just(new Output("book",List.of("harry potter books","mc book","bookshelf speaker","book rack","baby book","reading books","sketch book","bengali book","atomic habit book","book table")));
        Mono<Output> actual = searchDb.getSuggestion("book");
        String exp = Objects.requireNonNull(expected.block()).getSuggestion().toString();
        String act = Objects.requireNonNull(actual.block()).getSuggestion().toString();
        assertThat(exp)
                .isEqualTo(act);
        assertThat(exp.equals(act)).isTrue();
    }


    @Test
    public void whenTestingForEmptyString() throws Exception {
        List<String> actual = Objects.requireNonNull(searchDb.getSuggestion("").block()).getSuggestion();
        String exp = List.of("").toString();
        String act = actual.toString();
        assertThat(exp)
                .isEqualTo(act);
        assertThat(exp.equals(act)).isTrue();
    }
}
