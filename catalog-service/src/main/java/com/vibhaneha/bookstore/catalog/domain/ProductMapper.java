package com.vibhaneha.bookstore.catalog.domain;

class ProductMapper {

    static Product toProduct(ProductEntity productEntity) {
        return new Product(
                productEntity.getCode(),
                productEntity.getName(),
                productEntity.getDescription(),
                productEntity.getImageURL(),
                productEntity.getPrice());
    }
}
