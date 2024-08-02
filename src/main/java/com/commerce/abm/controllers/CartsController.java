package com.commerce.abm.controllers;

import com.commerce.abm.entities.Cart;
import com.commerce.abm.services.CartsCleanUpService;
import com.commerce.abm.services.CartsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Autowired
    private CartsCleanUpService cartsCleanUpService;

    @GetMapping
    @Operation(
            summary = "Return all Carts",
            tags = {"Carts paths definitions"},
            description = "Bring back a list of all Carts in JSON format",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of carts",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Cart.class),
                                    examples = @ExampleObject(
                                            value = """
                                                [
                                                  {
                                                    "cartId": 1,
                                                    "delivered": false,
                                                    "lastUpdated": "2024-07-31T15:18:21.528346",
                                                    "items": [
                                                      {
                                                        "id": 5001,
                                                        "quantity": 2,
                                                        "price": 542,
                                                        "product_id": 1003
                                                      }
                                                    ]
                                                  }
                                                ]"""
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Internal server error")))
            }
    )
    public ResponseEntity<List<Cart>> getAllCarts() {
            try {
                List<Cart> carts = cartsService.readAllCarts();
                return ResponseEntity.ok(carts);
            } catch (Exception e) {
                System.err.println("Error retrieving all Carts: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
    }

    @GetMapping("/{clid}")
    @Operation(
            summary = "Search a Cart by Client id",
            tags = {"Carts paths definitions"},
            description = "Using the Client Id, returns a specific Cart",
            parameters = @Parameter(name = "clid", description = "ID of the client whose cart is to be retrieved", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved cart",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Cart.class),
                                    examples = @ExampleObject(
                                            value = """
                                                [
                                                  {
                                                    "cartId": 1,
                                                    "delivered": false,
                                                    "lastUpdated": "2024-07-31T15:18:21.528346",
                                                    "items": [
                                                      {
                                                        "id": 5001,
                                                        "quantity": 2,
                                                        "price": 542,
                                                        "product_id": 1003
                                                      }
                                                    ]
                                                  }
                                                ]"""
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Cart not found", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Cart not found"))),
                    @ApiResponse(responseCode = "400", description = "Invalid Client id", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Invalid Client id"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Internal server error")))
            }
    )
    public ResponseEntity<Object> getProductsOfCartForClientId(
            @Parameter(description = "ID of the client whose cart is to be retrieved")
            @PathVariable Long clid) {
        try {
            Optional<Cart> cart = cartsService.readCartById(clid);
            if (cart.isPresent()) {
                Cart retrievedCart = cart.get();
                if (!retrievedCart.isDelivered()) {
                    return new ResponseEntity<>(retrievedCart, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Cart has already been delivered", HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>("Cart not found", HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/{clid}/{pid}/{q}")
    @Operation(
            summary = "Add a Product in a Cart",
            tags = {"Carts paths definitions"},
            description = "Add a specific product to a cart using client ID, product ID, and quantity.",
            parameters = {
                    @Parameter(name = "clid", description = "ID of the client whose cart is to be updated", required = true),
                    @Parameter(name = "pid", description = "ID of the product to be added to the cart", required = true),
                    @Parameter(name = "q", description = "Quantity of the product to be added", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product added to cart successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Cart.class),
                                    examples = @ExampleObject(
                                            value = """
                                                [
                                                  {
                                                    "cartId": 1,
                                                    "delivered": false,
                                                    "lastUpdated": "2024-07-31T15:18:21.528346",
                                                    "items": [
                                                      {
                                                        "id": 5001,
                                                        "quantity": 2,
                                                        "price": 542,
                                                        "product_id": 1003
                                                      }
                                                    ]
                                                  }
                                                ]"""
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Cart or Product not found", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Cart or Product not found"))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Invalid input data"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Internal server error")))
            }
    )
    public ResponseEntity<Object> addProductToCart(
            @Parameter(description = "ID of the client whose cart is to be updated")
            @PathVariable("clid") Long clid,
            @Parameter(description = "ID of the product to be added to the cart")
            @PathVariable("pid") Long pid,
            @Parameter(description = "Quantity of the product to be added")
            @PathVariable("q") int q) {
        try {
            if (clid == null || clid <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid client ID");
            }
            if (pid == null || pid <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid product ID");
            }
            if (q <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Quantity must be greater than 0");
            }
            Cart updatedCart = cartsService.addProductToCart(clid, pid, q);
            return new ResponseEntity<>(updatedCart, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error adding product to cart: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @DeleteMapping("/{cid}")
    @Operation(
            summary = "Remove a Product from a Cart",
            tags = {"Carts paths definitions"},
            description = "Remove a specific product from the cart using product ID",
            parameters = @Parameter(name = "cid", description = "ID of the cart to remove the product from", required = true),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"productId\": 5001\n" +
                                            "}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product removed from Cart successfully", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Product removed from Cart successfully"))),
                    @ApiResponse(responseCode = "400", description = "Product ID is required or invalid", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Product ID is required or invalid"))),
                    @ApiResponse(responseCode = "404", description = "Cart or Product not found", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Cart or Product not found"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Internal server error")))
            }
    )
    public ResponseEntity<String> removeProductFromCart(
            @Parameter(description = "ID of the cart to remove the product from")
            @PathVariable Long cid,
            @Parameter(description = "Request body containing the Product ID to be removed")
            @RequestBody Map<String, Long> requestProd) {
        Long productId = requestProd.get("productId");
        if (cid == null || cid <= 0) {
            return ResponseEntity.badRequest().body("Invalid Cart ID");
        }
        if (productId == null || productId <= 0) {
            return ResponseEntity.badRequest().body("Product ID is required and must be greater than 0");
        }
        try {
            cartsService.removeProductFromCart(cid, productId);
            return ResponseEntity.ok("Product removed from Cart successfully");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error removing product from cart: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @DeleteMapping("/{cid}/drop")
    @Operation(
            summary = "Remove a Cart",
            tags = {"Carts paths definitions"},
            description = "Using the required Id, delete a specific Cart",
            parameters = @Parameter(name = "cid", description = "ID of the cart to be deleted", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cart deleted successfully", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Cart deleted successfully"))),
                    @ApiResponse(responseCode = "404", description = "Cart not found", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Cart not found"))),
                    @ApiResponse(responseCode = "400", description = "Invalid Cart id", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Invalid Cart id"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Internal server error")))
            }
    )
    public ResponseEntity<Object> deleteCart(
            @Parameter(description = "ID of the cart to be deleted")
            @PathVariable Long cid) {
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
        } catch (Exception e) {
            System.err.println("Error removing cart: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @DeleteMapping("/cleanup")
    @Operation(
            summary = "Remove inactive carts",
            tags = {"Carts paths definitions"},
            description = "Manually trigger the removal of inactive carts",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Inactive Carts removed successfully", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Inactive Carts removed successfully"))),
                    @ApiResponse(responseCode = "404", description = "No inactive Carts to remove", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "No inactive Carts to remove"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Internal server error")))
            }
    )
    public ResponseEntity<String> cleanupCarts() {
        try {
            boolean cartsRemoved = cartsCleanUpService.removeInactiveCarts();
            if (cartsRemoved) {
                return ResponseEntity.ok("Inactive Carts removed successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No inactive Carts to remove");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error removing inactive carts: " + e.getMessage());
        }
    }
}
