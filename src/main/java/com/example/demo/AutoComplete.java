package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * This class is the entry point for the application. It enables caching, Swagger, and Spring Boot. It also scans the
 * package "com.example" for components
 */
@EnableCaching
@EnableSwagger2
@SpringBootApplication
@ComponentScan("com.example")
public class AutoComplete {
    public static void main(String[] args) {
        SpringApplication.run(AutoComplete.class, args);
    }
    @Bean
    public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.basePackage("com.example")).build();
    }
}

