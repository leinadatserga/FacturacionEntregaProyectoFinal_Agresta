package com.commerce.abm.controllers;

import com.commerce.abm.entities.Cart;
import com.commerce.abm.entities.Client;
import com.commerce.abm.entities.Invoice;
import com.commerce.abm.services.ClientsService;
import com.commerce.abm.services.InvoicesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/invoices")
public class InvoicesController {
    @Autowired
    private InvoicesService invoicesService;

    @Autowired
    private ClientsService clientsService;

    @GetMapping
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        try {
            List<Invoice> invoices = invoicesService.readAllInvoices();
            return new ResponseEntity<>(invoices, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
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
    public ResponseEntity<Invoice> createInvoice(@RequestBody Invoice invoice) {
        try {
            Optional<Client> clientOptional = clientsService.readClientById(invoice.getClient().getId());
            if (clientOptional.isPresent()) {
                invoice.setClient(clientOptional.get());
                for (Cart cart : invoice.getCartDetails()) {
                    cart.setInvoice(invoice);
                }
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
    public ResponseEntity<Invoice> updateInvoice(@PathVariable Long id, @RequestBody Invoice invoiceDetails) {
        try {
            Optional<Invoice> invoiceOptional = invoicesService.readInvoiceById(id);
            if (invoiceOptional.isPresent()) {
                Invoice invoice = invoiceOptional.get();
                invoice.setCreatedAt(invoiceDetails.getCreatedAt());
                invoice.setTotal(invoiceDetails.getTotal());
                invoice.setClient(invoiceDetails.getClient());
                invoice.getCartDetails().clear();
                invoice.getCartDetails().addAll(invoiceDetails.getCartDetails());
                for (Cart cart : invoice.getCartDetails()) {
                    cart.setInvoice(invoice);
                }
                return new ResponseEntity<>(invoicesService.newInvoice(invoice), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
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
