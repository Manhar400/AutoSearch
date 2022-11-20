package com.example.demo.configuration;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

import static com.example.demo.constants.AppConstants.*;

/**
 * It creates a RestHighLevelClient object which is used to connect to the Elasticsearch database
 */
@Slf4j
@Configuration
@SuppressWarnings("deprecation")
public class ElasticConfiguration {
    public final SearchRequest request = new SearchRequest(ELASTIC_INDEX);
    public RestHighLevelClient restHighLevelClient;
    public SearchSourceBuilder sourceBuilder;
    public Gson gson;

    @PostConstruct
    public void init() {
        log.info("RestHighLevelClient: Host: " + ELASTIC_HOST + " Port: " + ELASTIC_PORT);
        RestClientBuilder builder = RestClient
                .builder(new HttpHost(ELASTIC_HOST, ELASTIC_PORT, ELASTIC_SCHEME));

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(ELASTIC_USER, ELASTIC_PASSWORD));

        builder.setHttpClientConfigCallback(
                httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
        builder.setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder.setConnectTimeout(40000).setSocketTimeout(50000));

        this.restHighLevelClient = new RestHighLevelClient(builder);

        try {
            log.info("Pinging the elasticsearch Database");
            boolean pingResult = restHighLevelClient.ping(RequestOptions.DEFAULT);
            log.info("PingResult:: " + pingResult);
        } catch (Exception e) {
            log.error("Exception while pinging:: ", e);
        }
        sourceBuilder = new SearchSourceBuilder();
        gson = new Gson();
    }
}
