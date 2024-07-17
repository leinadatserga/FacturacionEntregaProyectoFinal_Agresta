package com.commerce.abm.controllers;

import com.commerce.abm.entities.Client;
import com.commerce.abm.services.ClientsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(path = "api/v1/clients")
@Tag(name = "Clients paths definitions", description = "CRUD of Client class controller")
public class ClientsController {
    @Autowired
    private ClientsService service;

    @PostMapping(consumes = "application/json", produces = "application/json")
    @Operation(summary = "Add a Client", description = "Using the required data, create a new Client")
    public ResponseEntity<Client> create(@RequestBody Client dataClient) {
        try {
            Client client = service.newClient(dataClient);
            return new ResponseEntity<>(client, HttpStatus.CREATED);
        } catch (Exception exception) {
            System.out.println(exception);
            throw new RuntimeException("Error in Client creation/update");
        }
    }

    @GetMapping()
    @Operation(summary = "Return all Clients", description = "Bring back a list of all Clients in JSON format")
    public ResponseEntity <List<Client>> readAllClients() {
        try {
            List<Client> clients = service.readAllClients();
            return ResponseEntity.ok(clients);
        } catch (Exception exception) {
            System.out.println(exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Search a Client", description = "Using the required Id, returns a specific Client")
    public ResponseEntity<Optional<Client>> readClientsById(@PathVariable("id") Long id) {
        try {
            Optional<Client> client = service.readClientById(id);
            if (client.isPresent()) {
                return ResponseEntity.ok(client);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception exception) {
            System.out.println(exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove a Client", description = "Using the required Id, delete a specific Client")
    public ResponseEntity <Map<String, Object>> deleteClient(@PathVariable Long id) {
        try {
            service.deleteClient(id);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            System.out.println(exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
