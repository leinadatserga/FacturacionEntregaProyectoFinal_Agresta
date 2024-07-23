package com.commerce.abm.services;

import com.commerce.abm.entities.Cart;
import com.commerce.abm.entities.Product;
import com.commerce.abm.repositories.CartsRepository;
import com.commerce.abm.repositories.ProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CartsService {
    @Autowired
    private CartsRepository repository;

    @Autowired
    private ProductsRepository productRepository;

    @Transactional
    public Cart newCart(Cart cart) {
        if (cart.getClient() == null) {
            throw new IllegalArgumentException("Cart must been associated to a Client");
        }
        return repository.save(cart);
    }

    public List<Cart> readAllCarts() {
        return repository.findAll();
    }

    public Optional<Cart> readCartById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Cart id cannot be null");
        }
        return repository.findById(id);
    }

    @Transactional
    public Cart addProductToCart(Long clientId, Long productId, int quantity) {
        Optional<Cart> cartOpt = repository.findById(clientId);
        if (!cartOpt.isPresent()) {
            throw new IllegalArgumentException("Cart not found for client ID: " + clientId);
        }
        Cart cart = cartOpt.get();
        Optional<Product> productOpt = productRepository.findById(productId);
        if (!productOpt.isPresent()) {
            throw new IllegalArgumentException("Product not found for product ID: " + productId);
        }
        Product product = productOpt.get();
        cart.setProduct(product);
        cart.setAmount(quantity);
        cart.setPrice(product.getPrice() * quantity);
        return repository.save(cart);
    }

    public void deleteCart(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Cart id cannot be null");
        }
        repository.deleteById(id);
    }
}
