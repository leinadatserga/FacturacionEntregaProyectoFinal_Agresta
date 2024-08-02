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
    @Operation(
            summary = "Add a Product",
            tags = {"Products paths definitions"},
            description = "Using the required data, create a new Product",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Product.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Example 1:",
                                            value = """
                                                    {
                                                    "product": "Dome Camera",
                                                    "price": 999.99,
                                                    "stock": 99
                                                    }"""
                                    ),
                                    @ExampleObject(
                                            name = "Example 2:",
                                            value = """
                                                    {
                                                    "product": "DVR 8ch",
                                                    "price": 599.99,
                                                    "stock": 39
                                                    }""")
                            }
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Invalid input data"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<?> newProduct(
            @Parameter(description = "Request body containing the details of the product to be created")
            @RequestBody Product dataProduct) {
        try {
            if (dataProduct.getProduct() == null || dataProduct.getProduct().trim().isEmpty()) {
                return new ResponseEntity<>("Invalid input data: product cannot be empty", HttpStatus.BAD_REQUEST);
            }
            if (dataProduct.getPrice() == null || dataProduct.getPrice() <= 0) {
                return new ResponseEntity<>("Invalid input data: price must be greater than 0", HttpStatus.BAD_REQUEST);
            }
            if (dataProduct.getStock() == null || dataProduct.getStock() < 0) {
                return new ResponseEntity<>("Invalid input data: stock cannot be negative", HttpStatus.BAD_REQUEST);
            }
            Product product = service.newProduct(dataProduct);
            return new ResponseEntity<>(product, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid input data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.err.println("Error in Product creation: " + e.getMessage());
            return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping()
    @Operation(
            summary = "Return all Products",
            tags = {"Products paths definitions"},
            description = "Bring back a list of all Products in JSON format",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of products",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Product.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                    "id": 1,
                                                    "product": "DVR 8ch",
                                                    "price": 599.99,
                                                    "stock": 39
                                                    }"""
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Products not found", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Products not found"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Internal server error")))
            }
    )
    public ResponseEntity<List<Product>> readAllProducts() {
        try {
            List<Product> products = service.readAllProducts();
            if (products.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception exception) {
            System.err.println("Error retrieving products: " + exception.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{pid}")
    @Operation(
            summary = "Search a Product",
            tags = {"Products paths definitions"},
            description = "Using the required Id, returns a specific Product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved product", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Invalid input data"))),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Product not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<?> readProductById(
            @Parameter(description = "ID of the product to be retrieved", required = true, example = "1")
            @PathVariable("pid") Long pid) {
        try {
            if (pid == null || pid <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("""
                        {"message": "Invalid product ID"}""");
            }
            Optional<Product> product = service.readProductById(pid);
            if (product.isPresent()) {
                return ResponseEntity.ok(product.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("""
                        {"message": "Product not found"}""");
            }
        } catch (Exception exception) {
            System.err.println("Error retrieving product: " + exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("""
                    {"message": "Internal server error"}""");
        }
    }

    @PutMapping("/{pid}")
    @Operation(summary = "Update a Product", tags = {"Products paths definitions"}, description = "Using the required Id and data, update a specific Product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Invalid input data"))),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Product not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<?> updateProduct(
            @Parameter(description = "ID of the product to be updated", required = true, example = "1")
            @PathVariable("pid") Long pid,
            @Parameter(description = "Request body containing the updated details of the product", required = true)
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Product to be updated, all data or especific field",
                    content = @Content(
                            mediaType = "application/json",
                            examples = { @ExampleObject(
                                    name = "Example 1",
                                            value = """
                                                    {
                                                    "product": "Dome camera",
                                                    "price": 999.99,
                                                    "stock": 99
                                                    }""")
                    })
            )
            Product dataProduct) {
        try {
            if (pid == null || pid <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"Invalid product ID\"}");
            }
            Optional<Product> existingProductOpt = service.readProductById(pid);
            if (existingProductOpt.isPresent()) {
                Product existingProduct = existingProductOpt.get();
                if (dataProduct.getProduct() != null) {
                    existingProduct.setProduct(dataProduct.getProduct());
                }
                if (dataProduct.getPrice() != null) {
                    existingProduct.setPrice(dataProduct.getPrice());
                }
                if (dataProduct.getStock() != null) {
                    existingProduct.setStock(dataProduct.getStock());
                }
                Product updatedProduct = service.updateProduct(pid, dataProduct);
                return ResponseEntity.ok(updatedProduct);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\": \"Product not found\"}");
            }
        } catch (Exception exception) {
            System.err.println("Error updating product: " + exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Internal server error\"}");
        }
    }

    @DeleteMapping("/{pid}")
    @Operation(summary = "Remove a Product", tags = {"Products paths definitions"}, description = "Using the required Id, delete a specific Product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product deleted successfully", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Product deleted successfully"))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Invalid input data"))),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Product not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<?> deleteProduct(
            @Parameter(description = "ID of the product to be deleted")
            @PathVariable("pid") Long pid) {
        try {
            if (pid == null || pid <= 0) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Invalid product ID");
            }
            Optional<Product> existingProduct = service.readProductById(pid);
            if (existingProduct.isPresent()) {
                service.deleteProduct(pid);
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body("Product deleted successfully");
            } else {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body("Product not found");
            }
        } catch (Exception exception) {
            System.err.println("Error deleting product: " + exception.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error");
        }
    }
}
