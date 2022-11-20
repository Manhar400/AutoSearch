package com.example.demo.mongo.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * It's a POJO that has two fields, query and suggestion, and a constructor that takes two arguments, query and suggestion
 */
@Getter
@Builder
@NoArgsConstructor
public class Output implements Serializable {
    private String query;
    private List<String> suggestion;

    public Output(String query, List<String> suggestion) {
        this.query = query;
        this.suggestion = suggestion;
    }
}
