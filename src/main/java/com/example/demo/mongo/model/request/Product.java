package com.example.demo.mongo.model.request;

import org.bson.codecs.pojo.annotations.BsonProperty;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Map;

/**
 * The class Product has a Map<String, String> field called Query
 */
@Document
public class Product implements Serializable {
    @BsonProperty(value = "Query")
    private Map<String, String> Query;

    public Map<String, String> getQuery() {
        return Query;
    }

    public void setQuery(Map<String, String> Query) {
        this.Query = Query;
    }
}
