package com.commerce.abm.services;

import com.commerce.abm.entities.Product;
import com.commerce.abm.repositories.CartsRepository;
import com.commerce.abm.repositories.ProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductsService {
    @Autowired
    private ProductsRepository repository;
    private CartsRepository cartsRepository;

    public Product newProduct(Product product) {
        repository.save(product);
        return product;
    }

    public List<Product> readAllProducts() {
        return repository.findAll();
    }

    public Optional<Product> readProductById(Long id) {
        return repository.findById(id);
    }

    public Product updateProduct(Long id, Product dataProduct) {
        Optional<Product> existingProduct = repository.findById(id);
        if (existingProduct.isPresent()) {
            Product product = existingProduct.get();
            if (dataProduct.getProduct() != null) product.setProduct(dataProduct.getProduct());
            if (dataProduct.getPrice() != null) product.setPrice(dataProduct.getPrice());
            if (dataProduct.getStock() != null) product.setStock(dataProduct.getStock());
            return repository.save(product);
        } else {
            throw new RuntimeException("Product not found");
        }
    }

    public void deleteProduct(Long id) {
        repository.deleteById(id);
    }
}
