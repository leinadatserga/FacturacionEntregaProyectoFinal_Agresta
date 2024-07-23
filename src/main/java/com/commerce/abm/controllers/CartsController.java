package com.commerce.abm.controllers;

import com.commerce.abm.entities.Cart;
import com.commerce.abm.entities.Product;
import com.commerce.abm.services.CartsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/carts")
@Tag(name = "Carts paths definitions", description = "CRUD of Cart class controller")
public class CartsController {
    @Autowired
    private CartsService cartsService;

    @PostMapping
    @Operation(summary = "Add a Cart", description = "Using the required data, create a new Cart")
    public ResponseEntity<Object> createCart(@RequestBody Cart cart) {
        try {
            Cart newCart = cartsService.newCart(cart);
            return new ResponseEntity<>(newCart, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    @Operation(summary = "Return all Carts", description = "Bring back a list of all Carts in JSON format")
    public ResponseEntity<List<Cart>> getAllCarts() {
        List<Cart> carts = cartsService.readAllCarts();
        return new ResponseEntity<>(carts, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Search a Cart", description = "Using the required Id, returns a specific Cart")
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

    @PostMapping(value = "/{clid}/{pid}/{q}")
    @Operation(summary = "Add a Product in a Cart", description = "Using the required Id, allows the user to add items by entering Product Id")
    public ResponseEntity<?> addProductToCart(
            @PathVariable("clid") Long clid,
            @PathVariable("pid") Long pid,
            @PathVariable("q") int q) {
        try {
            Cart updatedCart = cartsService.addProductToCart(clid, pid, q);
            return new ResponseEntity<>(updatedCart, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove a Cart", description = "Using the required Id, delete a specific Cart")
    public ResponseEntity<Object> deleteCart(@PathVariable Long id) {
        try {
            cartsService.deleteCart(id);
            return new ResponseEntity<>("Cart deleted successfully", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
