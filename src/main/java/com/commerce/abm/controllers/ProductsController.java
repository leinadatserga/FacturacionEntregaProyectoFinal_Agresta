package com.commerce.abm.controllers;

import com.commerce.abm.entities.Product;
import com.commerce.abm.services.ProductsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "api/v1/products")
@Tag(name = "Products paths definitions", description = "CRUD of Product class controller")
public class ProductsController {
    @Autowired
    private ProductsService service;

    @PostMapping()
    @Operation(summary = "Add a Product", description = "Using the required data, create a new Product")
    public ResponseEntity<Product> newProduct(@RequestBody Product dataProduct) {
        try {
            Product product = service.newProduct(dataProduct);
            return new ResponseEntity<>(product, HttpStatus.CREATED);
        } catch (Exception exception) {
            System.out.println(exception);
            throw new RuntimeException("Error in Product creation");
        }
    }

    @GetMapping()
    @Operation(summary = "Return all Products", description = "Bring back a list of all Products in JSON format")
    public List<Product> readAllProducts() {
        try {
            return service.readAllProducts();
        } catch (Exception exception) {
            System.out.println(exception);
            throw new RuntimeException("Error in Products list");
        }
    }

    @GetMapping("/{pid}")
    @Operation(summary = "Search a Product", description = "Using the required Id, returns a specific Product")
    public Optional<Product> readProductById(@PathVariable("id") Long id) {
        try {
            return service.readProductById(id);
        } catch (Exception exception) {
            System.out.println(exception);
            throw new RuntimeException("Error in Product obtain");
        }
    }

    @PutMapping("/{pid}")
    @Operation(summary = "Update a Product", description = "Using the required Id and data, update a specific Product")
    public ResponseEntity<Product> updateProduct(@PathVariable("pid") Long id, @RequestBody Product dataProduct) {
        try {
            Optional<Product> existingProduct = service.readProductById(id);
            if (existingProduct.isPresent()) {
                Product updatedProduct = service.updateProduct(id, dataProduct);
                return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception exception) {
            System.out.println(exception);
            throw new RuntimeException("Error in Product update");
        }
    }

    @DeleteMapping("/{pid}")
    @Operation(summary = "Remove a Product", description = "Using the required Id, delete a specific Product")
    public void deleteProduct(@PathVariable("pid") Long id) {
        try {
            service.deleteProduct(id);
        } catch (Exception exception) {
            System.out.println(exception);
            throw new RuntimeException("Error in Product deletion");
        }
    }
}
