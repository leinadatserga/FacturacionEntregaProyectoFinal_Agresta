package com.commerce.abm.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Invoices")
@NoArgsConstructor @ToString @EqualsAndHashCode
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter private Long invoiceId;

    @Getter @Setter private LocalDateTime createdAt;

    @Getter @Setter private Double total;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    @Getter @Setter private Client client;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter @Setter private List<Cart> cartDetails = new ArrayList<>();
}
