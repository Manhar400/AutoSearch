package com.example.demo.mongo.service.impl;

import com.example.demo.mongo.model.request.Product;
import com.example.demo.mongo.model.response.Output;
import com.example.demo.mongo.service.MongoService;
import com.example.demo.mongo.service.MongoProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo.constants.AppConstants.REDIS_KEY_MONGO;

/**
 * It's a MongoDB implementation of the SearchService interface
 */
@ConditionalOnProperty(prefix = "search", name = "engine", havingValue = "mongo")
@Slf4j
@Component
public class MongoServiceImpl extends MongoService {

    @Autowired
    private final MongoProductRepository productRepo;
    private final ReactiveHashOperations<String, String, Output> hashOperations;
    MongoServiceImpl(ReactiveRedisOperations<String, Output> redisOperations, MongoProductRepository productRepo) {
        this.productRepo = productRepo;
        this.hashOperations = redisOperations.opsForHash();
    }

    public Mono<Output> getSuggestion(String queryWithoutTrimming) {
        String query = queryWithoutTrimming.trim();
        log.info( "The query is " + (query.length() < 2 ? "INVALID" : query));
        List<String> suggestions = new ArrayList<>();
        if(query.length() < 2) {
            return Mono.just(new Output(query,suggestions));
        }
        try {
            boolean hasKey = Boolean.TRUE.equals(hashOperations.hasKey(REDIS_KEY_MONGO, query).block());
            if (hasKey) {
                log.info("Used Redis in Mongo for " + query);
                return hashOperations.get(REDIS_KEY_MONGO, query);
            }
            List<Product> documents = productRepo.getData(query);
            if (documents.size() < 10) {
                int RemainingDocument = 10 - documents.size();
                int fuzzy = (query.length() < 4 ? 1 : 2);
                log.info("Using Fuzzy for " + query + "and maxEdits is " + fuzzy);
                List<Product> RemainingSuggestion = productRepo.getData(query, fuzzy, RemainingDocument);
                documents.addAll(RemainingSuggestion);
            }
            for (Product product : documents) {
                suggestions.add(product.getQuery().get("search_term"));
            }
        } catch (Exception exception) {
            log.warn("The Exception is " + exception);
        }
        Output result = new Output(query, suggestions);
        if(suggestions.isEmpty()) {
            return Mono.just(result);
        }
        return hashOperations.put(REDIS_KEY_MONGO, query, new Output(query, suggestions)).map(isSaved -> result).thenReturn(result);
    }
}



