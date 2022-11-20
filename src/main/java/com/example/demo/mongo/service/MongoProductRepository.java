package com.example.demo.mongo.service;

import com.example.demo.mongo.model.request.MongoProduct;
import com.example.demo.mongo.model.request.Product;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

// This is a Spring Data MongoDB repository. It is used to query the MongoDB database.
@ConditionalOnProperty(prefix = "search", name = "engine", havingValue = "mongo")
public interface MongoProductRepository extends MongoRepository<MongoProduct, String> {
    @Aggregation(pipeline = {"{'$search': {'index': 'default','autocomplete': {'query': ?0, 'path': 'search_term', 'tokenOrder': 'sequential'}}}",
            "{'$limit':50}",
            "{'$group': {'_id': '$normalized_term','Query': {'$max': {'score': '$v1_score','search_term': '$search_term'}}}}",
            "{'$sort':{'Query.score': -1}}",
            "{'$limit':10}",
            "{'$project': {'_id':0, 'Query.search_term' : 1}}"
    })
    List<Product> getData(String query);
    @Aggregation(pipeline = {"{'$search': {'index': 'default','autocomplete': {'query': ?0, 'path': 'search_term', 'tokenOrder': 'sequential','fuzzy':{'maxEdits':?1}}}}",
            "{'$limit':40}",
            "{'$group': {'_id': '$normalized_term','Query': {'$max': {'score': '$v1_score','search_term': '$search_term'}}}}",
            "{'$sort':{'Query.score': -1}}",
            "{'$limit':?2}",
            "{'$project': {'_id':0, 'Query.search_term' : 1}}"
    })
    List<Product> getData(String query, int fuzzy, int limit);
}