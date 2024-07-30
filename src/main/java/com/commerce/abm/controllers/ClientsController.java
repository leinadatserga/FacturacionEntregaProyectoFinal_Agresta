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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
            description = "Using the required data, create a new Client",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Client.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"name\": \"Daniel\",\n" +
                                            "  \"lastname\": \"Agresta\",\n" +
                                            "  \"docnumber\": \"12345678\"\n" +
                                            "}"
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Client created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    })
    public ResponseEntity<Client> create(
            @Parameter(description = "Client data to create a new client")
            @RequestBody Client dataClient) {
        try {
            Client client = service.newClient(dataClient);
            return new ResponseEntity<>(client, HttpStatus.CREATED);
        } catch (Exception exception) {
            System.err.println("Error in Client creation: " + exception.getMessage());
            throw new RuntimeException("Error in Client creation/update");
        }
    }

    @PostMapping(path = "/{clid}/new-cart")
    @Operation(
            summary = "Create a New Cart for a Client",
            description = "Creates a new Cart for the specified Client after deleting the old one",
            parameters = @Parameter(name = "clid", description = "ID of the client to create a new cart for", required = true)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cart created successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Client not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Client already has an active cart or other error", content = @Content)
    })
    public ResponseEntity<Object> createNewCartForClient(
            @Parameter(description = "ID of the client to create a new cart for")
            @PathVariable Long clid) {
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
            return new ResponseEntity<>(savedCart, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/auth/clients")
    @Operation(
            summary = "Return all Clients",
            description = "Bring back a list of all Clients in JSON format",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of clients",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Client.class),
                                    examples = @ExampleObject(
                                            value = "[\n" +
                                                    "  {\n" +
                                                    "    \"id\": 1,\n" +
                                                    "    \"name\": \"Daniel\",\n" +
                                                    "    \"lastname\": \"Agresta\",\n" +
                                                    "    \"docnumber\": \"12345678\"\n" +
                                                    "  }\n" +
                                                    "]"
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
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
            description = "Using the required Id, returns a specific Client",
            parameters = @Parameter(name = "clid", description = "ID of the client to be searched", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved client",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Client.class),
                                    examples = @ExampleObject(
                                            value = "{\n" +
                                                    "  \"id\": 1,\n" +
                                                    "  \"name\": \"Daniel\",\n" +
                                                    "  \"lastname\": \"Agresta\",\n" +
                                                    "  \"docnumber\": \"12345678\"\n" +
                                                    "}"
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Client not found", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Invalid client ID", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    public ResponseEntity<Optional<Client>> readClientsById(
            @Parameter(description = "ID of the client to be searched")
            @PathVariable("clid") Long id) {
        try {
            Optional<Client> client = service.readClientById(id);
            if (client.isPresent()) {
                return ResponseEntity.ok(client);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception exception) {
            System.err.println("Error retrieving client by id: " + exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/auth/update/{clid}")
    @Operation(
            summary = "Update a Client",
            description = "Update a client's details using the provided client ID and data",
            parameters = @Parameter(name = "clid", description = "ID of the client to be updated", required = true),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Client.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"name\": \"Mauricio\",\n" +
                                            "  \"lastname\": \"Saravia\",\n" +
                                            "  \"docnumber\": \"87654321\"\n" +
                                            "}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Client updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Client not found", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Invalid client ID or data", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    public ResponseEntity<Client> updateClient(
            @Parameter(description = "ID of the client to be updated")
            @PathVariable("clid") Long clid,
            @Parameter(description = "Client data to update the existing client")
            @RequestBody Client clientDetails) {
        try {
            Optional<Client> clientOptional = clientsRepository.findById(clid);
            if (clientOptional.isPresent()) {
                Client existingClient = clientOptional.get();
                if (clientDetails.getName() != null) {
                    existingClient.setName(clientDetails.getName());
                }
                if (clientDetails.getName() != null) {
                    existingClient.setName(clientDetails.getName());
                }
                if (clientDetails.getLastname() != null) {
                    existingClient.setLastname(clientDetails.getLastname());
                }
                if (clientDetails.getDocnumber() != null) {
                    existingClient.setDocnumber(clientDetails.getDocnumber());
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
            description = "Using the required Id, delete a specific Client.",
            parameters = @Parameter(name = "clid", description = "ID of the client to be deleted", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Client deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Client not found", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Invalid client ID", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    public ResponseEntity<String> deleteClient(
            @Parameter(description = "ID of the client to be deleted")
            @PathVariable("clid") Long id) {
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
