package com.commerce.abm.services;

import com.commerce.abm.entities.Cart;
import com.commerce.abm.entities.Client;
import com.commerce.abm.repositories.CartsRepository;
import com.commerce.abm.repositories.ClientsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClientsService {
    @Autowired
    private ClientsRepository repository;

    @Autowired
    private CartsRepository cartsRepository;

    @Transactional
    public Client newClient(Client client) {
        if (client.getId() != null) {
            Optional<Client> existingClient = repository.findById(client.getId());
            if (existingClient.isPresent()) { return repository.save(client); }
        }
        Cart cart = new Cart();
        cart.setCartId(client.getId());
        cart.setClient(client);
        cartsRepository.save(cart);
        client.getCarts().add(cart);
        return repository.save(client);
    }

    public List<Client> readAllClients() {
        return repository.findAll();
    }

    public Optional<Client> readClientById(Long id) {
        return repository.findById(id);
    }

    public void deleteClient(Long id) {
        repository.deleteById(id);
    }
}
