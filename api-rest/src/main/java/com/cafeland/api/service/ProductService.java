package com.cafeland.api.service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import com.cafeland.api.model.*;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductService {

    private List<ProductResponse> repository = new ArrayList<>();

    public List<ProductResponse> listProducts(String search) {
        return repository;
    }

    public ProductResponse createProduct(Product product) {
        ProductResponse response = new ProductResponse();
        response.setName(product.getName());
        response.setPrice(product.getPrice());
        response.setDescription(product.getDescription());
        
        if (product.getCategory() != null) {
            response.setCategory(ProductResponse.CategoryEnum.fromValue(product.getCategory().toString()));
        }

        long timestamp = System.currentTimeMillis();
        String numericId = String.valueOf(timestamp).substring(String.valueOf(timestamp).length() - 5);
        response.setId("prod-" + numericId);

        response.setSku(String.valueOf((int)(Math.random() * 900) + 100)); 
        response.setCreatedAt(OffsetDateTime.now());
        response.setInStock(true);
        
        repository.add(response);
        return response;
    }

    public ProductResponse getProductById(String productId) {
        return repository.stream()
                .filter(p -> p.getId().equals(productId))
                .findFirst()
                .orElse(null);
    }

    public ProductResponse updateProduct(String productId, Product product) {
        ProductResponse existing = getProductById(productId);
        if (existing != null) {
            existing.setName(product.getName());
            existing.setPrice(product.getPrice());
            existing.setDescription(product.getDescription());
            if (product.getCategory() != null) {
                existing.setCategory(ProductResponse.CategoryEnum.fromValue(product.getCategory().toString()));
            }
        }
        return existing;
    }

    public boolean deleteProduct(String productId) {
        return repository.removeIf(p -> p.getId().equals(productId));
    }

    public Recipe getRecipe(String productId) {
        Recipe recipe = new Recipe();
        recipe.setProductId(productId);
        return recipe;
    }
}