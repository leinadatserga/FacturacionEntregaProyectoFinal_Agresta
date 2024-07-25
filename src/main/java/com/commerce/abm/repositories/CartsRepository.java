package com.commerce.abm.repositories;

import com.commerce.abm.entities.Cart;
import com.commerce.abm.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartsRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByClient(Client client);
}
