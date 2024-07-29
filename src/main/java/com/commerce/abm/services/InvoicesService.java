package com.commerce.abm.services;

import com.commerce.abm.entities.*;
import com.commerce.abm.repositories.CartsRepository;
import com.commerce.abm.repositories.ClientsRepository;
import com.commerce.abm.repositories.InvoicesRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InvoicesService {
    @Autowired
    private InvoicesRepository repository;

    @Autowired
    private CartsRepository cartsRepository;

    @Autowired
    private ClientsRepository clientsRepository;

    @Transactional
    public Invoice createInvoiceForClient(Long clientId) {
        Cart cart = cartsRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found for client ID: " + clientId));

        Invoice invoice = new Invoice();
        invoice.setClient(cart.getClient());
        invoice.setCreatedAt(LocalDateTime.now());
        invoice.setItems(cart.getItems().stream().map(cartItem -> {
            InvoiceItem invoiceItem = new InvoiceItem();
            invoiceItem.setInvoice(invoice);
            invoiceItem.setProduct(cartItem.getProduct());
            invoiceItem.setQuantity(cartItem.getQuantity());
            invoiceItem.setPrice(cartItem.getPrice());
            return invoiceItem;
        }).collect(Collectors.toList()));
        invoice.setTotal(cart.getItems().stream().mapToDouble(CartItem::getPrice).sum());
        Invoice newInvoice = repository.save(invoice);
        cart.setDelivered(true);
        cartsRepository.save(cart);
        return newInvoice;
    }

    public Optional<Invoice> readLatestInvoiceByClientId(Long clientId) {
        Optional<Client> clientOpt = clientsRepository.findById(clientId);
        if (clientOpt.isPresent()) {
            Client client = clientOpt.get();
            if (client.getInvoices().isEmpty()) {
                return Optional.empty();
            } else {
                Invoice latestInvoice = client.getInvoices().get(client.getInvoices().size() - 1);
                return Optional.of(latestInvoice);
            }
        }
        return Optional.empty();
    }

    public Invoice saveInvoice(Invoice invoice) {
        return repository.save(invoice);
    }

    public Optional<Invoice> readInvoiceByClientId(Long clientId) {
        return repository.findByClient_Id(clientId);
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
