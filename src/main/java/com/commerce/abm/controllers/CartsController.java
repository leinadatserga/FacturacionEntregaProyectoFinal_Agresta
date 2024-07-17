package com.commerce.abm.controllers;

import com.commerce.abm.entities.Cart;
import com.commerce.abm.entities.Product;
import com.commerce.abm.services.CartsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/carts")
public class CartsController {
    @Autowired
    private CartsService cartsService;

    @PostMapping
    public ResponseEntity<Object> createCart(@RequestBody Cart cart) {
        try {
            Cart newCart = cartsService.newCart(cart);
            return new ResponseEntity<>(newCart, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<Cart>> getAllCarts() {
        List<Cart> carts = cartsService.readAllCarts();
        return new ResponseEntity<>(carts, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getCartById(@PathVariable Long id) {
        try {
            Optional<Cart> cart = cartsService.readCartById(id);
            if (cart.isPresent()) {
                return new ResponseEntity<>(cart.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Cart not found", HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{id}/product")
    public ResponseEntity<?> addProductToCart(@PathVariable Long id, @RequestBody List<Product> products) {
        try {
            Cart updatedCart = cartsService.addProductToCart(id, products);
            return new ResponseEntity<>(updatedCart, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{id}/products")
    public ResponseEntity<Object> addProductsToCart(@PathVariable Long id, @RequestBody List<Long> productIds) {
        try {
            Cart updatedCart = cartsService.addProductsToCart(id, productIds);
            return new ResponseEntity<>(updatedCart, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteCart(@PathVariable Long id) {
        try {
            cartsService.deleteCart(id);
            return new ResponseEntity<>("Cart deleted successfully", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
