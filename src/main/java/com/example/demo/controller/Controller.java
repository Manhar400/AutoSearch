package com.example.demo.controller;

import com.example.demo.SearchDb;
import com.example.demo.mongo.model.response.Output;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

/**
 * It's a controller class that handles the requests from the frontend and calls the appropriate methods in the SearchDb
 * class
 */
@RestController
public class Controller {
    @Autowired
    private final SearchDb searchDb;


    public Controller(SearchDb searchDb) {
        this.searchDb = searchDb;
    }

    /**
     * It takes a query string, and returns a list of suggestions
     *
     * @param query The query string that you want to search for.
     * @return A list of suggestions for the query.
     */
    @CrossOrigin
    @GetMapping("/search/autocomplete/{query}")
    public Mono<Output> GetSuggestion(@PathVariable String query) {
        return searchDb.getSuggestion(query);
    }
    @CrossOrigin
    @GetMapping("/search/ui/{query}")
    public List<String> getSuggestionForUi(@PathVariable String query) {
        return Objects.requireNonNull(searchDb.getSuggestion(query).block()).getSuggestion();
    }
    @GetMapping("/welcome")
    public ModelAndView index() {
        return new ModelAndView();
    }

    @GetMapping("/error")
    public ModelAndView UrlError() {
        return new ModelAndView();
    }
}