package com.vibhaneha.bookstore.catalog.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest(
        properties = {
            "spring.test.database.replace=none",
            "spring.datasource.url=jdbc:tc:postgresql:16-alpine:///db",
        })

// @Import(ContainersConfig.class)
@Sql("/test-data.sql")
class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Test
    void shouldGetAllProducts() {
        List<ProductEntity> productEntityList = productRepository.findAll();
        assertThat(productEntityList).hasSize(15);
    }

    @Test
    void shouldGetProductByCode() {
        ProductEntity product = productRepository.findByCode("P100").orElseThrow();
        assertThat(product.getCode().equals("P100"));
        assertThat(product.getName().equals("The Hunger Games"));
        assertThat(product.getDescription().equals("Winning will make you famous. Losing means certain death..."));
        assertThat(product.getImageURL().equals("https://images.gr-assets.com/books/1447303603l/2767052.jpg"));
        assertThat(product.getPrice().equals(new BigDecimal("34.0")));
    }

    @Test
    void shouldReturnEmptyWhenProductCodeNotExists() {
        assertThat(productRepository.findByCode("invalid_product_code")).isEmpty();
    }
}
