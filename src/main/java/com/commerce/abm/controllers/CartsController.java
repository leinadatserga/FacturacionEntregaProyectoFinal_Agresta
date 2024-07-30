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
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Return all Carts", description = "Bring back a list of all Carts in JSON format")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of carts",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Cart.class),
                            examples = @ExampleObject(
                                    value = "[\n" +
                                            "  {\n" +
                                            "    \"id\": 1,\n" +
                                            "    \"clientId\": 1001,\n" +
                                            "    \"products\": [\n" +
                                            "      {\n" +
                                            "        \"productId\": 5001,\n" +
                                            "        \"quantity\": 2\n" +
                                            "      }\n" +
                                            "    ]\n" +
                                            "  }\n" +
                                            "]"
                            )
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<List<Cart>> getAllCarts() {
        List<Cart> carts = cartsService.readAllCarts();
        return new ResponseEntity<>(carts, HttpStatus.OK);
    }

    @GetMapping("/{clid}")
    @Operation(
            summary = "Search a Client Cart",
            description = "Using the Client Id, returns a specific Cart",
            parameters = @Parameter(name = "clid", description = "ID of the client whose cart is to be retrieved", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved cart",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Cart.class),
                                    examples = @ExampleObject(
                                            value = "{\n" +
                                                    "  \"id\": 1,\n" +
                                                    "  \"clientId\": 1001,\n" +
                                                    "  \"products\": [\n" +
                                                    "    {\n" +
                                                    "      \"productId\": 5001,\n" +
                                                    "      \"quantity\": 2\n" +
                                                    "    }\n" +
                                                    "  ]\n" +
                                                    "}"
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Cart not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid client ID")
            }
    )
    public ResponseEntity<Object> getProductsOfCartForClientId(
            @Parameter(description = "ID of the client whose cart is to be retrieved")
            @PathVariable Long clid) {
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
    @Operation(
            summary = "Add a Product in a Cart",
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
                                            value = "{\n" +
                                                    "  \"id\": 1,\n" +
                                                    "  \"clientId\": 1001,\n" +
                                                    "  \"products\": [\n" +
                                                    "    {\n" +
                                                    "      \"productId\": 5001,\n" +
                                                    "      \"quantity\": 5\n" +
                                                    "    }\n" +
                                                    "  ]\n" +
                                                    "}"
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Cart or product not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data")
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
            Cart updatedCart = cartsService.addProductToCart(clid, pid, q);
            return new ResponseEntity<>(updatedCart, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{cid}/product")
    @Operation(
            summary = "Remove a Product from a Cart",
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
                    @ApiResponse(responseCode = "200", description = "Product removed from cart successfully"),
                    @ApiResponse(responseCode = "400", description = "Product ID is required or invalid"),
                    @ApiResponse(responseCode = "404", description = "Cart or product not found")
            }
    )
    public ResponseEntity<String> removeProductFromCart(
            @Parameter(description = "ID of the cart to remove the product from")
            @PathVariable Long cid,
            @Parameter(description = "Request body containing the product ID to be removed")
            @RequestBody Map<String, Long> request) {
        Long productId = request.get("productId");
        if (productId == null) {
            return ResponseEntity.badRequest().body("Product ID is required");
        }
        try {
            cartsService.removeProductFromCart(cid, productId);
            return ResponseEntity.ok("Product removed from cart");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{cid}")
    @Operation(
            summary = "Remove a Cart",
            description = "Using the required Id, delete a specific Cart",
            parameters = @Parameter(name = "cid", description = "ID of the cart to be deleted", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cart deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Cart not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid cart ID")
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
        }
    }

    @DeleteMapping("/cleanup")
    @Operation(
            summary = "Remove inactive carts",
            description = "Manually trigger the removal of inactive carts",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Inactive carts removed successfully"),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    public ResponseEntity<String> cleanupCarts() {
        try {
            boolean cartsRemoved = cartsCleanUpService.removeInactiveCarts();
            if (cartsRemoved) {
                return ResponseEntity.ok("Inactive carts removed successfully");
            } else {
                return ResponseEntity.ok("No inactive Carts to remove");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error removing inactive carts: " + e.getMessage());
        }
    }
}
