package com.commerce.abm.controllers;

import com.commerce.abm.entities.Product;
import com.commerce.abm.services.ProductsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"id\":1,\"name\":\"Dome Camera\",\"price\":999.99,\"stock\":99}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<Product> newProduct(
            @Parameter(description = "Request body containing the details of the product to be created")
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Product to be created",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "Example 1", value = "{\"product\": \"Dome camera\", \"price\": 999.99, \"stock\": 99}"),
                            @ExampleObject(name = "Example 2", value = "{\"product\": \"DVR 8ch\", \"price\": 599.99, \"stock\": 39}")
                    })
            )
            Product dataProduct) {
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of products",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Product.class)
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved product", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public Optional<Product> readProductById(
            @Parameter(description = "ID of the product to be retrieved", required = true, example = "1")
            @PathVariable("pid") Long pid) {
        try {
            return service.readProductById(pid);
        } catch (Exception exception) {
            System.out.println(exception);
            throw new RuntimeException("Error in Product obtain");
        }
    }

    @PutMapping("/{pid}")
    @Operation(summary = "Update a Product", description = "Using the required Id and data, update a specific Product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<Product> updateProduct(
            @Parameter(description = "ID of the product to be updated", required = true, example = "1")
            @PathVariable("pid") Long id,
            @Parameter(description = "Request body containing the updated details of the product", required = true)
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Product to be updated, all data or especific field",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "Example 1", value = "{\"product\": \"Dome camera\", \"price\": 999.99, \"stock\": 99}")
                    })
            )
            Product dataProduct) {
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product deleted successfully", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public void deleteProduct(
            @Parameter(description = "ID of the product to be deleted")
            @PathVariable("pid") Long pid) {
        try {
            service.deleteProduct(pid);
        } catch (Exception exception) {
            System.out.println(exception);
            throw new RuntimeException("Error in Product deletion");
        }
    }
}
