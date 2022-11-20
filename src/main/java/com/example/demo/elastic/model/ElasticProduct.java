package com.example.demo.elastic.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

/**
 * The class is annotated with @Document, which tells Elasticsearch that this class is a document that can be indexed. The
 * indexName is the name of the index that will be created in Elasticsearch. The @Id annotation tells Elasticsearch that
 * the id field is the document id. The @Field annotation tells Elasticsearch that the field is a field that can be
 * indexed. The type parameter tells Elasticsearch what type of data the field contains
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "elasticdb")
public class ElasticProduct implements Serializable {
    @Id
    private String id;
    @Field(type = FieldType.Keyword)
    private String pk;
    @Field(type = FieldType.Keyword)
    private String searchTerm;
    @Field(type = FieldType.Keyword)
    private String normalizedTerm;
    @Field(type = FieldType.Double)
    private Double v1Score;
}