package com.example.demo.mongo.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * It's a POJO that represents a document in the MongoDB collection called "product"
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "product")
public class MongoProduct implements Serializable {
    @Id
    private String id;
    @Field("search_term")
    private String searchTerm;
    @Field("normalised_term")
    private String normalisedTerm;
    @Field("v1_score")
    private String v1Score;
}
