package com.commerce.abm.controllers;

import com.commerce.abm.entities.Invoice;
import com.commerce.abm.services.ClientsService;
import com.commerce.abm.services.InvoicesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of invoices"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        try {
            List<Invoice> invoices = invoicesService.readAllInvoices();
            return new ResponseEntity<>(invoices, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/id/{iid}")
    @Operation(summary = "Search an Invoice", description = "Using the required Id, returns a specific Invoice")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable Long iid) {
        try {
            Optional<Invoice> invoice = invoicesService.readInvoiceById(iid);
            if (invoice.isPresent()) {
                return new ResponseEntity<>(invoice.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{clid}")
    @Operation(summary = "Get the latest Invoice by Client ID", description = "Returns the latest Invoice created for a specific Client")
    public ResponseEntity<Invoice> getLatestInvoiceByClientId(@PathVariable Long clid) {
        try {
            Optional<Invoice> invoiceOptional = invoicesService.readLatestInvoiceByClientId(clid);
            if (invoiceOptional.isPresent()) {
                return new ResponseEntity<>(invoiceOptional.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    @Operation(summary = "Create an Invoice from a Cart", description = "Using the cart data and Client id, create a new Invoice")
    public ResponseEntity<Object> createInvoiceFromCart(@RequestBody Map<String, Long> clid) {
        Long clientId = clid.get("clientId");
        if (clientId == null) {
            return ResponseEntity.badRequest().body("Client ID is required");
        }

        try {
            Invoice invoice = invoicesService.createInvoiceForClient(clientId);
            return new ResponseEntity<>(invoice, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{clid}")
    @Operation(summary = "Update a Invoice", description = "Using the required Id, allows the user to make changes by entering new data")
    public ResponseEntity<Invoice> updateInvoice(@PathVariable Long clid, @RequestBody Invoice invoiceDetails) {
        try {
            Optional<Invoice> invoiceOptional = invoicesService.readInvoiceByClientId(clid);
            if (invoiceOptional.isPresent()) {
                Invoice invoice = invoiceOptional.get();
                if (invoiceDetails.getCreatedAt() != null) {
                    invoice.setCreatedAt(invoiceDetails.getCreatedAt());
                }
                if (invoiceDetails.getTotal() != null) {
                    invoice.setTotal(invoiceDetails.getTotal());
                }
                if (invoiceDetails.getClient() != null) {
                    invoice.setClient(invoiceDetails.getClient());
                }

                Invoice updatedInvoice = invoicesService.saveInvoice(invoice);
                return new ResponseEntity<>(updatedInvoice, HttpStatus.OK);
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
