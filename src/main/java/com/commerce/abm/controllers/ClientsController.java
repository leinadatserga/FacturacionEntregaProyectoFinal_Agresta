package com.commerce.abm.controllers;

import com.commerce.abm.entities.Cart;
import com.commerce.abm.entities.Client;
import com.commerce.abm.repositories.CartsRepository;
import com.commerce.abm.repositories.ClientsRepository;
import com.commerce.abm.services.ClientsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    @Operation(summary = "Add a Client", description = "Using the required data, create a new Client")
    public ResponseEntity<Client> create(@RequestBody Client dataClient) {
        try {
            Client client = service.newClient(dataClient);
            return new ResponseEntity<>(client, HttpStatus.CREATED);
        } catch (Exception exception) {
            System.err.println("Error in Client creation: " + exception.getMessage());
            throw new RuntimeException("Error in Client creation/update");
        }
    }

    @PostMapping(path = "/{clid}/new-cart")
    @Operation(summary = "Create a New Cart for a Client", description = "Creates a new Cart for the specified Client after deleting the old one")
    public ResponseEntity<Object> createNewCartForClient(@PathVariable Long clid) {
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
    @Operation(summary = "Return all Clients", description = "Bring back a list of all Clients in JSON format")
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
    @Operation(summary = "Search a Client", description = "Using the required Id, returns a specific Client")
    public ResponseEntity<Optional<Client>> readClientsById(@PathVariable("clid") Long id) {
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

    @DeleteMapping("/auth/drop/{clid}")
    @Operation(summary = "Remove a Client", description = "Using the required Id, delete a specific Client")
    public ResponseEntity <Map<String, Object>> deleteClient(@PathVariable("clid") Long id) {
        try {
            service.deleteClient(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Client deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception exception) {
            System.err.println("Error deleting client: " + exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
