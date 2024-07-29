package com.commerce.abm.repositories;

import com.commerce.abm.entities.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoicesRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByClient_Id(Long clientId);
}
