package com.commerce.abm.controllers;

import com.commerce.abm.entities.Cart;
import com.commerce.abm.services.CartsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/carts")
@Tag(name = "Carts paths definitions", description = "CRUD of Cart class controller")
public class CartsController {
    @Autowired
    private CartsService cartsService;

    @PostMapping
    @Operation(summary = "Add a Cart", description = "Using the required data, create a new Cart")
    public ResponseEntity<Cart> createCart(@RequestBody Cart cart) {
        try {
            Cart newCart = cartsService.newCart(cart);
            return new ResponseEntity<>(newCart, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    @Operation(summary = "Return all Carts", description = "Bring back a list of all Carts in JSON format")
    public ResponseEntity<List<Cart>> getAllCarts() {
        List<Cart> carts = cartsService.readAllCarts();
        return new ResponseEntity<>(carts, HttpStatus.OK);
    }

    @GetMapping("/{clid}")
    @Operation(summary = "Search a Client Cart", description = "Using the Client Id, returns a specific Cart")
    public ResponseEntity<Object> getProductsOfCartForClientId(@PathVariable Long clid) {
        try {
            Optional<Cart> cart = cartsService.readCartById(clid);
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
    public ResponseEntity<Object> addProductToCart(
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

    @DeleteMapping("/{cid}/product")
    public ResponseEntity<String> removeProductFromCart(@PathVariable Long cid, @RequestBody Map<String, Long> request) {
        Long productId = request.get("productId");
        if (productId == null) {
            return ResponseEntity.badRequest().body("Product ID is required");
        }

        try {
            System.out.println(cid);
            System.out.println(productId);
            cartsService.removeProductFromCart(cid, productId);
            return ResponseEntity.ok("Product removed from cart");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{cid}")
    @Operation(summary = "Remove a Cart", description = "Using the required Id, delete a specific Cart")
    public ResponseEntity<Object> deleteCart(@PathVariable Long cid) {
        try {
            Optional<Cart> cart = cartsService.readCartById(cid);
            if (cart.isPresent()) {
                cartsService.deleteCart(cid);
                return new ResponseEntity<>("Cart deleted successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Cart not found", HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
