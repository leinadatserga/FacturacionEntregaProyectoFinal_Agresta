package com.commerce.abm.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@NoArgsConstructor @ToString @EqualsAndHashCode
public class Cart {
    @Id
    @Getter @Setter private Long cartId;

    @Getter @Setter private boolean delivered;

    @Getter @Setter private LocalDateTime lastUpdated;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "client_id")
    @JsonIgnore
    @Getter @Setter private Client client;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter @Setter private List<CartItem> items = new ArrayList<>();
}
