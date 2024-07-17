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

    public Optional<Cart> cartById(Long id) {
        return repository.findById(id);
    }

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
    public Cart addProductToCart(Long cartId, List<Product> products) {
        Optional<Cart> cartOpt = repository.findById(cartId);
        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            for (Product product : products) {
                product.getCarts().add(cart);
                cart.getProducts().add(product);
            }
            cart.setAmount(cart.getProducts().size());
            cart.setPrice(cart.getProducts().stream().mapToDouble(Product::getPrice).sum());
            return repository.save(cart);
        } else {
            throw new IllegalArgumentException("Cart not founded");
        }
    }

    @Transactional
    public Cart addProductsToCart(Long cartId, List<Long> productIds) {
        Optional<Cart> cartOpt = repository.findById(cartId);
        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            for (Long productId : productIds) {
                Optional<Product> productOpt = productRepository.findById(productId);
                if (productOpt.isPresent()) {
                    Product product = productOpt.get();
                    if (!cart.getProducts().contains(product)) {
                        cart.getProducts().add(product);
                        product.getCarts().add(cart);
                    }
                } else {
                    throw new IllegalArgumentException("Product ID " + productId + ", not founded");
                }
            }
            cart.setAmount(cart.getProducts().size());
            cart.setPrice(cart.getProducts().stream().mapToDouble(Product::getPrice).sum());
            return repository.save(cart);
        } else {
            throw new IllegalArgumentException("Cart not founded");
        }
    }

    public void deleteCart(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Cart id cannot be null");
        }
        repository.deleteById(id);
    }
}
