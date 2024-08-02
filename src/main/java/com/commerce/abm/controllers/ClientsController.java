package com.commerce.abm.controllers;

import com.commerce.abm.entities.Cart;
import com.commerce.abm.entities.Client;
import com.commerce.abm.repositories.CartsRepository;
import com.commerce.abm.repositories.ClientsRepository;
import com.commerce.abm.services.ClientsService;
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
@RequestMapping(path = "api/v1")
@Tag(name = "Clients paths definitions", description = "CRUD of Client class controller")
public class ClientsController {
    @Autowired
    private ClientsService service;

    @Autowired
    private ClientsRepository clientsRepository;

    @Autowired
    private CartsRepository cartsRepository;

    @PostMapping(path = "/auth/register", consumes = "application/json", produces = "application/json")
    @Operation(
            summary = "Add a Client",
            tags = {"Clients paths definitions"},
            description = "Using the required data, create a new Client",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Client.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "name": "Daniel",
                                              "lastname": "Agresta",
                                              "docnumber": 12345678,
                                              "age": 31
                                            }"""
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Client created successfully", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Client created successfully"))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Invalid input data"))),
            @ApiResponse(responseCode = "404", description = "Client not found", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Client not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<?> create(
            @Parameter(description = "Client data to create a new client")
            @RequestBody Client dataClient) {
        try {
            Client client = service.newClient(dataClient);
            return new ResponseEntity<>(client, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid input data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Client not found: " + e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("Error in Client creation: " + e.getMessage());
            return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "/{clid}/new-cart")
    @Operation(
            summary = "Create a New Cart for a Client",
            tags = {"Clients paths definitions"},
            description = "Creates a new Cart for the specified Client after deleting the old one",
            parameters = @Parameter(name = "clid", description = "ID of the client to create a new cart for", required = true)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cart created successfully", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Cart created successfully"))),
            @ApiResponse(responseCode = "404", description = "Client not found", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Client not found"))),
            @ApiResponse(responseCode = "400", description = "Client already has an active cart or Invalid client ID", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Client already has an active cart or Invalid client ID"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Internal server error")))
    })
    public ResponseEntity<Object> createNewCartForClient(
            @Parameter(description = "ID of the client to create a new cart for")
            @PathVariable Long clid) {
        if (clid == null || clid <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            Optional<Client> clientOpt = clientsRepository.findById(clid);
            if (clientOpt.isEmpty()) {
                return new ResponseEntity<>("Client not found", HttpStatus.NOT_FOUND);
            }

            Client client = clientOpt.get();
            Optional<Cart> existingCart = cartsRepository.findByClient(client);
            if (existingCart.isPresent()) {
                return new ResponseEntity<>("Client already has an active cart", HttpStatus.BAD_REQUEST);
            }
            Cart newCart = new Cart();
            newCart.setCartId(client.getId());
            newCart.setClient(client);
            newCart.setDelivered(false);

            Cart savedCart = cartsRepository.save(newCart);
            return new ResponseEntity<>("Cart created successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/auth/clients")
    @Operation(
            summary = "Return all Clients",
            tags = {"Clients paths definitions"},
            description = "Bring back a list of all Clients in JSON format",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of clients",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Client.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "id": 1,
                                                      "name": "Daniel",
                                                      "lastname": "Agresta",
                                                      "docnumber": 12345678,
                                                      "age": 31,
                                                      "carts": [
                                                        {
                                                          "cartId": 3,
                                                          "delivered": false,
                                                          "lastUpdated": "2024-07-31T15:10:36.444005",
                                                          "items": []
                                                        }
                                                      ],
                                                      "invoices": []
                                                    }"""
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Internal server error")))
            }
    )
    public ResponseEntity <List<Client>> readAllClients() {
        try {
            List<Client> clients = service.readAllClients();
            return ResponseEntity.ok(clients);
        } catch (Exception exception) {
            System.err.println("Error retrieving all clients: " + exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(path = "/clients/{clid}")
    @Operation(
            summary = "Search a Client",
            tags = {"Clients paths definitions"},
            description = "Using the required Id, returns a specific Client",
            parameters = @Parameter(name = "clid", description = "ID of the client to be searched", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved client",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Client.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "id": 1,
                                                      "name": "Daniel",
                                                      "lastname": "Agresta",
                                                      "docnumber": 12345678,
                                                      "age": 31,
                                                      "carts": [
                                                        {
                                                          "cartId": 3,
                                                          "delivered": false,
                                                          "lastUpdated": "2024-07-31T15:10:36.444005",
                                                          "items": []
                                                        }
                                                      ],
                                                      "invoices": []
                                                    }"""
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Client not found", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Client not found"))),
                    @ApiResponse(responseCode = "400", description = "Invalid client ID", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Invalid client ID"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Internal server error")))
            }
    )
    public ResponseEntity<Optional<Client>> readClientsById(
            @Parameter(description = "ID of the client to be searched")
            @PathVariable("clid") Long clid) {
        try {
            if (clid == null || clid <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Optional.empty());
            }
            Optional<Client> client = service.readClientById(clid);
            if (client.isPresent()) {
                return ResponseEntity.ok(client);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Optional.empty());
            }
        } catch (Exception exception) {
            System.err.println("Error retrieving client by id: " + exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Optional.empty());
        }
    }

    @PutMapping("/auth/me")
    @Operation(
            summary = "Update a Client",
            tags = {"Clients paths definitions"},
            description = "Update a client's details using the provided client ID and data",
            parameters = @Parameter(name = "clid", description = "ID of the client to be updated", required = true),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Client.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "name": "Mauricio",
                                              "lastname": "Saravia",
                                              "docnumber": 87654321,
                                              "age": 31
                                            }"""
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Client updated successfully", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Client updated successfully"))),
                    @ApiResponse(responseCode = "404", description = "Client not found", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Client not found"))),
                    @ApiResponse(responseCode = "400", description = "Invalid client ID or data", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Invalid client ID or data"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Internal server error")))
            }
    )
    public ResponseEntity<Client> updateClient(
            @Parameter(description = "Client data to update the existing client")
            @RequestBody Map<String, Object> clientDetails) {
        try {
            Long clid = ((Number) clientDetails.get("clid")).longValue();
            if (clid == null || clid <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            Optional<Client> clientOptional = clientsRepository.findById(clid);
            if (clientOptional.isPresent()) {
                Client existingClient = clientOptional.get();
                if (clientDetails.containsKey("name")) {
                    existingClient.setName((String) clientDetails.get("name"));
                }
                if (clientDetails.containsKey("lastname")) {
                    existingClient.setLastname((String) clientDetails.get("lastname"));
                }
                if (clientDetails.containsKey("docnumber")) {
                    existingClient.setDocnumber((Integer) clientDetails.get("docnumber"));
                }
                if (clientDetails.containsKey("age")) {
                    existingClient.setAge((Integer) clientDetails.get("age"));
                }

                Client updatedClient = clientsRepository.save(existingClient);
                return new ResponseEntity<>(updatedClient, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception exception) {
            System.err.println("Error updating client: " + exception.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/auth/drop/{clid}")
    @Operation(
            summary = "Remove a Client",
            tags = {"Clients paths definitions"},
            description = "Using the required Id, delete a specific Client.",
            parameters = @Parameter(name = "clid", description = "ID of the client to be deleted", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Client deleted successfully", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Client deleted successfully"))),
                    @ApiResponse(responseCode = "404", description = "Client not found", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Client not found"))),
                    @ApiResponse(responseCode = "400", description = "Invalid client ID", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Invalid client ID"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "Internal server error")))
            }
    )
    public ResponseEntity<?> deleteClient(
            @Parameter(description = "ID of the client to be deleted")
            @PathVariable("clid") Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid client ID");
        }
        try {
            Optional<Client> clientOptional = clientsRepository.findById(id);
            if (clientOptional.isPresent()) {
                clientsRepository.deleteById(id);
                return ResponseEntity.ok("Client deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Client not found");
            }
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
        }
    }
}
