package com.example.demo.mongo.service;

import com.example.demo.SearchDb;
import com.example.demo.mongo.model.response.Output;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * > This is an abstract class that extends the SearchDb class. It is annotated with @Component, which means that it is a
 * Spring bean. It is also annotated with @ConditionalOnProperty, which means that it will only be instantiated if the
 * property "engine" in the "search" prefix is set to "mongo"
 */
@Component
@ConditionalOnProperty(prefix = "search", name = "engine", havingValue = "mongo")
public abstract class MongoService extends SearchDb {
    public abstract Mono<Output> getSuggestion(String query);
}
