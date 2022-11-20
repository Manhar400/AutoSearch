package com.example.demo.elastic.service.impl;

import com.example.demo.configuration.ElasticConfiguration;
import com.example.demo.elastic.model.ElasticProduct;
import com.example.demo.elastic.service.ElasticRepository;
import com.example.demo.mongo.model.response.Output;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.script.mustache.SearchTemplateRequest;
import org.elasticsearch.script.mustache.SearchTemplateResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.demo.constants.AppConstants.*;

@Slf4j
@ConditionalOnProperty(prefix = "search", name = "engine", havingValue = "elastic")
@Component
/*
 * It's a class that implements the ElasticRepository interface
 */
public class ElasticRepositoryImpl extends ElasticRepository {

    final ElasticConfiguration elasticsearchConfiguration;
    private final ReactiveHashOperations<String, String, Output> hashOperations;

    ElasticRepositoryImpl(ReactiveRedisOperations<String, Output> redisOperations, ElasticConfiguration elasticsearchConfiguration) {
        this.hashOperations = redisOperations.opsForHash();
        this.elasticsearchConfiguration = elasticsearchConfiguration;
    }
    @Override
    public Mono<Output> getSuggestion(String queryWithoutTrimming) {
        String query = queryWithoutTrimming.trim();
        List<ElasticProduct> data = new ArrayList<>();
        log.info( "The query is " + (query.length() < 2 ? "INVALID" : query));
        List<String> suggestions = new ArrayList<>();
        if(query.length() < 2) {
            return Mono.just(new Output(query,suggestions));
        }
        try {
            boolean hash = Boolean.TRUE.equals(hashOperations.hasKey(REDIS_KEY_ELASTIC, query).block());
            if (hash) {
                log.info("Used Redis in Elastic for " + query);
                return hashOperations.get(REDIS_KEY_ELASTIC, query);
            }
            getResponse(ELASTIC_TEMPLATE, data, query, "searchTerm", "match_phrase_prefix");
            log.info("[ElasticSearchDB][PrefixMatch] Suggestion found from Prefix:: " + data.size());
            if (data.size() < 10) {
                int termFound = data.size();
                log.info("[ElasticSearchDB][WildCard] Suggestions to be found from WildCard Query:: " + (10 - data.size()));
                log.info("[ElasticSearchDB][WildCard]Term passed in Wildcard query:: " + "*" + query + "*");
                getResponse(ELASTIC_TEMPLATE, data, "*" + query + "*", "searchTerm", "wildcard");
                log.info("[ElasticSearchDB][WildCard] Suggestions found from WildCard Query:: " + (data.size() - termFound));
            }
            if (data.size() < 10) {
                int remaining = 10 - data.size();
                int prev = data.size();
                log.info("[ElasticSearchDB][Fuzziness] Suggestions to be found from Fuzzy Query " + (remaining));
                getResponse(TEMPLATE_FUZZY, data, query, "searchTerm", "fuzzy");
                log.info("[ElasticSearchDB][Fuzziness] Suggestions found from Fuzzy Query " + (data.size() - prev));
            }
        } catch (Exception exception) {
            log.warn("[ElasticSearchDB] Exception is found:: " + exception);
        }
        for (ElasticProduct product : data) {
            suggestions.add(product.getSearchTerm());
        }
        log.info("[ElasticSearchDB] Response Found:: " + suggestions);
        Output result = new Output(query, suggestions);
        if(suggestions.isEmpty()) {
            return Mono.just(result);
        }
        return hashOperations.put(REDIS_KEY_ELASTIC, query, new Output(query, suggestions)).map(isSaved -> result).thenReturn(result);
    }

    // A method that is used to get the response from the elastic search.

    /**
     * It takes a template, a list of ElasticProduct objects, a term, a field, and a query type, and then it uses the
     * template to query the database, and then it adds the results to the list of ElasticProduct objects
     *
     * @param TEMPLATE  The name of the template that you want to use.
     * @param data      This is the list of ElasticProduct objects that will be returned.
     * @param term      The term to be searched
     * @param field     The field to be searched
     * @param queryType This is the type of query you want to perform. For example, if you want to perform a match query,
     *                  you can pass "match" as the value.
     */
    public void getResponse(String TEMPLATE, List<ElasticProduct> data, String term, String field, String queryType) {
        try {
            SearchTemplateRequest searchTemplateRequest = new SearchTemplateRequest();
            searchTemplateRequest.setScriptType(ScriptType.STORED);
            searchTemplateRequest.setScript(TEMPLATE);
            searchTemplateRequest.setRequest(elasticsearchConfiguration.request);
            Map<String, Object> scriptParams = new HashMap<>();
            scriptParams.put("field", term);
            scriptParams.put("query_type", queryType);
            scriptParams.put("field_type", field);
            searchTemplateRequest.setScriptParams(scriptParams);
            SearchTemplateResponse searchTemplateResponse = this.elasticsearchConfiguration.restHighLevelClient.searchTemplate(searchTemplateRequest, RequestOptions.DEFAULT);
            SearchResponse response = searchTemplateResponse.getResponse();
            SearchHits responseHits;
            responseHits = response.getHits();

            SearchHit[] hits;
            hits = responseHits.getHits();
            for (SearchHit hit : hits) {
                String sourceAsString = hit.getSourceAsString();
                log.info("[ElasticSearchDB][insideGetResponse] Json Response:: " + sourceAsString);
                ElasticProduct fromJson = elasticsearchConfiguration.gson.fromJson(sourceAsString, ElasticProduct.class);
                log.info("[ElasticSearchDB][insideGetResponse] Json to Object:: " + fromJson);
                data.add(fromJson);
            }
        } catch (Exception exception) {
            log.warn("[Elasticsearch][insideGetResponse] Exception Found: " + exception);
        }
    }
}



