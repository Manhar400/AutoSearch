package com.example.demo.elastic.service;

import com.example.demo.SearchDb;
import com.example.demo.mongo.model.response.Output;
import reactor.core.publisher.Mono;

/**
 * The `ElasticRepository` class is an abstract class that extends the `SearchDb` class. It has one abstract method called
 * `GetSuggestion` that takes a `String` and returns a `Mono<Output>`
 */
public abstract class ElasticRepository extends SearchDb {
    public abstract Mono<Output> getSuggestion(String term);
}
