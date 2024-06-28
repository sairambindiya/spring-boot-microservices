package com.vibhaneha.bookstore.catalog.domain;

import java.math.BigDecimal;

public record Product(String code, String name, String description, String imageURL, BigDecimal price) {}
