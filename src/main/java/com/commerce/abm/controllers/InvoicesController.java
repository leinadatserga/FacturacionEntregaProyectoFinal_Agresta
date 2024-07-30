package com.commerce.abm.controllers;

import com.commerce.abm.entities.Invoice;
import com.commerce.abm.services.ClientsService;
import com.commerce.abm.services.InvoicesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @Operation(
            summary = "Return all Invoices",
            description = "Bring back a list of all Invoices in JSON format",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of invoices", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Invoice.class)
                    )),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        try {
            List<Invoice> invoices = invoicesService.readAllInvoices();
            return new ResponseEntity<>(invoices, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/id/{iid}")
    @Operation(
            summary = "Search an Invoice",
            description = "Using the required Id, returns a specific Invoice",
            parameters = @Parameter(name = "iid", description = "ID of the invoice to be retrieved", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved invoice", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Invoice.class)
                    )),
                    @ApiResponse(responseCode = "404", description = "Invoice not found", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    public ResponseEntity<Invoice> getInvoiceById(
            @Parameter(description = "ID of the invoice to be retrieved")
            @PathVariable Long iid) {
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
    @Operation(
            summary = "Get the latest Invoice by Client ID",
            description = "Returns the latest Invoice created for a specific Client",
            parameters = @Parameter(name = "clid", description = "ID of the client to get the latest invoice for", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved latest invoice", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Invoice.class)
                    )),
                    @ApiResponse(responseCode = "404", description = "Client or invoice not found", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    public ResponseEntity<Invoice> getLatestInvoiceByClientId(
            @Parameter(description = "ID of the client to get the latest invoice for")
            @PathVariable Long clid) {
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
    @Operation(
            summary = "Create an Invoice from a Cart",
            description = "Using the cart data and Client id, create a new Invoice",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"clientId\": 1\n" +
                                            "}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Invoice created successfully", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Invoice.class)
                    )),
                    @ApiResponse(responseCode = "400", description = "Client ID is required or invalid", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    public ResponseEntity<Object> createInvoiceFromCart(
            @Parameter(description = "Request body containing the client ID to create an invoice for")
            @RequestBody Map<String, Long> clid) {
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
    @Operation(
            summary = "Update a Invoice",
            description = "Using the required Id, allows the user to make changes by entering new data",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Invoice.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"createdAt\": \"2023-07-30T12:34:56\",\n" +
                                            "  \"total\": 100.00,\n" +
                                            "  \"client\": {\n" +
                                            "    \"id\": 1\n" +
                                            "  }\n" +
                                            "}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Invoice updated successfully", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Invoice.class)
                    )),
                    @ApiResponse(responseCode = "404", description = "Invoice not found", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Invalid data provided", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    public ResponseEntity<Invoice> updateInvoice(
            @Parameter(description = "ID of the client whose invoice is to be updated")
            @PathVariable Long clid,
            @Parameter(description = "Request body containing the new invoice details")
            @RequestBody Invoice invoiceDetails) {
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
    @Operation(
            summary = "Remove a Invoice",
            description = "Using the required Id, delete a specific Invoice",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Invoice deleted successfully", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Invoice not found", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
            }
    )
    public ResponseEntity<Void> deleteInvoice(
            @Parameter(description = "ID of the invoice to be deleted")
            @PathVariable Long id) {
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
