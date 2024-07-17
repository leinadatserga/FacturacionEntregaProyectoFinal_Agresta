package com.commerce.abm.services;

import com.commerce.abm.entities.Invoice;
import com.commerce.abm.repositories.InvoicesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InvoicesService {
    @Autowired
    public InvoicesRepository repository;

    public Invoice newInvoice(Invoice invoice) {
        return repository.save(invoice);
    }

    public List<Invoice> readAllInvoices() {
        return repository.findAll();
    }

    public Optional<Invoice> readInvoiceById(Long id) {
        return repository.findById(id);
    }

    public void deleteInvoice(Long id) {
        repository.deleteById(id);
    }
}
