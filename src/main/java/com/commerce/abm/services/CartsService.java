package com.commerce.abm.services;

import com.commerce.abm.entities.Cart;
import com.commerce.abm.entities.CartItem;
import com.commerce.abm.entities.Client;
import com.commerce.abm.entities.Product;
import com.commerce.abm.repositories.CartsRepository;
import com.commerce.abm.repositories.ClientsRepository;
import com.commerce.abm.repositories.ProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartsService {
    @Autowired
    private CartsRepository repository;

    @Autowired
    private ProductsRepository productsRepository;

    @Autowired
    private ClientsRepository clientsRepository;

    @Transactional
    public Cart newCart(Cart cart) {
        Optional<Client> clientOpt = clientsRepository.findById(cart.getClient().getId());
        if (clientOpt.isEmpty()) {
            throw new IllegalArgumentException("Client not found");
        }
        Client client = clientOpt.get();
        Optional<Cart> existingCart = repository.findByClient(client);
        if (existingCart.isPresent()) {
            throw new IllegalArgumentException("Client already has an active cart");
        }
        cart.setCartId(client.getId());
        cart.setDelivered(false);
        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
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
        Optional<Client> clientOpt = clientsRepository.findById(clientId);
        if (clientOpt.isEmpty()) {
            throw new IllegalArgumentException("Client not found for client ID: " + clientId);
        }
        Client client = clientOpt.get();

        Optional<Cart> cartOpt = repository.findById(clientId);
        Cart cart;
        if (cartOpt.isPresent()) {
            cart = cartOpt.get();
        } else {
            cart = new Cart();
            cart.setClient(client);
            cart.setDelivered(false);
            cart.setCartId(client.getId());
            cart.setItems(new ArrayList<>());
            cart = repository.save(cart);
        }

        Optional<Product> productOpt = productsRepository.findById(productId);
        if (productOpt.isEmpty()) {
            throw new IllegalArgumentException("Product not found for product ID: " + productId);
        }
        Product product = productOpt.get();

        Optional<CartItem> cartItemOpt = cart.getItems().stream()
                .filter(item -> item.getProduct().equals(product))
                .findFirst();
        CartItem cartItem;
        if (cartItemOpt.isPresent()) {
            cartItem = cartItemOpt.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setPrice(cartItem.getPrice() + (product.getPrice() * quantity));
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setPrice(product.getPrice() * quantity);
            cart.getItems().add(cartItem);
        }
        return repository.save(cart);
    }

    public void deleteCart(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Cart id, cannot be null");
        }
        repository.deleteById(id);
    }
}
