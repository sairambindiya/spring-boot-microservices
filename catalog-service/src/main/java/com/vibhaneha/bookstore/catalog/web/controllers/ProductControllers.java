package com.vibhaneha.bookstore.catalog.web.controllers;

import com.vibhaneha.bookstore.catalog.domain.PagedResult;
import com.vibhaneha.bookstore.catalog.domain.Product;
import com.vibhaneha.bookstore.catalog.domain.ProductNotFoundException;
import com.vibhaneha.bookstore.catalog.domain.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
class ProductControllers {

    private final ProductService productService;

    ProductControllers(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    PagedResult<Product> getProducts(@RequestParam(name = "page", defaultValue = "1") int pageNumber) {
        return productService.getProducts(pageNumber);
    }

    @GetMapping("/{code}")
    ResponseEntity<Product> getProductByCode(@PathVariable String code) {
        return productService
                .getProductByCode(code)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> ProductNotFoundException.forCode("Product with code " + code + " not found"));
    }
}
