package com.commerce.abm.services;

import com.commerce.abm.entities.Cart;
import com.commerce.abm.repositories.CartsRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CartsCleanUpService {
    @Autowired
    private CartsRepository cartRepository;

    @Transactional
    public boolean removeInactiveCarts() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(3);
        List<Cart> inactiveCarts = cartRepository.findByLastUpdatedBefore(cutoffDate);

        if (inactiveCarts.isEmpty()) {
            return false;
        }

        for (Cart cart : inactiveCarts) {
            cartRepository.delete(cart);
        }
        return true;
    }
}
