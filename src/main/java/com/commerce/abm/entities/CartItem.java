package com.commerce.abm.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cart_items")
@NoArgsConstructor @ToString @EqualsAndHashCode
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    @JsonIgnore
    @Getter @Setter private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnore
    @Getter @Setter private Product product;

    @Getter @Setter private Integer quantity;
    @Getter @Setter private Double price;

    @JsonProperty("product_id")
    public Long getProductId() {
        return product.getId();
    }
}
