package com.example.demo;

import com.example.demo.mongo.model.response.Output;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * It's an abstract class that has a method called GetSuggestion that takes a string as an input and returns a Mono of type
 * Output
 */
@Component
public abstract class SearchDb {
    public abstract Mono<Output> getSuggestion(String query);
}
