package com.commerce.abm.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "carts")
@NoArgsConstructor @ToString @EqualsAndHashCode
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter private Long cartId;

    @NotNull @Min(value = 1)
    @Getter @Setter private Integer amount;

    @NotNull @Min(value = 0)
    @Getter @Setter private Double price;

    @Getter @Setter private boolean delivered;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnore
    @Getter @Setter private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    @JsonIgnore
    @Getter @Setter private Client client;
}
