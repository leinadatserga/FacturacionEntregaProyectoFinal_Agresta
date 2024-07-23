package com.commerce.abm.controllers;

import com.commerce.abm.entities.Cart;
import com.commerce.abm.entities.Client;
import com.commerce.abm.entities.Invoice;
import com.commerce.abm.services.ClientsService;
import com.commerce.abm.services.InvoicesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/invoices")
@Tag(name = "Invoices paths definitions", description = "CRUD of Invoice class controller")
public class InvoicesController {
    @Autowired
    private InvoicesService invoicesService;

    @Autowired
    private ClientsService clientsService;

    @GetMapping
    @Operation(summary = "Return all Invoices", description = "Bring back a list of all Invoices in JSON format")
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        try {
            List<Invoice> invoices = invoicesService.readAllInvoices();
            return new ResponseEntity<>(invoices, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Search an Invoice", description = "Using the required Id, returns a specific Invoice")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable Long id) {
        try {
            Optional<Invoice> invoice = invoicesService.readInvoiceById(id);
            if (invoice.isPresent()) {
                return new ResponseEntity<>(invoice.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    @Operation(summary = "Add a Invoice", description = "Using the required data, create a new Invoice")
    public ResponseEntity<Invoice> createInvoice(@RequestBody Invoice invoice) {
        try {
            Optional<Client> clientOptional = clientsService.readClientById(invoice.getClient().getId());
            if (clientOptional.isPresent()) {
                invoice.setClient(clientOptional.get());
                Invoice savedInvoice = invoicesService.newInvoice(invoice);
                return new ResponseEntity<>(savedInvoice, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a Invoice", description = "Using the required Id, allows the user to make changes by entering new data")
    public ResponseEntity<Invoice> updateInvoice(@PathVariable Long id, @RequestBody Invoice invoiceDetails) {
        try {
            Optional<Invoice> invoiceOptional = invoicesService.readInvoiceById(id);
            if (invoiceOptional.isPresent()) {
                Invoice invoice = invoiceOptional.get();
                invoice.setCreatedAt(invoiceDetails.getCreatedAt());
                invoice.setTotal(invoiceDetails.getTotal());
                invoice.setClient(invoiceDetails.getClient());
                return new ResponseEntity<>(invoicesService.newInvoice(invoice), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove a Invoice", description = "Using the required Id, delete a specific Invoice")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        try {
            Optional<Invoice> invoice = invoicesService.readInvoiceById(id);
            if (invoice.isPresent()) {
                invoicesService.deleteInvoice(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
