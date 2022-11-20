package com.example.demo.controller;

import com.example.demo.SearchDb;
import com.example.demo.mongo.model.response.Output;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.servlet.ModelAndView;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;
@SpringBootTest
public class ControllerTest {
    @Mock
    SearchDb searchDb;
    @InjectMocks
    Controller controller;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetSuggestion() throws Exception {
        when(searchDb.getSuggestion(anyString())).thenReturn(null);
        Mono<Output> result = controller.GetSuggestion("query");
        Assert.assertEquals(null, result);
    }

    @Test
    public void testIndex() throws Exception {
        ModelAndView result = controller.index();
        Assert.assertEquals(null, result);
    }

    @Test
    public void testUrlError() throws Exception {
        ModelAndView result = controller.UrlError();
        Assert.assertEquals(null, result);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme