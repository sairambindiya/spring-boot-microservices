package com.vibhaneha.bookstore.orders.clients.catalog;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProductServiceClient {
    private static final Logger log = LoggerFactory.getLogger(ProductServiceClient.class);

    private final RestClient restClient;

    public ProductServiceClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @CircuitBreaker(name = "catalog-service")
    @Retry(name = "catalog-service", fallbackMethod = "getProductByCodeFallBack")
    public Optional<Product> getProductByCode(String code) {
        log.info("Fetching the product for code " + code);
        var product =
                restClient.get().uri("/api/products/{code}", code).retrieve().body(Product.class);
        return Optional.ofNullable(product);
    }

    Optional<Product> getProductByCodeFallBack(String code, Throwable t) {
        System.out.println("ProductServiceClient.getProductByCodeFallBack: code: " + code);
        return Optional.empty();
    }
}
