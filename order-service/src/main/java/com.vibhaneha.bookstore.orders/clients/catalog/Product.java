package com.vibhaneha.bookstore.orders.clients.catalog;

import java.math.BigDecimal;

public record Product(String code, String name, String description, String imageURL, BigDecimal price) {}
