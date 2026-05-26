package com.example.warehouse;

import java.util.Optional;

public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public int updateStock(String productId, int quantityChange) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            throw new IllegalArgumentException("Product not found with ID: " + productId);
        }

        Product product = productOpt.get();
        int newStock = product.getStockQuantity() + quantityChange;

        if (newStock < 0) {
            throw new IllegalArgumentException("Resulting stock would be negative");
        }

        product.setStockQuantity(newStock);
        productRepository.save(product);

        return newStock;
    }

    public static class Product {
        private final String id;
        private int stockQuantity;

        public Product(String id, int stockQuantity) {
            this.id = id;
            this.stockQuantity = stockQuantity;
        }

        public String getId() {
            return id;
        }

        public int getStockQuantity() {
            return stockQuantity;
        }

        public void setStockQuantity(int stockQuantity) {
            this.stockQuantity = stockQuantity;
        }
    }

    public interface ProductRepository {
        Optional<Product> findById(String id);

        Product save(Product product);
    }
}
